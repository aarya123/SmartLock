import logging

from datetime import datetime
from device import generate_id
from gcmclient import PlainTextMessage

from constants import ALREADY_REGISTERED_MSG, LOCKED_DOOR_MSG, REGISTRATION_SUCCESS_MSG, UNLOCKED_DOOR_MSG


def get_params(data):
    param_list = data.split('\n')
    params = {}
    for param in param_list:
        key_value = param.split('=')
        params[key_value[0]] = key_value[1]
    return params


class MessageHandler:
    log = None
    handler = None

    def __init__(self, handler, ):
        self.log = logging.getLogger('MessageHandler')
        self.log.setLevel(logging.DEBUG)
        self.handler = handler

    def __call__(self, msg, ):
        response = self.handle_msg(msg)
        if response:
            self.log.debug('Response: {}'.format(response))
        return response

    def handle_msg(self, msg, ):
        params = get_params(msg)

        # Register new device
        if 'register' in params:
            # Check if already registered
            output = self.handler.server.db_mgr.execute(
                '''
                SELECT ID, GCM_KEY FROM DEVICES WHERE GCM_KEY="{}";
                '''.format(params['register']))
            if len(output) > 0:
                uid = output[0][0]
                self.handler.server.gcm.send(
                    PlainTextMessage(params["register"], {"message": ALREADY_REGISTERED_MSG}))
                return 'registered={}'.format(uid)

            # Register new device
            uid = generate_id(6)
            output = self.handler.server.db_mgr.execute(
                '''
                INSERT INTO DEVICES (ID, GCM_KEY, CREATED_ON, APPROVED)
                VALUES ({}, "{}", "{}", {});
                '''.format(uid, params['register'], datetime.now(), 0)
            )
            self.log.debug('Insert new request for access: {}, {}'.format(uid, output))
            self.handler.server.gcm.send(
                PlainTextMessage(params["register"], {"message": REGISTRATION_SUCCESS_MSG}))
            return 'registered={}'.format(uid)
        elif 'ping' in params:
            return 'pong\nstate={}'.format(self.handler.server.rpi.isLocked)

        # UID Verification
        if 'uid' not in params:
            self.log.error('No device uid sent')
            return 'UID not found'

        try:
            uid = int(params['uid'])
        except TypeError, e:
            self.log.error('Invalid uid={}, {}'.format(params['uid'], e))
            return 'Invalid UID'

        output = self.handler.server.db_mgr.execute('SELECT ID FROM DEVICES WHERE ID={}'.format(uid))
        if len(output) < 1:
            self.log.error('UID [{}] not recognized'.format(uid))
            return 'UID not recognized'

        self.log.info('UID {} RECOGNIZED!!'.format(uid))  # TODO check if approved

        # End-user commands
        if 'lock_door' in params:
            self.handler.server.rpi.lock_door()
            return self.handler.server.notify_all(self.handler.server.rpi.isLocked)
        elif 'unlock_door' in params:
            self.handler.server.rpi.unlock_door()
            return self.handler.server.notify_all(self.handler.server.rpi.isLocked)
