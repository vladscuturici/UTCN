import unittest
from unittest.mock import patch, MagicMock
from model.repository.Repository import Repository
from model.User import User
from model.repository.UserRepository import UserRepository

class TestUserRepository(unittest.TestCase):

    @patch('model.repository.UserRepository.Repository')
    def setUp(self, mock_repository):
        self.mock_repository = mock_repository.return_value
        self.user_repository = UserRepository()

    def test_add_user(self):
        username, password, usertype = "test_user", "test_pass", "admin"
        self.user_repository.add_user(username, password, usertype)
        sql = "INSERT INTO users (username, password, usertype) VALUES (%s, %s, %s)"
        self.mock_repository.execute_sql_command.assert_called_with(sql, (username, password, usertype))

    def test_delete_user(self):
        user_id = 1
        self.user_repository.delete_user(user_id)
        sql = "DELETE FROM users WHERE user_id = %s"
        self.mock_repository.execute_sql_command.assert_called_with(sql, (user_id,))

    def test_search_user(self):
        username = "test_user"
        mock_return_value = [{'user_id': 1, 'username': username, 'password': 'test_pass', 'usertype': 'admin'}]
        self.mock_repository.fetch_data.return_value = mock_return_value

        expected_user = User(user_id=1, username=username, password='test_pass', type='admin')
        actual_user = self.user_repository.search_user(username)

        sql = "SELECT * FROM users WHERE username = %s"
        self.mock_repository.fetch_data.assert_called_with(sql, (username,))
        self.assertEqual(expected_user.username, actual_user.username)
        self.assertEqual(expected_user.password, actual_user.password)
        self.assertEqual(expected_user.type, actual_user.type)

    def test_update_user(self):
        user_id = 1
        new_username = "updated_user"
        self.user_repository.update_user(user_id, username=new_username)
        sql = "UPDATE users SET username = %s WHERE user_id = %s"
        self.mock_repository.execute_sql_command.assert_called_with(sql, [new_username, user_id])

    def test_search_user_by_string(self):
        partial_username = "user"
        mock_return_value = [
            {'user_id': 1, 'username': 'user1', 'password': 'pass1', 'usertype': 'type1'},
            {'user_id': 2, 'username': 'user2', 'password': 'pass2', 'usertype': 'type2'}
        ]
        self.mock_repository.fetch_data.return_value = mock_return_value

        expected_users_str = [
            User(user_id=1, username='user1', password='pass1', type='type1').__str__(),
            User(user_id=2, username='user2', password='pass2', type='type2').__str__()
        ]
        actual_users_str = self.user_repository.search_user_by_string(partial_username)

        sql = "SELECT * FROM users WHERE username LIKE %s"
        self.mock_repository.fetch_data.assert_called_with(sql, (f"%{partial_username}%",))
        self.assertEqual(expected_users_str, actual_users_str)

    def test_get_all_users(self):
        mock_return_value = [
            {'user_id': 1, 'username': 'user1', 'password': 'pass1', 'usertype': 'type1'},
            {'user_id': 2, 'username': 'user2', 'password': 'pass2', 'usertype': 'type2'}
        ]
        self.mock_repository.fetch_data.return_value = mock_return_value

        expected_users = [
            User(user_id=1, username='user1', password='pass1', type='type1'),
            User(user_id=2, username='user2', password='pass2', type='type2')
        ]
        actual_users = self.user_repository.get_all_users()

        sql = "SELECT * FROM users ORDER BY user_id"
        self.mock_repository.fetch_data.assert_called_with(sql)
        for expected, actual in zip(expected_users, actual_users):
            self.assertEqual(expected.username, actual.username)
            self.assertEqual(expected.password, actual.password)
            self.assertEqual(expected.type, actual.type)

if __name__ == '__main__':
    unittest.main()
