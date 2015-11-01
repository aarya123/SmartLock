import logging

from device import Device, generate_id
from datetime import datetime


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

        # Register new device
        if 'register' in params:
            # TODO fix GCM
            # response = self.server.server.gcm.plaintext_request(params['register'], data={"message": "hi!"})
            # self.log.debug('GCM response: {}'.format(response))
            uid = generate_id()
            output = self.server.server.db_mgr.execute(
                    '''
                    INSERT INTO DEVICES (ID, GCM_KEY, CREATED_ON, APPROVED)
                    VALUES ({}, {}, {}, {});
                    '''.format(uid, params['gcm_key'], datetime.now(), 0)
                )
            self.log.debug('Insert new request for access: {}, {}'.format(uid, output))
            return 'registered={}'.format(uid)

        # UID Verification
        if 'uid' not in params:
            self.log.error('No device uid sent')
            return 'UID not found'

        try:
            uid = int(params['uid'])
        except TypeError, e:
            self.log.error('uid={}, {}'.format(uid, e))
            return 'Invalid UID'

        output = self.server.server.db_mgr.execute('SELECT ID FROM DEVICES WHERE ID={}'.format(uid))
        if len(output) < 1:
            self.log.error('UID [{}] not recognized'.format(uid))
            return 'UID not recognized'

        self.log.info('UID {} RECOGNIZED!!'.format(uid))

        # End-User Functions
        if 'doorbell' in params:
            return 'Notify doorbell'  # TODO self.notify_doorbell()

    def notify_doorbell(self, ):
        pass