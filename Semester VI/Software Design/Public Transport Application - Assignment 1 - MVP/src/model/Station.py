class Station:
    def __init__(self, station_id, name):
        self._station_id = station_id
        self._name = name

    @property
    def station_id(self):
        return self._station_id

    @station_id.setter
    def station_id(self, value):
        self._station_id = value

    @property
    def name(self):
        return self._name

    @name.setter
    def name(self, value):
        self._name = value

    def __str__(self):
        return f"Station ID {self._station_id}, Name: {self._name}"
