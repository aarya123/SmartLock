import logging

from device import Device, generate_id

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

    def __init__(self, server, ):
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
            # TODO fix GCM
            # response = self.server.server.gcm.plaintext_request(params['register'], data={"message": "hi!"})
            # self.log.debug('GCM response: {}'.format(response))
            uid = generate_id()
            output = self.server.server.db_mgr.execute('SELECT * FROM DEVICES')
            return 'Register device'  # TODO
        if 'uid' in params:
            uid = params['uid']
            output = self.server.server.db_mgr.execute('SELECT ID FROM DEVICES WHERE ID={}'.format(uid))
            if len(output) < 1:
                self.log.error('UID [{}] not recognized'.format(uid))
                return 'UID not recognized'
            Device.__init__()
        elif 'doorbell' in params:
            return 'Notify doorbell'  # TODO self.notify_doorbell()

    def notify_doorbell(self, ):
        pass
