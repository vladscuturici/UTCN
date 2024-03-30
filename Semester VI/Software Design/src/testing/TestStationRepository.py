import unittest
from unittest.mock import patch, MagicMock
from model.Station import Station
from model.repository.Repository import Repository
from model.repository.StationRepository import StationRepository


class TestStationRepository(unittest.TestCase):

    @patch('model.repository.StationRepository.Repository')
    def setUp(self, mock_repository):

        self.mock_repository = mock_repository.return_value
        self.station_repository = StationRepository()

    def test_add_station(self):
        station_name = "Central"
        self.station_repository.add_station(station_name)
        sql_command = "INSERT INTO stations (name) VALUES (%s)"
        self.mock_repository.execute_sql_command.assert_called_with(sql_command, (station_name,))

    def test_delete_station(self):
        station_id = 1
        self.station_repository.delete_station(station_id)
        sql_command = "DELETE FROM stations WHERE station_id = %s"
        self.mock_repository.execute_sql_command.assert_called_with(sql_command, (station_id,))

    def test_search_station(self):
        station_id = 1
        mock_return_value = [{'station_id': 1, 'name': 'Central'}]
        self.mock_repository.fetch_data.return_value = mock_return_value

        expected_station = Station(1, 'Central')
        actual_station = self.station_repository.search_station(station_id)

        sql_command = "SELECT * FROM stations WHERE station_id = %s"
        self.mock_repository.fetch_data.assert_called_with(sql_command, (station_id,))
        self.assertEqual(expected_station.station_id, actual_station.station_id)
        self.assertEqual(expected_station.name, actual_station.name)

    def test_update_station(self):
        station_id = 1
        new_name = "Downtown"
        self.station_repository.update_station(station_id, new_name)
        sql_command = "UPDATE stations SET name = %s WHERE station_id = %s"
        self.mock_repository.execute_sql_command.assert_called_with(sql_command, (new_name, station_id))

    def test_get_all_stations(self):
        mock_return_value = [{'name': 'Central'}, {'name': 'Downtown'}]
        self.mock_repository.fetch_data.return_value = mock_return_value

        expected_station_names = ['Central', 'Downtown']
        actual_station_names = self.station_repository.get_all_stations()

        sql_command = "SELECT * FROM stations ORDER BY name"
        self.mock_repository.fetch_data.assert_called_with(sql_command)
        self.assertEqual(expected_station_names, actual_station_names)


if __name__ == '__main__':
    unittest.main()
