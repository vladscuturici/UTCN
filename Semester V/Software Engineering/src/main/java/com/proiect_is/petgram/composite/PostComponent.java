package com.proiect_is.petgram.composite;

import com.proiect_is.dataaccess.CommentsDAO;
import com.proiect_is.dataaccess.LikesDAO;
import com.proiect_is.dataaccess.PostsDAO;
import com.proiect_is.dataaccess.UsersDAO;
import com.proiect_is.model.Pets;
import com.proiect_is.model.Posts;
import com.proiect_is.model.Users;
import com.proiect_is.petgram.ProfileView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.server.VaadinSession;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PostComponent extends VerticalLayout {
    UsersDAO usersDAO;
    CommentsDAO commentsDAO;

    {
        try {
            commentsDAO = new CommentsDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            usersDAO = new UsersDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PostComponent(Posts post, Users user, Pets pet) {
        add(addPost(post, user, pet));
    }
    VerticalLayout addPost(Posts post, Users user, Pets pet) {
        VerticalLayout postLayout = new VerticalLayout();
        postLayout.addClassName("post-layout");
        postLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        postLayout.add(getPosters(user, pet));

        Span description = new Span(post.getDescription());
        description.addClassName("description");
        postLayout.add(description);

        postLayout.add(getPostImage(post));

        // Add comment button
        Button commentButton = new Button("Comment", event -> openCommentDialog(user, post));
        commentButton.addClassName("comment-button");
        postLayout.add(commentButton);

        // Add like button and like count
        Span likeCount = new Span("Likes: " + getLikeCount(post));
        Button likeButton = new Button("Like", event -> {
            try {
                likeButtonPressed(user, post, likeCount);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        likeCount.addClassName("like-count");
        likeButton.addClassName("like-button");
        HorizontalLayout likeLayout = new HorizontalLayout(likeButton, likeCount);
        likeLayout.addClassName("like-count-layout");
        likeLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        postLayout.add(likeLayout);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = post.getPost_time().toLocalDateTime();
        Span dateSpan = new Span(dateTime.format(formatter));
        dateSpan.addClassName("post-date");
        postLayout.add(dateSpan);


        return postLayout;
    }

    private void likeButtonPressed(Users user, Posts post, Span likeCount) throws SQLException {
        LikesDAO likesDAO = new LikesDAO();
        if(!likesDAO.hasLiked(user.getUser_id(), post.getId())) {
            likePost(post, likeCount);
            likesDAO.addLike(user.getUser_id(), post.getId());
        }
        else {
            unlikePost(post, likeCount);
            likesDAO.removeLike(user.getUser_id(), post.getId());
        }
    }

    private void likePost(Posts post, Span likeCountLabel) {
        try {
            Users currentUser = VaadinSession.getCurrent().getAttribute(Users.class);
            if (currentUser != null) {
                String notificationText = currentUser.getUsername() + " has liked your post";
                ProfileView.NotificationService.notifyObservers(post.getUser_id(), notificationText);
            }
            PostsDAO postsDAO = new PostsDAO();
            postsDAO.likePost(post.getId());

            UI.getCurrent().access(() -> {
                int newLikeCount = 0;
                try {
                    newLikeCount = postsDAO.getLikeCount(post.getId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                likeCountLabel.setText("Likes: " + newLikeCount);
                UI.getCurrent().push();
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void unlikePost(Posts post, Span likeCountLabel) {
        try {
            PostsDAO postsDAO = new PostsDAO();
            postsDAO.unlikePost(post.getId());

            UI.getCurrent().access(() -> {
                int newLikeCount = 0;
                try {
                    newLikeCount = postsDAO.getLikeCount(post.getId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                likeCountLabel.setText("Likes: " + newLikeCount);
                UI.getCurrent().push();
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private int getLikeCount(Posts post) {
        try {PostsDAO postsDAO = new PostsDAO();
            return postsDAO.getLikeCount(post.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }


    HorizontalLayout getPosters(Users user, Pets pet) {
        HorizontalLayout posters = new HorizontalLayout();
        posters.add(getUser(user));
        posters.add(getPetPost(pet));
        return posters;
    }

    VerticalLayout getUser(Users user) {
        VerticalLayout container = new VerticalLayout();
        container.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        String pathToUserPicture = "./images/user_" + user.getUser_id() + ".png";
        Image petPicture = new Image(pathToUserPicture, "User small profile picture");

        Div imageContainer = new Div(petPicture);
        imageContainer.addClassName("small-profile-picture");

        Span username = new Span(user.getUsername());
        username.addClassName("post-username");
        container.add(imageContainer, username);
        return container;
    }
    VerticalLayout getPetPost(Pets pet) {
        VerticalLayout container = new VerticalLayout();
        container.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        String pathToUserPicture = "./images/pet_" + pet.getPet_id() + ".png";
        Image petPicture = new Image(pathToUserPicture, "Pet small profile picture");

        Div imageContainer = new Div(petPicture);
        imageContainer.addClassName("small-profile-picture");

        Span username = new Span(pet.getName());
        username.addClassName("post-username");
        container.add(imageContainer, username);
        return container;
    }

    Div getPostImage(Posts post) {
        String pathToPostPicture = "./images/post_" + post.getId() + ".png";
        Image postPicture = new Image(pathToPostPicture, "Post Picture");
        postPicture.addClassName("post-image");

        Div imageContainer = new Div();
        File file = new File(pathToPostPicture);

        //if (file.exists()) {
        imageContainer.add(postPicture);
        //}
        imageContainer.addClassName("post-picture");
        return imageContainer;
    }

    private void openCommentDialog(Users user, Posts post) {
        Dialog dialog = new Dialog();
        dialog.add(new H2("Comments"));

        VerticalLayout commentsLayout = new VerticalLayout();

        // Display existing comments
        List<String> existingComments = fetchExistingComments(post);
        for (String comment : existingComments) {
            // Parse the comment information
            String[] commentParts = comment.split(" by ");
            if (commentParts.length == 2) {
                String commentText = commentParts[0];
                String userInfo = commentParts[1];

                // Split user info into username, timestamp, and user profile picture
                String[] userInfoParts = userInfo.split(" at ");
                if (userInfoParts.length == 2) {
                    String username = userInfoParts[0];
                    String timestamp = userInfoParts[1];
                    //String pathToUserProfilePicture = userInfoParts[2];

                    // Create a Div to group the comment information
                    Div commentDiv = new Div();
                    commentDiv.getStyle().set("border", "1px solid #cccccc");
                    commentDiv.getStyle().set("padding", "10px"); // Set padding
                    commentDiv.getStyle().set("margin-bottom", "10px"); // Set margin
                    commentDiv.getStyle().set("width", "250px"); // Set width
                    commentDiv.getStyle().set("maxWidth", "100%"); // Set maxWidth to make it responsive

                    // Display user profile picture
                    String pathToUserProfilePicture = "./images/user_" + usersDAO.getIdByUsername(username) + ".png";

                    Image userProfilePicture = new Image(pathToUserProfilePicture, "Profile Picture");
                    userProfilePicture.setWidth("25px");
                    commentDiv.add(userProfilePicture);

                    // Add username on one row, comment text on another row, and timestamp on another row
                    commentDiv.add(new Div(new Span(username)));
                    commentDiv.add(new Div(new Span(commentText)));
                    commentDiv.add(new Div(new Span(timestamp)));

                    // Add the framed comment to the layout
                    commentsLayout.add(commentDiv);
                }
            }
        }

        TextArea commentTextArea = new TextArea("Add a comment");
        Button addCommentButton = new Button("Add Comment", event -> {
            String newComment = commentTextArea.getValue();
            // Save the new comment
            saveComment(post, newComment);
            // Refresh comments layout
            refreshComments(commentsLayout, post);
        });

        commentsLayout.add(commentTextArea, addCommentButton);
        dialog.add(commentsLayout);
        dialog.open();
    }
    private List<String> fetchExistingComments(Posts post) {
        return commentsDAO.getByPostId(post.getId());
    }
    private void saveComment(Posts post, String commentText) {
        Users user = VaadinSession.getCurrent().getAttribute(Users.class);
        Users currentUser = VaadinSession.getCurrent().getAttribute(Users.class);
        if (currentUser != null) {
            commentsDAO.addComment(post.getId(), currentUser.getUser_id(), commentText);

            // Adaugă logica de notificare
            String notificationText = currentUser.getUsername() + " commented on your post";
            ProfileView.NotificationService.notifyObservers(post.getUser_id(), notificationText);
        }
    }
    private void refreshComments(VerticalLayout commentsLayout, Posts post) {
        // Clear existing comments
        commentsLayout.removeAll();

        // Fetch and display updated comments
        List<String> existingComments = fetchExistingComments(post);
        for (String comment : existingComments) {
            commentsLayout.add(new Span(comment));
        }
    }
}