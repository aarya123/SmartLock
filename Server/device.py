from random import randint


def generate_id(length):
    start = int('1' + '0' * (length-1))
    end = int('9' + '9' * (length-1))
    return randint(start, end)


class Device:
    id = 0
    approved = 0
    created_on = None
    gcm_key = None

    def __init__(self, device_id):
        self.id = device_id

    def approve(self):
        self.approved = 1


