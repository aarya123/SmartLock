import logging
import sqlite3


class DatabaseConnector:
    db_conn = None
    db_cursor = None
    db_name = None
    log = None

    def __init__(self, db_name, ):
        self.log = logging.getLogger('DB')
        self.log.setLevel(logging.DEBUG)

        self.log.debug('Opening connection')
        self.db_name = db_name
        self.db_conn = sqlite3.connect(db_name)
        self.db_cursor = self.db_conn.cursor()

    def execute(self, sql, params=None):
        self.log.debug('Execute: {} | Params: {}'.format(sql, params))
        output = self.db_cursor.execute(sql, params)
        self.log.debug('Execute complete {}'.format(output))

    def __del__(self, ):
        if self.db_conn:
            self.log.debug('Closing connection')
            self.db_conn.close()
