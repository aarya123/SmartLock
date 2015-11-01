import logging


def get_params(data):
    param_list = data.split('\n')
    params = {}
    for param in param_list:
        key_value = param.split('=')
        params[key_value[0]] = key_value[1]
    return params


class MessageHandler:
    log = None
    server = None

    def __init__(self, server):
        self.log = logging.getLogger('MessageHandler')
        self.log.setLevel(logging.DEBUG)
        self.server = server

    def __call__(self, msg, ):
        response = self.handle_msg(msg)
        if response:
            self.log.debug('Response: {}'.format(response))
        return response

    def handle_msg(self, msg, ):
        params = get_params(msg)
        if 'register' in params:
            response = self.server.server.gcm.plaintext_request(params['register'], data={"message": "hi!"})
            self.log.debug('GCM response: {}'.format(response))
            return 'Register device'  # TODO devices.add(params['register'])
        elif 'doorbell' in params:
            return 'Notify doorbell'  # TODO self.notify_doorbell()
        elif 'lock_door' in params:
            return self.server.rpi.lock_door()
        elif 'unlock_door' in params:
            return self.server.rpi.unlock_door()

    def notify_doorbell(self, ):
        pass
