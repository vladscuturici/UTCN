from model.Station import Station
from model.repository.Repository import Repository


class StationRepository:
    def __init__(self):
        self.repository = Repository(user='root', password='', host='localhost', database='ps')

    def add_station(self, name):
        sql = "INSERT INTO stations (name) VALUES (%s)"
        self.repository.execute_sql_command(sql, (name,))

    def delete_station(self, station_id):
        sql = "DELETE FROM stations WHERE station_id = %s"
        self.repository.execute_sql_command(sql, (station_id,))

    def search_station(self, station_id):
        sql = "SELECT * FROM stations WHERE station_id = %s"
        result_set = self.repository.fetch_data(sql, (station_id,))
        if result_set:
            return Station(result_set[0]['station_id'], result_set[0]['name'])
        else:
            return None

    def update_station(self, station_id, new_name):
        sql = "UPDATE stations SET name = %s WHERE station_id = %s"
        self.repository.execute_sql_command(sql, (new_name, station_id))

    def get_all_stations(self):
        sql = "SELECT * FROM stations ORDER BY name"
        result_set = self.repository.fetch_data(sql)
        if result_set is None:
            return []
        station_names = [row['name'] for row in result_set]
        return station_names

