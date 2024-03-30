from model.repository.TransportLineRepository import TransportLineRepository
from view.AbstractTravelerView import AbstractTravelerView


class TravelerPresenter:
    def __init__(self, iview):
        self.iview: AbstractTravelerView = iview
        self.transport_line_repo = TransportLineRepository()

    def get_all_transport_lines(self):
        self.iview.reset_table()
        transport_lines = self.transport_line_repo.get_all_transport_lines()
        for line in transport_lines:
            self.iview.set_table(line)

    def get_transport_line_by_number(self):
        self.iview.reset_table()
        line_number = self.iview.get_search_number()
        transport_line = self.transport_line_repo.search_transport_line(line_number)
        if transport_line:
            found = transport_line.__str__()
        else:
            found = "No results found."
        self.iview.set_table(found)
