from model.repository.UserRepository import UserRepository


class AuthenticationPresenter:
    def __init__(self, iview):
        self.iview = iview
        self.user_repository = UserRepository()

    def authenticate_user(self):
        username = self.iview.get_username()
        password = self.iview.get_password()

        user = self.user_repository.search_user(username)
        if user and user.password == password:
            if user.type == "admin":
                self.iview.logged_in_administrator()
            else:
                self.iview.logged_in_employee()
        else:
            self.iview.display_error()
