package com.proiect_is.dataaccess;

import com.proiect_is.connection.ConnectionFactory;
import com.proiect_is.model.Notifications;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationsDAO {
    private Connection connection;

    public NotificationsDAO() throws SQLException {
        // Conexiunea la baza de date
        connection = ConnectionFactory.createConnection();
    }

    public List<Notifications> getNotificationsForUser(int userId) throws SQLException {
        List<Notifications> notifications = new ArrayList<>();
        String query = "SELECT * FROM notifications WHERE user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int notificationId = resultSet.getInt("notification_id");
                    String text = resultSet.getString("text");
                    Notifications notification = new Notifications(notificationId, userId, text);
                    notifications.add(notification);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            throw e; // Optionally re-throw the exception
        }

        return notifications;
    }

    public boolean hasUnreadNotifications(int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void markAllAsRead(int userId) throws SQLException {
        String query = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        }
    }

    public void addNotification(Notifications notification) throws SQLException {
        String query = "INSERT INTO notifications (user_id, text) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, notification.getUserId());
            preparedStatement.setString(2, notification.getText());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteNotification(int notificationId) throws SQLException {
        String query = "DELETE FROM notifications WHERE notification_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, notificationId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

}