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

        self.log.info('Opening connection')
        self.db_name = db_name
        self.db_conn = sqlite3.connect(db_name)
        self.db_cursor = self.db_conn.cursor()

        get_tables = self.execute('SELECT name FROM sqlite_master WHERE type="table";')
        self.log.debug('Active Tables: {}'.format(get_tables))

        if len(get_tables) < 1 or 'DEVICES' not in get_tables[0]:
            self.create_devices_table()
            self.db_conn.commit()
            self.log.debug('Created DEVICES table')

    def create_devices_table(self):
        return self.execute(
                '''
                    CREATE TABLE DEVICES(
                        ID          INT     PRIMARY KEY     NOT NULL,
                        GCM_KEY     TEXT    NOT NULL,
                        CREATED_ON  TEXT    NOT NULL,
                        APPROVED    INT     NOT NULL
                    );
                '''
            )

    def execute(self, sql):
        self.log.debug('Execute: {}'.format(sql))
        output = self.db_cursor.execute(sql).fetchall()
        self.log.debug('Execute complete {}'.format(output))
        return output

    def __del__(self, ):
        if self.db_conn:
            self.log.info('Closing connection')
            self.db_conn.close()
