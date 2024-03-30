from model.repository.Repository import Repository

from model.User import User

class UserRepository:
    def __init__(self):
        self.repository = Repository(user='root', password='', host='localhost', database='ps')

    def add_user(self, username, password, usertype):
        sql = "INSERT INTO users (username, password, usertype) VALUES (%s, %s, %s)"
        self.repository.execute_sql_command(sql, (username, password, usertype))

    def delete_user(self, user_id):
        sql = "DELETE FROM users WHERE user_id = %s"
        self.repository.execute_sql_command(sql, (user_id,))

    def search_user(self, username):
        sql = "SELECT * FROM users WHERE username = %s"
        result_set = self.repository.fetch_data(sql, (username,))
        if result_set:

            user_data = result_set[0]
            return User(user_id=user_data['user_id'], username=user_data['username'],
                        password=user_data['password'], type=user_data['usertype'])
        else:
            return None

    def search_user_by_string(self, username):
        sql = "SELECT * FROM users WHERE username LIKE %s"
        param = "%" + username + "%"
        result_set = self.repository.fetch_data(sql, (param,))

        users = []
        for user_data in result_set:
            user = User(user_id=user_data['user_id'], username=user_data['username'],
                        password=user_data['password'], type=user_data['usertype'])
            users.append(user.__str__())

        return users

    def update_user(self, user_id, username=None, password=None, usertype=None):
        updates = []
        params = []
        if username:
            updates.append("username = %s")
            params.append(username)
        if usertype:
            updates.append("usertype = %s")
            params.append(usertype)
        if password:
            updates.append("password = %s")
            params.append(password)
        params.append(user_id)
        sql = f"UPDATE users SET {', '.join(updates)} WHERE user_id = %s"
        self.repository.execute_sql_command(sql, params)

    def get_all_users(self):
        sql = "SELECT * FROM users ORDER BY user_id"
        result_set = self.repository.fetch_data(sql)
        users = [User(user_id=row['user_id'], username=row['username'],
                      password=row['password'], type=row['usertype']) for row in result_set]
        return users

