import logging

from processing import launch_tcp_server


if __name__ == '__main__':
    logging.info('Launching SmartLock Server...')
    launch_tcp_server()
