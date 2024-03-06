package com.proiect_is.dataaccess;

import com.proiect_is.connection.ConnectionFactory;
import com.proiect_is.model.Posts;
import com.proiect_is.model.Users;
import com.vaadin.flow.component.html.Pre;
import org.apache.catalina.User;
import org.apache.catalina.startup.ConnectorCreateRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendsDAO {

    private Connection connection;
    public FriendsDAO () throws SQLException{
        connection= ConnectionFactory.createConnection();
    }

    public Connection getConnection() throws SQLException{
        return ConnectionFactory.createConnection();
    }

    public void addFriend(int userId, int friendId) {
        String query = "INSERT INTO friends (user1_id, user2_id) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, friendId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeFriend(int user_id, int friend_id) {
        String query = "DELETE FROM friends WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, user_id);
            statement.setInt(2, friend_id);
            statement.setInt(3, friend_id);
            statement.setInt(4, user_id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getFriendUserIds(int userId) {
        List<Integer> friendUserIds = new ArrayList<>();
        // Query to check both user1_id and user2_id columns
        String query = "SELECT CASE " +
                "WHEN f.user1_id = ? THEN f.user2_id " +
                "WHEN f.user2_id = ? THEN f.user1_id " +
                "END AS friend_id " +
                "FROM friends f " +
                "WHERE f.user1_id = ? OR f.user2_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            statement.setInt(3, userId);
            statement.setInt(4, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                friendUserIds.add(resultSet.getInt("friend_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendUserIds;
    }

    public boolean isFriendsWith(int userId1, int userId2) {
        String query = "SELECT 1 FROM friends WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId1);
            statement.setInt(2, userId2);
            statement.setInt(3, userId2);
            statement.setInt(4, userId1);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Posts> getFriendsPosts(List<Integer> friendUserIds) {
        List<Posts> friendsPosts = new ArrayList<>();
        String query = "SELECT * FROM posts WHERE user_id IN (";

        for (int i = 0; i < friendUserIds.size(); i++) {
            query += "?";
            if (i < friendUserIds.size() - 1) {
                query += ",";
            }
        }

        query += ") ORDER BY post_time DESC";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < friendUserIds.size(); i++) {
                statement.setInt(i + 1, friendUserIds.get(i));
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int postId = resultSet.getInt("id");
                int userId = resultSet.getInt("user_id");
                // Alte câmpuri pentru Posts...
                String description = resultSet.getString("description");
                // Timestamp, etc.

                Posts post = new Posts();
                friendsPosts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendsPosts;
    }
}