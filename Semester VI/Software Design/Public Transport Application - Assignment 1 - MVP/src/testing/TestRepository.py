import unittest
from unittest.mock import patch, MagicMock

from model.repository.Repository import Repository


class TestRepository(unittest.TestCase):
    @patch('model.repository.Repository.mysql.connector.connect')
    def test_open_connection(self, mock_connect):

        mock_cursor = MagicMock()
        mock_connection = MagicMock()
        mock_connection.cursor.return_value = mock_cursor
        mock_connect.return_value = mock_connection

        repo = Repository(user='test', password='test', host='localhost', database='testdb')
        repo.open_connection()

        mock_connect.assert_called_with(user='test', password='test', host='localhost', database='testdb')
        self.assertIsNotNone(repo.cnx)
        self.assertIsNotNone(repo.cursor)

    @patch('model.repository.Repository.mysql.connector.connect')
    def test_execute_sql_command(self, mock_connect):
        mock_cursor = MagicMock()
        mock_connection = MagicMock()
        mock_connection.cursor.return_value = mock_cursor
        mock_connect.return_value = mock_connection

        repo = Repository(user='test', password='test', host='localhost', database='testdb')
        repo.execute_sql_command("SELECT * FROM users")

        mock_cursor.execute.assert_called_with("SELECT * FROM users")
        mock_connection.commit.assert_called_once()

    @patch('model.repository.Repository.mysql.connector.connect')
    def test_fetch_data(self, mock_connect):
        mock_cursor = MagicMock()
        mock_cursor.fetchall.return_value = [{'id': 1, 'name': 'Test User'}]
        mock_connection = MagicMock()
        mock_connection.cursor.return_value = mock_cursor
        mock_connect.return_value = mock_connection

        repo = Repository(user='test', password='test', host='localhost', database='testdb')
        result = repo.fetch_data("SELECT * FROM users")

        mock_cursor.execute.assert_called_with("SELECT * FROM users")
        self.assertEqual(result, [{'id': 1, 'name': 'Test User'}])


if __name__ == '__main__':
    unittest.main()
