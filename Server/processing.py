import logging
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer

from MessageHandler.message_handler import MessageHandler
from MessageSender.message_sender import MessageSender


DEFAULT_PROCESS_TIMEOUT = 1000
PORT = 8000

def launch_tcp_server():
    set_logging()
    handler = RequestHandler
    httpd = HTTPServer(("", PORT), handler, bind_and_activate=False)
    httpd.allow_reuse_address = True
    httpd.server_bind()
    httpd.server_activate()
    try:
        httpd.serve_forever()
    except BaseException:
        pass
    httpd.server_close()


def set_logging():
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

    def process_request(self):
        if 'Content-Length' in self.headers:
            data = self.rfile.read(int(self.headers['Content-Length']))
            self.rfile.close()
            logging.debug('msg: {}'.format(data))
            response = MessageHandler(self).__call__(data)
            if response:
                MessageSender(self).__call__(response)
