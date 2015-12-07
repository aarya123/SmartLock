import logging
import os
import subprocess

from scapy.all import ARP, sniff


class DoorbellConnector:
    doorbell_mac_address = None
    log = None
    server = None
    sniffer = None

    def __init__(self, server, doorbell_mac_address):
        self.log = logging.getLogger('Doorbell')
        self.log.setLevel(logging.DEBUG)

        self.log.info('Initializing doorbell arp sniffer process')
        self.server = server

        sudo_request = subprocess.call(["/usr/bin/sudo", "/usr/bin/id"])
        self.log.info('Requested sudo: {}'.format(sudo_request))

        self.doorbell_mac_address = doorbell_mac_address
        self.log.debug('Set doorbell mac address to {}'.format(doorbell_mac_address))

    def sniff_arp(self):
        self.sniffer = sniff(prn=self.arp_display, filter='arp', store=0, count=0)

    def arp_display(self, pkt):
        if pkt.haslayer(ARP):
            if pkt[ARP].op == 1:  # who-has (request)
                if pkt[ARP].psrc == '0.0.0.0':  # ARP Probe
                    device_mac_address = pkt[ARP].hwsrc
                    if device_mac_address == self.doorbell_mac_address:
                        self.log.info('Doorbell pressed')
                        self.server.notify_all('Your doorbell was pressed!')
                        os.system('mpg321 -a bluetooth -g 15 doorbell.mp3')
                    else:
                        self.log.debug('Unknown device probe: {}'.format(device_mac_address))

    def __del__(self):
        self.log.info('Closing arp sniffer')
        return
