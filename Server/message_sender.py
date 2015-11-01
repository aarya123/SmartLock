import logging
import socket


class MessageSender:
    log = None
    handler = None

    def __init__(self, handler, ):
        self.log = logging.getLogger('MessageSender')
        self.log.setLevel(logging.DEBUG)
        self.handler = handler

    def __call__(self, msg, ):
        self.log.info('msg_response: {}'.format(msg))
        try:
            self.handler.wfile.write(msg)
            self.log.debug('Sent msg')
        except socket.error, e:
            self.log.error('Failed to send response: {}'.format(e))

    def __del__(self, ):
        self.log.debug('Closing write socket')
        try:
            if not self.handler.wfile.closed:
                self.handler.wfile.flush()
                self.handler.wfile.close()
        except socket.error, e:
            self.log.error('Failed to close socket: {}'.format(e))
