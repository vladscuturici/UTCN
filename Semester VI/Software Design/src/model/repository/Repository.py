import mysql.connector


class Repository:
    def __init__(self, user, password, host, database):
        self.user = user
        self.password = password
        self.host = host
        self.database = database
        self.cnx = None
        self.cursor = None

    def execute_sql_command(self, sql, params=None):
        try:
            self.open_connection()
            if params:
                self.cursor.execute(sql, params)
            else:
                self.cursor.execute(sql)
            self.cnx.commit()
        except mysql.connector.Error as err:
            print(err)
        finally:
            self.close_connection()

    def open_connection(self):
        self.cnx = mysql.connector.connect(user=self.user, password=self.password,
                                           host=self.host, database=self.database)
        self.cursor = self.cnx.cursor(dictionary=True)

    def close_connection(self):
        if self.cursor is not None:
            self.cursor.close()
        if self.cnx is not None:
            self.cnx.close()

    def fetch_data(self, sql, params=None):
        results = None
        try:
            self.open_connection()
            if params:
                self.cursor.execute(sql, params)
            else:
                self.cursor.execute(sql)
            results = self.cursor.fetchall()
        except mysql.connector.Error as err:
            print(err)
        finally:
            self.close_connection()
            return results
