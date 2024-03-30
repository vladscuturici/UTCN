import mysql.connector
from mysql.connector import errorcode


class CreateDataBaseTables:
    def __init__(self, database_name):
        self.database_name = database_name
        self.config = {
            'user': 'root',
            'password': '',
            'host': 'localhost'
        }

    def create_database(self):
        # Connect without specifying a database to create one
        try:
            conn = mysql.connector.connect(user=self.config['user'], password=self.config['password'],
                                           host=self.config['host'])
            cursor = conn.cursor()
            cursor.execute(f"CREATE DATABASE IF NOT EXISTS {self.database_name}")
            print(f"Database {self.database_name} created successfully!")
        except mysql.connector.Error as err:
            print(f"Failed creating database: {err}")
        finally:
            cursor.close()
            conn.close()

    # def create_table_teacher(self):
    #     # Connect to the newly created database
    #     try:
    #         conn = mysql.connector.connect(user=self.config['user'], password=self.config['password'],
    #                                        host=self.config['host'], database=self.database_name)
    #         cursor = conn.cursor()
    #         sql = ("""
    #             CREATE TABLE IF NOT EXISTS Teacher (
    #                 ID CHAR(13) PRIMARY KEY,
    #                 Name CHAR(20),
    #                 FirstName CHAR(30),
    #                 HighSchool CHAR(50)
    #             )
    #         """)
    #         cursor.execute(sql)
    #         print("Table Teacher created successfully.")
    #     except mysql.connector.Error as err:
    #         print(f"Failed creating table: {err}")
    #     finally:
    #         cursor.close()
    #         conn.close()

    def create_table_users(self):
        # Connect to the newly created database
        try:
            conn = mysql.connector.connect(user=self.config['user'], password=self.config['password'],
                                           host=self.config['host'], database=self.database_name)
            cursor = conn.cursor()
            sql = """
                           CREATE TABLE IF NOT EXISTS users (
                             user_id int(11) NOT NULL AUTO_INCREMENT,
                             username varchar(255) NOT NULL,
                             password varchar(255) NOT NULL,
                             usertype varchar(50) NOT NULL,
                             PRIMARY KEY (user_id)
                           )
                       """
            cursor.execute(sql)
            print("Table Users created successfully.")
        except mysql.connector.Error as err:
            print(f"Failed creating table: {err}")
        finally:
            cursor.close()
            conn.close()

    def create_table_transport_line(self):
        # Connect to the newly created database
        try:
            conn = mysql.connector.connect(user=self.config['user'], password=self.config['password'],
                                           host=self.config['host'], database=self.database_name)
            cursor = conn.cursor()
            sql = """
                CREATE TABLE IF NOT EXISTS transport_line (
                  transport_line_id int(11) NOT NULL AUTO_INCREMENT,
                  route varchar(256) DEFAULT NULL,
                  PRIMARY KEY (transport_line_id)
                )
            """
            cursor.execute(sql)
            print("Table Transport line created successfully.")
        except mysql.connector.Error as err:
            print(f"Failed creating table: {err}")
        finally:
            cursor.close()
            conn.close()

    def create_table_stations(self):
        # Connect to the newly created database
        try:
            conn = mysql.connector.connect(user=self.config['user'], password=self.config['password'],
                                           host=self.config['host'], database=self.database_name)
            cursor = conn.cursor()
            sql = """
                CREATE TABLE IF NOT EXISTS stations (
                  station_id int(11) NOT NULL AUTO_INCREMENT,
                  name varchar(255) NOT NULL,
                  PRIMARY KEY (station_id)
                )
            """
            cursor.execute(sql)
            print("Table Stations created successfully.")
        except mysql.connector.Error as err:
            print(f"Failed creating table: {err}")
        finally:
            cursor.close()
            conn.close()
# Usage
if __name__ == "__main__":
    database_name = "ps_test"
    db_tables = CreateDataBaseTables(database_name)
    db_tables.create_database()
    db_tables.create_table_users()
    db_tables.create_table_transport_line()
    db_tables.create_table_stations()
    # db_tables.create_table_teacher()
