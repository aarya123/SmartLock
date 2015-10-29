import logging
import socket


class MessageSender:
    server = None

    def __init__(self, server, ):
        self.server = server

    def __call__(self, msg, ):
        logging.info('msg_response: {}'.format(msg))
        try:
            self.server.wfile.write(msg)
            logging.debug('Sender - Sent msg')
        except socket.error, e:
            logging.error('Sender - Failed to send response: {}'.format(e))

    def __del__(self, ):
        logging.debug('Sender - Closing write socket')
        try:
            if not self.server.wfile.closed:
                self.server.wfile.flush()
                self.server.wfile.close()
        except socket.error, e:
            logging.error('Sender - Failed to close socket: {}'.format(e))
