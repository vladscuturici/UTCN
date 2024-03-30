class User:
    def __init__(self, user_id, username, password, type):
        self._user_id = user_id
        self._username = username
        self._password = password
        self._type = type

    @property
    def user_id(self):
        return self._user_id

    @user_id.setter
    def user_id(self, value):
        self._user_id = value

    @property
    def username(self):
        return self._username

    @username.setter
    def username(self, value):
        self._username = value

    @property
    def password(self):
        return self._password

    @password.setter
    def password(self, value):
        self._password = value

    @property
    def type(self):
        return self._type

    @type.setter
    def type(self, value):
        if value not in ['admin', 'user']:
            raise ValueError("type must be 'admin' or 'user'.")
        self._type = value

    def __str__(self):
        return f"User ID: {self._user_id}, username: {self._username}, password: {self._password}, type: {self._type}"
