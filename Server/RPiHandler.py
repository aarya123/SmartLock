import logging
import time
from threading import Lock

import RPi.GPIO as GPIO

from constants import LOCKED, UNLOCKED


class RPiHandler:
    postive_pin = 0
    negative_pin = 0
    pwm_pin = 0
    pwm = None
    lock = None
    log = None
    isLocked = None

    def __init__(self, positive=20, negative=16, pwm_pin=21):
        self.log = logging.getLogger('RPiHandler')
        self.log.setLevel(logging.DEBUG)
        self.isLocked = LOCKED
        self.postive_pin = positive
        self.negative_pin = negative
        self.pwm_pin = pwm_pin
        self.lock = Lock()
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(self.postive_pin, GPIO.OUT)
        GPIO.setup(self.negative_pin, GPIO.OUT)
        GPIO.setup(self.pwm_pin, GPIO.OUT)
        self.pwm = GPIO.PWM(self.pwm_pin, 1000)

    def __del__(self):
        GPIO.output(self.postive_pin, 0)
        GPIO.output(self.negative_pin, 0)
        GPIO.output(self.pwm_pin, 0)
        self.pwm.stop()
        GPIO.cleanup()

    def lock_door(self, ):
        self.lock.acquire()
        if self.isLocked:
            self.lock.release()
            return "Done!"
        self.log.info('Locking door...')
        GPIO.output(self.postive_pin, 0)
        GPIO.output(self.negative_pin, 1)
        self.pwm.start(100)
        time.sleep(.5)
        GPIO.output(self.postive_pin, 0)
        GPIO.output(self.negative_pin, 0)
        self.pwm.stop()
        self.isLocked = LOCKED
        self.lock.release()
        self.log.info('Locking door complete')
        return "Done!"

    def unlock_door(self, ):
        self.lock.acquire()
        if not self.isLocked:
            self.lock.release()
            return "Done!"
        self.log.info('Unlocking door...')
        GPIO.output(self.postive_pin, 1)
        GPIO.output(self.negative_pin, 0)
        self.pwm.start(100)
        time.sleep(.5)
        GPIO.output(self.postive_pin, 0)
        GPIO.output(self.negative_pin, 0)
        self.pwm.stop()
        self.isLocked = UNLOCKED
        self.lock.release()
        self.log.info('Unlocking door complete')
        return "Done!"
