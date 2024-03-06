package com.proiect_is.dataaccess;


import com.proiect_is.connection.ConnectionFactory;
import com.proiect_is.model.Comments;
import com.vaadin.flow.component.html.Pre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentsDAO {

    private Connection connection;
    public CommentsDAO () throws SQLException {
        connection= ConnectionFactory.createConnection();
    }

    public Connection getConnection() throws SQLException{
        return ConnectionFactory.createConnection();
    }

    public void addComment(int post_id, int user_id, String comment_text) {
        String query = "INSERT INTO comments (post_id, user_id, comment_text, comment_post_time) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, post_id);
            statement.setInt(2, user_id);
            statement.setString(3, comment_text);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int commentId = generatedKeys.getInt(1);
                    System.out.println("Inserted comment with ID: " + commentId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /*public List<String> getByPostId(int post_id) {
        List<String> comments = new ArrayList<>();
        String query = "SELECT comment_text FROM comments WHERE post_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, post_id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                comments.add(resultSet.getString("comment_text"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments;
    }*/

    public List<String> getByPostId(int post_id) {
        List<String> comments = new ArrayList<>();
        String query = "SELECT c.comment_text, u.username, c.comment_post_time " +
                "FROM comments c " +
                "JOIN users u ON c.user_id = u.user_id " +
                "WHERE c.post_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, post_id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String commentText = resultSet.getString("comment_text");
                String username = resultSet.getString("username");
                Timestamp timestamp = resultSet.getTimestamp("comment_post_time");

                // Assuming you want to format the timestamp as a string
                String formattedTimestamp = timestamp.toString();

                // Combine the information into a single string and add to the list
                String commentInfo = String.format("%s by %s at %s", commentText, username, formattedTimestamp);
                comments.add(commentInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments;
    }



    public void removeByPostId(int post_id) throws SQLException {
        String query="DELETE FROM comments WHERE post_id=?";
        try (PreparedStatement statement =connection.prepareStatement(query)){
            statement.setInt(1,post_id);
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public String getUserNameByCommentId(int commentId) {
        String query = "SELECT u.username " +
                "FROM users u " +
                "JOIN comments c ON c.user_id = u.user_id " +
                "WHERE c.comment_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, commentId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Comments> getCommentsWithInfoByPostId(int post_id) {
        List<Comments> comments = new ArrayList<>();
        String query = "SELECT c.comment_id, c.comment_text, c.comment_post_time, u.username " +
                "FROM comments c " +
                "JOIN users u ON c.user_id = u.user_id " +
                "WHERE c.post_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, post_id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int commentId = resultSet.getInt("comment_id");
                String commentText = resultSet.getString("comment_text");
                Timestamp commentPostTime = resultSet.getTimestamp("comment_post_time");
                String userName = resultSet.getString("username");

                Comments commentInfo = new Comments(commentId, commentText, commentPostTime, userName);
                comments.add(commentInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comments;
    }






}
