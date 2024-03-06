package com.proiect_is.dataaccess;

import com.proiect_is.connection.ConnectionFactory;
import com.proiect_is.model.Messages;
import org.springframework.stereotype.Component;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.proiect_is.model.*;
import com.proiect_is.connection.*;

public class MessagesDAO {

    private Connection connection;
    public MessagesDAO () throws SQLException {
        connection= ConnectionFactory.createConnection();
    }

    public Connection getConnection() throws SQLException{
        return ConnectionFactory.createConnection();
    }

    public void addMessage(Messages message) throws SQLException {
        String query = "INSERT INTO messages (sender_id, receiver_id, message_text, time) VALUES (?, ?, ?,  CURRENT_TIMESTAMP)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, message.getSender_id());
            preparedStatement.setInt(2, message.getReceiver_id());
            preparedStatement.setString(3, message.getMessage_text());

            preparedStatement.executeUpdate();
        }
    }


    public List<Messages> getByUsersIds(int userId1, int userId2) throws SQLException {
        List<Messages> messagesList = new ArrayList<>();
        String query = "SELECT message_id, sender_id, receiver_id, message_text, time " +
                "FROM messages " +
                "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                "ORDER BY time";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId1);
            preparedStatement.setInt(2, userId2);
            preparedStatement.setInt(3, userId2);
            preparedStatement.setInt(4, userId1);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int messageId = resultSet.getInt("message_id");
                    int senderId = resultSet.getInt("sender_id");
                    int receiverId = resultSet.getInt("receiver_id");
                    String content = resultSet.getString("message_text");
                    Timestamp timestamp = resultSet.getTimestamp("time");

                    Messages message = new Messages(messageId, senderId, receiverId, content, timestamp);
                    messagesList.add(message);
                }
            }
        }

        return messagesList;
    }

    public List<Messages> getByReceiverId(int receiverId) throws SQLException {
        List<Messages> messagesList = new ArrayList<>();
        String query = "SELECT message_id, sender_id, receiver_id, message_text, time " +
                "FROM messages " +
                "WHERE receiver_id = ? " +
                "ORDER BY time";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, receiverId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int messageId = resultSet.getInt("message_id");
                    int senderId = resultSet.getInt("sender_id");
                    receiverId = resultSet.getInt("receiver_id");
                    String content = resultSet.getString("message_text");
                    Timestamp timestamp = resultSet.getTimestamp("time");

                    Messages message = new Messages(messageId, senderId, receiverId, content, timestamp);
                    messagesList.add(message);
                }
            }
        }

        return messagesList;
    }
    public List<Messages> getByReceiverAndSender(int receiverId, int senderId) throws SQLException {
        List<Messages> messagesList = new ArrayList<>();
        String query = "SELECT message_id, sender_id, receiver_id, message_text, time " +
                "FROM messages " +
                "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                "ORDER BY time";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, senderId);
            preparedStatement.setInt(2, receiverId);
            preparedStatement.setInt(3, receiverId);
            preparedStatement.setInt(4, senderId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int messageId = resultSet.getInt("message_id");
                    int retrievedSenderId = resultSet.getInt("sender_id");
                    int retrievedReceiverId = resultSet.getInt("receiver_id");
                    String content = resultSet.getString("message_text");
                    Timestamp timestamp = resultSet.getTimestamp("time");

                    Messages message = new Messages(messageId, retrievedSenderId, retrievedReceiverId, content, timestamp);
                    messagesList.add(message);
                }
            }
        }

        return messagesList;
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
}