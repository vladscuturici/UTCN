package com.proiect_is.dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.proiect_is.model.*;
import com.proiect_is.connection.*;
public class UsersDAO {
    ///delete, update, viwe all'(username), insert,
    private Connection connection;
    public UsersDAO() throws  SQLException{
        ///conexiunea la baza de date, arunc o execptie daca nu sa putut efectua conexiunea
        connection=ConnectionFactory.createConnection();
    }

    public Connection getConnection() throws SQLException {
        return ConnectionFactory.createConnection();
    }

    //method to check if the username is already taken when registering
    public boolean usernameExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    public void add_user(Users u) throws  SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("INSERT INTO users (email,username,password) VALUES (?,?,?) ");
        preparedStatement.setString(1,u.getEmail());
        preparedStatement.setString(2,u.getUsername());
        preparedStatement.setString(3,u.getPassword());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void update_user(Users u) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("UPDATE users SET email=?,username=?,password=? WHERE user_id=?");
        preparedStatement.setString(1,u.getEmail());
        preparedStatement.setString(2,u.getUsername());
        preparedStatement.setString(3,u.getPassword());
        preparedStatement.setInt(4,u.getUser_id());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public  void delete_user(int id ) throws  SQLException{
        PreparedStatement preparedStatement= connection.prepareStatement("DELETE FROM users WHERE user_id=?");
        preparedStatement.setInt(1,id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public List<String> getAllUsernames() throws SQLException {
        List<String> usernames = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT username FROM users");
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String username = resultSet.getString("username");
            usernames.add(username);
        }

        resultSet.close();
        preparedStatement.close();
        return usernames;
    }
    public void updatePassword(String username, String oldPassword, String newPassword) throws SQLException {
        if (authenticateUser(username, oldPassword)) {
            String query = "UPDATE users SET password = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, newPassword);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
            }
        } else {
            System.out.println("Parola veche incorectă. Actualizarea parolei a eșuat.");
        }
    }
    public boolean authenticateUser(String username, String password) throws SQLException {
        System.out.println("merge");
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public Users getUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    String email = resultSet.getString("email");
                    String userUsername = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    return new Users(userId, email, userUsername, password);
                } else {
                    return null;
                }
            }
        }
    }
    public Users getById(int id) throws SQLException {
        String query = "SELECT * FROM users WHERE user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    String email = resultSet.getString("email");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");

                    return new Users(userId, email, username, password);
                } else {
                    return null;
                }
            }
        }
    }

    public int getIdByUsername(String username) {
        String query = "SELECT user_id FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<Users> getAllUsers() throws SQLException {
        List<Users> usersList = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String email = resultSet.getString("email");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                Users user = new Users(userId, email, username, password);
                usersList.add(user);
            }
        }

        return usersList;
    }

    public List<String> getByNameSubstring(String search) throws SQLException {
        List<String> usernames = new ArrayList<>();
        String query = "SELECT username FROM users WHERE username LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + search + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    usernames.add(username);
                }
            }
        }

        return usernames;
    }
}



