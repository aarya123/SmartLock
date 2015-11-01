import logging
import socket


class MessageSender:
    log = None
    server = None

    def __init__(self, server, ):
        self.log = logging.getLogger('MessageSender')
        self.log.setLevel(logging.DEBUG)
        self.server = server

    def __call__(self, msg, ):
        self.log.info('msg_response: {}'.format(msg))
        try:
            self.server.wfile.write(msg)
            self.log.debug('Sent msg')
        except socket.error, e:
            self.log.error('Failed to send response: {}'.format(e))

    def __del__(self, ):
        self.log.debug('Closing write socket')
        try:
            if not self.server.wfile.closed:
                self.server.wfile.flush()
                self.server.wfile.close()
        except socket.error, e:
            self.log.error('Failed to close socket: {}'.format(e))
