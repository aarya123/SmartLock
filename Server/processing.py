import logging
import os
import socket
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import sys

from gcm import GCM

from database import DatabaseConnector
from message_handler import MessageHandler
from message_sender import MessageSender


DB_NAME = 'smartlock.db'
DEFAULT_PROCESS_TIMEOUT = 1000
GCM_ENV = 'GCM_KEY'
PORT = 8000


def launch_tcp_server():
    handler = RequestHandler
    database = DatabaseConnector
    httpd = Server(("", PORT), handler, database, bind_and_activate=False)
    httpd.allow_reuse_address = True
    httpd.server_bind()
    httpd.server_activate()
    try:
        httpd.serve_forever()
    except BaseException:
        pass
    httpd.server_close()


class Server(HTTPServer):
    db_mgr = None
    gcm = None
    log = None

    def __init__(self, server_address, RequestHandlerClass, DatabaseManagerClass, bind_and_activate=True):
        self.setup_logging()
        self.setup_database(DatabaseManagerClass, DB_NAME)
        if len(sys.argv) == 2:
            self.log.debug('Got gcm key from cli')
            self.setup_gcm_with_key(sys.argv[1])
        else:
            self.log.debug('Got gcm key from environment')
            self.setup_gcm()
        HTTPServer.__init__(self, server_address, RequestHandlerClass, bind_and_activate)

    def setup_database(self, DatabaseManagerClass, db_name):
        self.db_mgr = DatabaseManagerClass(db_name)

    def setup_gcm(self):
        gcm_key = os.getenv(GCM_ENV)
        if not gcm_key:
            raise Exception('{} environment key not found.'.format(GCM_ENV))
        self.setup_gcm_with_key(gcm_key)

    def setup_gcm_with_key(self, gcm_key):
        self.gcm = GCM(gcm_key)
        self.log.debug('Configured gcm service')

    def setup_logging(self):
        self.log = logging.getLogger('Server')
        self.log.setLevel(logging.DEBUG)
        self.log.debug('Configured logger')

    def __del__(self):
        self.log.warning('Server completed execution')


class RequestHandler(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

    def do_GET(self):
        self.server.log.debug('GET recieved')
        self.process_request()

    def do_POST(self):
        self.server.log.debug('POST recieved')
        self.process_request()

    def process_request(self, ):
        if 'Content-Length' in self.headers:
            content_length = 0
            data = ''

            try:
                content_length = int(self.headers['Content-Length'])
            except TypeError, e:
                self.server.log.error('Content length value does not match type Integer. {}'.format(e))
            if content_length <= 0:
                self.server.error('Invalid content length: {}'.format(content_length))
                return

            try:
                data = self.rfile.read(content_length).strip()
            except TypeError, e:
                self.server.error('Content does not match type String. {}'.format(e))
            if len(data) <= 0:
                self.server.log.error('Data could not be parsed from message.')
                return

            self.server.log.info('msg_received: {}'.format(data))
            response = MessageHandler(self).__call__(data)
            if response:
                MessageSender(self).__call__(response)

    def __del__(self, ):
        self.server.log.debug('Closing read socket')
        try:
            if not self.rfile.closed:
                self.rfile.flush()
                self.rfile.close()
        except socket.error, e:
            self.server.log.error('Failed to close read socket: {}'.format(e))
