package com.proiect_is.dataaccess;

import com.proiect_is.connection.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LikesDAO {

    private Connection connection;

    public LikesDAO() throws SQLException {
        connection = ConnectionFactory.createConnection();
    }

    public boolean hasLiked(int userId, int postId) throws SQLException {
        String query = "SELECT 1 FROM likes WHERE user_id = ? AND post_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, postId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        }
    }

    public void addLike(int userId, int postId) throws SQLException {
        System.out.println(userId + " liked " + postId + "\n");
        String query = "INSERT INTO likes (user_id, post_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE post_id = post_id;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, postId);
            statement.executeUpdate();
        }
    }

    public void removeLike(int userId, int postId) throws SQLException {
        String query = "DELETE FROM likes WHERE user_id = ? AND post_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, postId);
            statement.executeUpdate();
        }
    }
}
