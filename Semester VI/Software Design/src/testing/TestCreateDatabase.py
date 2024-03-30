import unittest
from unittest.mock import patch, MagicMock

from model.repository.CreateDatabaseTables import CreateDataBaseTables


class TestCreateDataBaseTables(unittest.TestCase):

    def setUp(self):
        self.patcher = patch('model.repository.CreateDatabaseTables.mysql.connector.connect')
        self.mock_connect = self.patcher.start()

        self.mock_cursor = MagicMock()
        self.mock_connection = MagicMock()
        self.mock_connection.cursor.return_value = self.mock_cursor
        self.mock_connect.return_value = self.mock_connection

        self.db_tables = CreateDataBaseTables("test_db")

    def tearDown(self):
        self.patcher.stop()

    def test_create_database(self):
        self.db_tables.create_database()
        self.mock_cursor.execute.assert_called_with(f"CREATE DATABASE IF NOT EXISTS test_db")
        print(f"Database test_db created successfully!")

    def test_create_table_users(self):
        self.db_tables.create_table_users()

        sql_command = """
                       CREATE TABLE IF NOT EXISTS users (
                         user_id int(11) NOT NULL AUTO_INCREMENT,
                         username varchar(255) NOT NULL,
                         password varchar(255) NOT NULL,
                         usertype varchar(50) NOT NULL,
                         PRIMARY KEY (user_id)
                       )
                   """
        expected_sql_lines = [line.strip() for line in sql_command.strip().splitlines() if line.strip()]
        actual_sql_call_args = self.mock_cursor.execute.call_args[0][0]
        actual_sql_lines = [line.strip() for line in actual_sql_call_args.strip().splitlines() if line.strip()]

        self.assertEqual(expected_sql_lines, actual_sql_lines)
        print("Table Users created successfully.")

    def test_create_table_transport_line(self):
        self.db_tables.create_table_transport_line()

        sql_command = """
            CREATE TABLE IF NOT EXISTS transport_line (
              transport_line_id int(11) NOT NULL AUTO_INCREMENT,
              route varchar(256) DEFAULT NULL,
              PRIMARY KEY (transport_line_id)
            )
        """
        expected_sql_lines = [line.strip() for line in sql_command.strip().splitlines() if line.strip()]
        actual_sql_call_args = self.mock_cursor.execute.call_args[0][0]
        actual_sql_lines = [line.strip() for line in actual_sql_call_args.strip().splitlines() if line.strip()]

        self.assertEqual(expected_sql_lines, actual_sql_lines)
        print("Table Transport line created successfully.")

    def test_create_table_stations(self):
        self.db_tables.create_table_stations()

        sql_command = """
            CREATE TABLE IF NOT EXISTS stations (
              station_id int(11) NOT NULL AUTO_INCREMENT,
              name varchar(255) NOT NULL,
              PRIMARY KEY (station_id)
            )
        """
        expected_sql_lines = [line.strip() for line in sql_command.strip().splitlines() if line.strip()]
        actual_sql_call_args = self.mock_cursor.execute.call_args[0][0]
        actual_sql_lines = [line.strip() for line in actual_sql_call_args.strip().splitlines() if line.strip()]

        self.assertEqual(expected_sql_lines, actual_sql_lines)
        print("Table Stations created successfully.")


if __name__ == '__main__':
    unittest.main()
