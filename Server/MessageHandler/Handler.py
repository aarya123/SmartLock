class MessageHandler():
    handler_queue = None
    send_queue = None

    def __init__(self):
        pass

    def __call__(self, handler_queue, send_queue):
        while 1:
            print "message_handler"
