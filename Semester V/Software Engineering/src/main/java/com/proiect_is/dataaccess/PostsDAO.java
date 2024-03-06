package com.proiect_is.dataaccess;

import com.proiect_is.connection.ConnectionFactory;
import com.proiect_is.model.*;

import java.awt.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.List;

public class PostsDAO {

    private static Connection connection;
    public PostsDAO() throws SQLException {
        connection=ConnectionFactory.createConnection();
    }

    public int addPost(Posts p) throws SQLException {
        int postId = -1; // Default value indicating failure

        // Create the SQL statement with the RETURN_GENERATED_KEYS option
        String sql = "INSERT INTO posts (user_id, pet_id, description, photo_id, photo_url, like_count, post_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        // Set the parameters for the PreparedStatement
        preparedStatement.setInt(1, p.getUser_id());
        preparedStatement.setInt(2, p.getPet_id());
        preparedStatement.setString(3, p.getDescription());
        preparedStatement.setInt(4, p.getPhoto_id());
        preparedStatement.setString(5, p.getPhoto_url());
        preparedStatement.setInt(6, 0); // Set the initial like count to 0
        preparedStatement.setTimestamp(7, p.getPost_time());

        // Execute the update
        preparedStatement.executeUpdate();

        // Retrieve the generated keys (IDs)
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            postId = generatedKeys.getInt(1); // Get the generated post ID
        }

        // Close resources
        generatedKeys.close();
        preparedStatement.close();

        return postId;
    }



    public void updatePostDescription(int post_id,String Newdescription) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("UPDATE posts SET description=? WHERE post_id=?");
        preparedStatement.setString(1,Newdescription);
        preparedStatement.setInt(2,post_id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void deletePost(int post_id) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM posts WHERE post_id=?");
        preparedStatement.setInt(1,post_id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
    public List<Posts> getAllPosts() throws SQLException{
        ///am nevoie de user_id, de description ,de photo_id, de like_count si de post_time
        List<Posts> posts= new ArrayList<>();
        PreparedStatement preparedStatement=connection.prepareStatement("SELECT user_id,description,photo_id,like_count,post_time FROM posts");
        ResultSet resultSet= preparedStatement.executeQuery();

        while(resultSet.next()){
            int user_id=resultSet.getInt("user_id");
            String description = resultSet.getString("description");
            int photo_id= resultSet.getInt("photo_id");
            int like_count=resultSet.getInt("like_count");
            Timestamp post_time= resultSet.getTimestamp("post_time");
            Posts post=new Posts();
            post.setUser_id(user_id);
            post.setDescription(description);
            post.setPhoto_id(photo_id);
            post.setLike_count(like_count);
            post.setPost_time(post_time);

            posts.add(post);
        }

        return posts;

    }

    public static List<Posts> getPostsByUserId(int userId) throws SQLException {
        List<Posts> userPosts = new ArrayList<>();
        String query = "SELECT * FROM posts WHERE user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int postId = resultSet.getInt("post_id");
                    int user_id = resultSet.getInt("user_id");
                    int pet_id = resultSet.getInt("pet_id");
                    String description = resultSet.getString("description");
                    int photo_id = resultSet.getInt("photo_id");
                    String photo_url = resultSet.getString("photo_url");
                    int like_count = resultSet.getInt("like_count");
                    Timestamp post_time = resultSet.getTimestamp("post_time");

                    // Assuming a Posts constructor: Posts(int postId, int user_id, int pet_id, String description, int photo_id, String photo_url, int like_count, Timestamp post_time)
                    Posts post = new Posts(postId, user_id, pet_id, description, photo_id, photo_url, like_count, post_time);
                    userPosts.add(post);
                }
            }
        }

        return userPosts;
    }

    public void likePost(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE posts SET like_count = like_count + 1 WHERE post_id= ?");
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
    public int getLikeCount(int id) throws SQLException {
        int likeCount = 0;

        String sql = "SELECT like_count FROM posts WHERE post_id= ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    likeCount = resultSet.getInt("like_count");
                }
            }
        }

        return likeCount;
    }
    public void unlikePost(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE posts SET like_count = like_count - 1 WHERE post_id= ?");
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
    public List<Posts> getHomePageFeed(Users user) throws SQLException {
        List<Posts> homePageFeed = new ArrayList<>();
        FriendsDAO friendsDAO = new FriendsDAO();

        // Get friends of the user
        List<Integer> friendUserIds = friendsDAO.getFriendUserIds(user.getUser_id());

        // Include the user's own posts
        friendUserIds.add(user.getUser_id());

        // Retrieve posts from friends in chronological order
        String query = "SELECT * FROM posts WHERE user_id IN (" + String.join(",", Collections.nCopies(friendUserIds.size(), "?")) + ") ORDER BY post_time DESC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < friendUserIds.size(); i++) {
                preparedStatement.setInt(i + 1, friendUserIds.get(i));
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int postId = resultSet.getInt("post_id");
                    int userId = resultSet.getInt("user_id");
                    int petId = resultSet.getInt("pet_id");
                    String description = resultSet.getString("description");
                    int photoId = resultSet.getInt("photo_id");
                    String photoUrl = resultSet.getString("photo_url");
                    int likeCount = resultSet.getInt("like_count");
                    Timestamp postTime = resultSet.getTimestamp("post_time");

                    // Assuming you have a constructor for Posts that includes petId, photoId, and photoUrl
                    Posts post = new Posts(postId, userId, petId, description, photoId, photoUrl, likeCount, postTime);
                    homePageFeed.add(post);
                }
            }
        }

        return homePageFeed;
    }

    public List<Posts> getFriendsPosts(List<Integer> friendUserIds) {
        List<Posts> friendsPosts = new ArrayList<>();

        // Verificare dacă lista de ID-uri prieteni este goală
        if (friendUserIds.isEmpty()) {
            return friendsPosts;
        }

        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM posts WHERE user_id IN (");
        // Adăugare parametri pentru utilizatori
        for (int i = 0; i < friendUserIds.size(); i++) {
            queryBuilder.append("?");
            if (i < friendUserIds.size() - 1) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(")");

        try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
            // Setare valorile pentru parametrii din lista de utilizatori
            for (int i = 0; i < friendUserIds.size(); i++) {
                statement.setInt(i + 1, friendUserIds.get(i));
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                // Construirea obiectului Posts din rezultatele interogării
                Posts post = new Posts();
                post.setId(resultSet.getInt("post_id"));
                post.setUser_id(resultSet.getInt("user_id"));
                post.setPet_id(resultSet.getInt("pet_id"));
                post.setDescription(resultSet.getString("description"));
                post.setPhoto_id(resultSet.getInt("photo_id"));
                post.setPhoto_url(resultSet.getString("photo_url"));
                post.setLike_count(resultSet.getInt("like_count"));
                post.setPost_time(resultSet.getTimestamp("post_time"));

                friendsPosts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friendsPosts;
    }

    public Users getPostUser(int userId) throws SQLException {
        String query = "SELECT * FROM users WHERE user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int postUserId = resultSet.getInt("user_id");
                    String email = resultSet.getString("email");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");

                    return new Users(postUserId, email, username, password);
                } else {
                    return null; // Utilizatorul nu a fost găsit cu user_id-ul specificat
                }
            }
        }
    }
}