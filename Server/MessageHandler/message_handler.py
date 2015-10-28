import logging


def get_params(data):
    param_list = data.split("\n")
    param_list.remove("")
    params = {}
    for param in param_list:
        key_value = param.split("=")
        params[key_value[0]] = key_value[1]
    return params


class MessageHandler:
    server = None

    def __init__(self, server, ):
        self.server = server

    def __call__(self, msg, ):
        logging.debug('Handler - Handle msg')
        response = self.handle_msg(msg)
        if response:
            logging.debug('Handler - Response: {}'.format(response))
        return response

    def handle_msg(self, msg, ):
        params = get_params(msg)
        if 'register' in params:
            logging.info(gcm.plaintext_request(params['register'], data={"message": "hi!"}))
            return 'Register device'  # TODO devices.add(params["register"])
        elif 'doorbell' in params:
            return 'Notify doorbell'  # TODO self.notify_doorbell()

    def notify_doorbell(self):
        pass
