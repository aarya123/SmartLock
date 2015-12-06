import logging

from datetime import datetime
from device import generate_id
from gcmclient import PlainTextMessage

from constants import ADMIN_SUCCESS_MSG, APPROVAL_FAILED_MSG, ALREADY_REGISTERED_MSG, REGISTRATION_PENDING_MSG, REGISTRATION_SUCCESS_MSG
from constants import LOCKED, UNLOCKED


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
            # Check if first device to register
            output = self.handler.server.db_mgr.execute(
                '''
                SELECT ID FROM DEVICES;
                '''.format(params['register']))
            if len(output) == 0:
                uid = generate_id(6)
                approved = 1
                output = self.handler.server.db_mgr.execute(
                    '''
                    INSERT INTO DEVICES (ID, GCM_KEY, CREATED_ON, APPROVED)
                    VALUES ({}, "{}", "{}", {});
                    '''.format(uid, params['register'], datetime.now(), approved)
                )
                self.log.debug('New admin assigned: {}, {}'.format(uid, output))
                self.handler.server.gcm.send(
                    PlainTextMessage(params["register"], {"message": ADMIN_SUCCESS_MSG}))
                return 'registered={}\napproved={}'.format(uid, approved)
            # Check if already registered
            output = self.handler.server.db_mgr.execute(
                '''
                SELECT ID, GCM_KEY, APPROVED FROM DEVICES WHERE GCM_KEY="{}";
                '''.format(params['register']))
            if len(output) > 0:
                uid = output[0][0]
                approved = output[0][2]
                if approved:
                    self.handler.server.gcm.send(
                        PlainTextMessage(params["register"], {"message": ALREADY_REGISTERED_MSG}))
                else:
                    self.handler.server.gcm.send(
                        PlainTextMessage(params["register"], {"message": REGISTRATION_PENDING_MSG}))
                return 'registered={}\napproved={}'.format(uid, approved)
            # Register new unknown device
            uid = generate_id(6)
            approved = 0
            output = self.handler.server.db_mgr.execute(
                '''
                INSERT INTO DEVICES (ID, GCM_KEY, CREATED_ON, APPROVED)
                VALUES ({}, "{}", "{}", {});
                '''.format(uid, params['register'], datetime.now(), approved)
            )
            self.log.debug('Insert new request for access: {}, {}'.format(uid, output))
            self.handler.server.gcm.send(
                PlainTextMessage(params["register"], {"message": REGISTRATION_PENDING_MSG}))
            return 'registered={}\napproved={}'.format(uid, approved)

        # Check if UID recognized and if user is approved
        approved = 0
        lock_state = LOCKED
        if 'uid' in params:
            try:
                uid = int(params['uid'])
            except TypeError, e:
                self.log.error('Invalid uid={}, {}'.format(params['uid'], e))
                return 'Invalid UID'
            output = self.handler.server.db_mgr.execute('SELECT ID, APPROVED FROM DEVICES WHERE ID={};'.format(uid))
            if len(output) > 0:
                self.log.info('UID [{}] recognized'.format(uid))
                approved = output[0][1]

        # Return pong if ping recieved
        if 'ping' in params:
            if approved:
                lock_state = self.handler.server.rpi.isLocked
            return 'pong\nstate={}\napproved={}'.format(lock_state, approved)

        # UID Verification
        if 'uid' not in params:
            self.log.error('No device uid sent')
            return 'UID not found'

        if approved:
            # End-user commands
            if 'lock_door' in params:
                self.handler.server.rpi.lock_door()
                return self.handler.server.notify_all(self.handler.server.rpi.isLocked)
            elif 'unlock_door' in params:
                self.handler.server.rpi.unlock_door()
                return self.handler.server.notify_all(self.handler.server.rpi.isLocked)
            elif 'getunapproved' in params:
                uid_list = ''
                output = self.handler.server.db_mgr.execute('SELECT ID FROM DEVICES WHERE APPROVED=0;')
                for index, device in enumerate(output):
                    if index > 0:
                        uid_list += ','
                    uid_list += str(device[0])
                return uid_list
            elif 'approve' in params:
                approval_uid = params['approve']
                self.log.info('Request to approve guest={} from uid={}'.format(uid, approval_uid))
                output = self.handler.server.db_mgr.execute(
                    '''
                    UPDATE DEVICES SET APPROVED=1 WHERE ID={};
                    '''.format(approval_uid))
                if output == 0:
                    self.log.error('Could not approve uid={}: UID not found!'.format(approval_uid))
                    gcm_key = self.handler.server.db_mgr.getgcmkey(uid)
                    self.handler.server.gcm.send(
                        PlainTextMessage(gcm_key, {"message": APPROVAL_FAILED_MSG}))
                    return 'failed'
                output = self.handler.server.db_mgr.execute(
                    '''
                    SELECT GCM_KEY FROM DEVICES WHERE ID={};
                    '''.format(approval_uid))
                gcm_key = output[0][0]
                self.handler.server.gcm.send(
                    PlainTextMessage(gcm_key, {"message": REGISTRATION_SUCCESS_MSG}))
                return 'success'
