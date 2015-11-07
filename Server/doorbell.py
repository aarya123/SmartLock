import logging
import subprocess

from scapy.i2 import ARP
from scapy.sniff


class DoorbellConnector:
    doorbell_mac_address = None
    log = None

    def __init__(self, doorbell_mac_address):
        self.log = logging.getLogger('DOORBELL')
        self.log.setLevel(logging.DEBUG)

        sudo_request = subprocess.call(["/usr/bin/sudo", "/usr/bin/id"])
        self.log.info('Requested sudo: {}'.format(sudo_request))

        self.doorbell_mac_address = doorbell_mac_address
        self.log.debug('Set doorbell mac address to {}'.format(doorbell_mac_address))

        network_sniffer = sniff(prn=self.arp_display, filter='arp', store=0, count=0)

    def arp_display(self, pkt):
        return
        '''
        if pkt[ARP].op == 1:  # who-has (request)
            if pkt[ARP].psrc == '0.0.0.0':  # ARP Probe
                device_mac_address = pkt[ARP].hwsrc
                if device_mac_address == self.doorbell_mac_address:
                    print 'Doorbell pressed'
                else:
                    print 'Unknown device probe: {}'.format(device_mac_address)
        '''

    def __del__(self):
        pass
