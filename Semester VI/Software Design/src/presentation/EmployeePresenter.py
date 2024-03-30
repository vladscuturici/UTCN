from model.repository.StationRepository import StationRepository
from view.AbstractEmployeeView import AbstractEmployeeView
from model.repository.TransportLineRepository import TransportLineRepository
import re


class EmployeePresenter:
    def __init__(self, iview):
        self.iview = iview
        self.transport_line_repo = TransportLineRepository()
        self.station_repo = StationRepository()

    def get_all_transport_lines(self):
        self.iview.reset_listbox()
        transport_lines = self.transport_line_repo.get_all_transport_lines()
        for line in transport_lines:
            self.iview.set_listbox(line)

    def get_transport_line_by_number(self):
        self.iview.reset_listbox()
        line_number = self.iview.get_search_number()
        transport_line = self.transport_line_repo.search_transport_line(line_number)
        if transport_line:
            found = transport_line.__str__()
        else:
            found = "No results found."
        self.iview.set_listbox(found)

    def add_transport_line(self):
        # id = self.iview.get_add_id
        route = self.iview.get_add_route()
        # print(route)
        self.transport_line_repo.add_transport_line(route)
        self.iview.clear_route_entry()

    def delete_transport_line(self):
        id_str = self.iview.get_clicked_row_id()
        digits_str = ''.join(re.findall(r'\d+', id_str))
        tl_id = int(digits_str) if digits_str.isdigit() else None
        # print(tl_id)
        self.transport_line_repo.delete_transport_line(tl_id)

    def update_transport_line(self):
        new_id = self.iview.get_update_id()
        new_route = self.iview.get_update_route()
        # print(new_id)
        # print(new_route)
        self.transport_line_repo.update_transport_line(new_id, new_route)

    def populate_station_values(self):

        list = self.station_repo.get_all_stations()
        self.iview.set_stations(list)
