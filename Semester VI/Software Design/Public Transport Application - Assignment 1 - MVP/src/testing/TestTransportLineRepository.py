import unittest
from unittest.mock import patch, MagicMock
from model.TransportLine import TransportLine
from model.repository.Repository import Repository
from model.repository.TransportLineRepository import TransportLineRepository

class TestTransportLineRepository(unittest.TestCase):

    @patch('model.repository.TransportLineRepository.Repository')
    def setUp(self, mock_repository):

        self.mock_repository = mock_repository.return_value
        self.transport_line_repository = TransportLineRepository()

    def test_add_transport_line(self):
        route = "Route 66"
        self.transport_line_repository.add_transport_line(route)
        sql = "INSERT INTO transport_line (route) VALUES (%s)"
        self.mock_repository.execute_sql_command.assert_called_with(sql, (route,))

    def test_delete_transport_line(self):
        transport_line_id = 1
        self.transport_line_repository.delete_transport_line(transport_line_id)
        sql = "DELETE FROM transport_line WHERE transport_line_id = %s"
        self.mock_repository.execute_sql_command.assert_called_with(sql, (transport_line_id,))

    def test_search_transport_line(self):
        number = 1
        mock_return_value = [{'transport_line_id': number, 'route': 'Route 66'}]
        self.mock_repository.fetch_data.return_value = mock_return_value

        expected_transport_line = TransportLine(number, 'Route 66')
        actual_transport_line = self.transport_line_repository.search_transport_line(number)

        sql = "SELECT * FROM transport_line WHERE transport_line_id = %s"
        self.mock_repository.fetch_data.assert_called_with(sql, (number,))
        self.assertEqual(expected_transport_line.route, actual_transport_line.route)

    def test_update_transport_line(self):
        transport_line_id = 1
        new_route = "New Route"
        self.transport_line_repository.update_transport_line(transport_line_id, new_route)
        sql = "UPDATE transport_line SET route = %s WHERE transport_line_id = %s"
        self.mock_repository.execute_sql_command.assert_called_with(sql, (new_route, transport_line_id))

    def test_get_all_transport_lines(self):
        mock_return_value = [
            {'transport_line_id': 1, 'route': 'Route 66'},
            {'transport_line_id': 2, 'route': 'Route 67'}
        ]
        self.mock_repository.fetch_data.return_value = mock_return_value

        expected_transport_lines = [
            TransportLine(1, 'Route 66'),
            TransportLine(2, 'Route 67')
        ]
        actual_transport_lines = self.transport_line_repository.get_all_transport_lines()

        sql = "SELECT * FROM transport_line ORDER BY transport_line_id"
        self.mock_repository.fetch_data.assert_called_with(sql)
        for expected, actual in zip(expected_transport_lines, actual_transport_lines):
            self.assertEqual(expected.route, actual.route)

if __name__ == '__main__':
    unittest.main()
