import time

import RPi.GPIO as GPIO

__author__ = 'anubhaw'


class RPiHandler:
    postive_pin = 0
    negative_pin = 0
    pwm_pin = 0
    pwm = None

    def __init__(self, positive=20, negative=16, pwm_pin=21):
        self.postive_pin = positive
        self.negative_pin = negative
        self.pwm_pin = pwm_pin
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
        GPIO.output(self.postive_pin, 0)
        GPIO.output(self.negative_pin, 1)
        self.pwm.start(100)
        time.sleep(.5)
        GPIO.output(self.postive_pin, 0)
        GPIO.output(self.negative_pin, 0)
        self.pwm.stop()
        return "Done!"

    def unlock_door(self, ):
        GPIO.output(self.postive_pin, 1)
        GPIO.output(self.negative_pin, 0)
        self.pwm.start(100)
        time.sleep(.5)
        GPIO.output(self.postive_pin, 0)
        GPIO.output(self.negative_pin, 0)
        self.pwm.stop()
        return "Done!"
