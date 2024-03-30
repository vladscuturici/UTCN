from model.TransportLine import TransportLine
from model.repository.Repository import Repository


class TransportLineRepository:
    def __init__(self):
        self.repository = Repository(user='root', password='', host='localhost', database='ps')

    def add_transport_line(self, route):
        sql = "INSERT INTO transport_line (route) VALUES (%s)"
        self.repository.execute_sql_command(sql, (route,))

    def delete_transport_line(self, transport_line_id):
        sql = "DELETE FROM transport_line WHERE transport_line_id = %s"
        self.repository.execute_sql_command(sql, (transport_line_id,))

    def search_transport_line(self, number):
        sql = "SELECT * FROM transport_line WHERE transport_line_id = %s"
        result_set = self.repository.fetch_data(sql, (number,))
        if result_set:
            return TransportLine(result_set[0]['transport_line_id'], result_set[0]['route'])
        else:
            return None

    def update_transport_line(self, transport_line_id, new_route):
        sql = "UPDATE transport_line SET route = %s WHERE transport_line_id = %s"
        self.repository.execute_sql_command(sql, (new_route, transport_line_id))

    def get_all_transport_lines(self):
        sql = "SELECT * FROM transport_line ORDER BY transport_line_id"
        result_set = self.repository.fetch_data(sql)
        transport_lines = [TransportLine(row['transport_line_id'], row['route']) for row in result_set]
        return transport_lines
