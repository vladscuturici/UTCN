package com.proiect_is.dataaccess;

import com.proiect_is.connection.ConnectionFactory;
import com.proiect_is.model.Stories;
import com.proiect_is.model.Users;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StoriesDAO {

    private static Connection connection;

    public StoriesDAO() throws SQLException {
        connection = ConnectionFactory.createConnection();
    }

    public int addStory(Stories story) throws SQLException {
        int storyId = -1; // Default value indicating failure

        String sql = "INSERT INTO Stories (story_id, user_id, post_time, storyDescription) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setInt(1, story.getStoryId());
        preparedStatement.setInt(2, story.getUserId());
        preparedStatement.setTimestamp(3, story.getPostTime());
        preparedStatement.setString(4, story.getStoryDescription());

        preparedStatement.executeUpdate();

        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            storyId = generatedKeys.getInt(1); // Get the generated story ID
        }

        generatedKeys.close();
        preparedStatement.close();

        return storyId;
    }

    public void updateStoryDescription(int storyId, String newDescription) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Stories SET storyDescription = ? WHERE story_id = ?");
        preparedStatement.setString(1, newDescription);
        preparedStatement.setInt(2, storyId);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void deleteStory(int storyId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Stories WHERE story_id = ?");
        preparedStatement.setInt(1, storyId);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public List<Stories> getAllStories() throws SQLException {
        List<Stories> storiesList = new ArrayList<>();

        String sql = "SELECT * FROM Stories";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            int storyId = resultSet.getInt("story_id");
            int userId = resultSet.getInt("user_id");
            Timestamp postTime = resultSet.getTimestamp("post_time");
            String storyDescription = resultSet.getString("storyDescription");

            Stories story = new Stories(storyId, userId, postTime, storyDescription);
            storiesList.add(story);
        }

        resultSet.close();
        statement.close();

        return storiesList;
    }
    public int getUserIdForStory(int storyId) throws SQLException {
        String sql = "SELECT user_id FROM Stories WHERE story_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, storyId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("user_id");
        }

        resultSet.close();
        preparedStatement.close();

        return -1;
    }
}
