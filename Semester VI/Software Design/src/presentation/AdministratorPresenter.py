from model.repository.UserRepository import UserRepository
from view.AbstractEmployeeView import AbstractEmployeeView
from model.repository.TransportLineRepository import TransportLineRepository
import re


class AdministratorPresenter:
    def __init__(self, iview):
        self.iview = iview
        self.user_repo = UserRepository()

    def get_all_users(self):
        self.iview.reset_listbox()
        users = self.user_repo.get_all_users()
        for line in users:
            self.iview.set_listbox(line)

    def get_user_by_username(self):
        self.iview.reset_listbox()
        username = self.iview.get_search_field()
        users = self.user_repo.search_user_by_string(username)

        if users:
            found = users
        else:
            found = ["No results found."]

        for line in found:
            self.iview.set_listbox(line)

    def add_user(self):
        # id = self.iview.get_add_id
        username = self.iview.get_add_username()
        password = self.iview.get_add_password()
        type2 = self.iview.get_add_type()
        # print(username)
        # print(password)
        # print(type)
        self.user_repo.add_user(username, password, str(type2))

    def delete_user(self):
        id_str = self.iview.get_clicked_row_id()
        digits_str = ''.join(re.findall(r'\d+', id_str))
        tl_id = int(digits_str) if digits_str.isdigit() else None
        # print(tl_id)
        self.user_repo.delete_user(tl_id)

    def update_user(self):
        id2 = self.iview.get_update_id()
        new_username = self.iview.get_update_username()
        new_password = self.iview.get_update_password()
        new_type = str(self.iview.get_update_type())

        # print(new_username)
        # print(new_password)
        # print(new_type)

        self.user_repo.update_user(id2, new_username, new_password, new_type)
