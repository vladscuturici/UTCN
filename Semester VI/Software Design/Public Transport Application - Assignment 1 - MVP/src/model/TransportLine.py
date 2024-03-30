class TransportLine:
    def __init__(self, transport_line_id, route):
        self._transport_line_id = transport_line_id
        self._route = route

    @property
    def transport_line_id(self):
        return self._transport_line_id

    @transport_line_id.setter
    def transport_line_id(self, value):
        self._transport_line_id = value

    @property
    def route(self):
        return self._route

    @route.setter
    def route(self, value):
        self._route = value

    def __str__(self):
        return f"Transport Line  {self._transport_line_id}, with the route : {self._route}"
