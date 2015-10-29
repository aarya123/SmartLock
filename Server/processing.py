import logging
import os
import socket

from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
from gcm import GCM

from MessageHandler.message_handler import MessageHandler
from MessageSender.message_sender import MessageSender


DEFAULT_PROCESS_TIMEOUT = 1000
PORT = 8000

GCM_ENV = 'GCM_KEY'


def launch_tcp_server():
    handler = RequestHandler
    httpd = Server(("", PORT), handler, bind_and_activate=False)
    httpd.allow_reuse_address = True
    httpd.server_bind()
    httpd.server_activate()
    try:
        httpd.serve_forever()
    except BaseException:
        pass
    httpd.server_close()


class Server(HTTPServer):
    gcm = None

    def __init__(self, server_address, RequestHandlerClass, bind_and_activate=True):
        self.setup_logging()
        self.gcm = self.setup_gcm()
        HTTPServer.__init__(self, server_address, RequestHandlerClass, bind_and_activate)

    def setup_gcm(self):
        gcm_key = os.getenv(GCM_ENV)
        if not gcm_key:
            raise Exception('{} environment key not found.'.format(GCM_ENV))
        logging.debug('Configured GCM Service')
        gcm = GCM(gcm_key)
        return gcm

    def setup_logging(self):
        log = logging.getLogger()
        log.setLevel(logging.DEBUG)
        logging.debug('Configured Logger')


class RequestHandler(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

    def do_GET(self):
        logging.debug('GET recieved')
        self.process_request()

    def do_POST(self):
        logging.debug('POST recieved')
        self.process_request()

    def process_request(self, ):
        if 'Content-Length' in self.headers:
            content_length = 0
            data = ''

            try:
                content_length = int(self.headers['Content-Length'])
            except TypeError, e:
                logging.error('Content length value does not match type Integer. {}'.format(e))
            if content_length <= 0:
                logging.error('Invalid content length: {}'.format(content_length))
                return

            try:
                data = self.rfile.read(content_length).strip()
            except TypeError, e:
                logging.error('Content does not match type String. {}'.format(e))
            if len(data) <= 0:
                logging.error('Data could not be parsed from message.')
                return

            logging.debug('msg: {}'.format(data))
            response = MessageHandler(self).__call__(data)
            if response:
                MessageSender(self).__call__(response)

    def __del__(self, ):
        logging.debug('Reader - Closing read socket')
        try:
            if not self.rfile.closed:
                self.rfile.flush()
                self.rfile.close()
        except socket.error, e:
            logging.error('Sender - Failed to close socket: {}'.format(e))
