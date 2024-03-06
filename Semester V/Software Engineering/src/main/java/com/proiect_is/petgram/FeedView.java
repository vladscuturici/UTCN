package com.proiect_is.petgram;

import com.proiect_is.dataaccess.*;
import com.proiect_is.petgram.composite.FeedComponent;
import com.proiect_is.petgram.composite.PostComponent;
import com.proiect_is.petgram.patterns.ExistingImageDisplayStrategy;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.proiect_is.model.Users;
import com.proiect_is.model.Posts;
import com.proiect_is.model.Pets;

@Route("feed")
@StyleSheet("context://styles/styles.css")
public class FeedView extends VerticalLayout {

    private com.proiect_is.petgram.patterns.ImageDisplayStrategy imageDisplayStrategy = new ExistingImageDisplayStrategy();

    private UsersDAO userDAO = new UsersDAO();

    public FeedView() throws SQLException {
        Users user = VaadinSession.getCurrent().getAttribute(Users.class);

        setClassName("background");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        SearchView searchBar = new SearchView();
        searchBar.setWidth("1600px");
        searchBar.setHeight("100px");

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.add(searchBar);
        searchLayout.setWidthFull();
        searchLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        add(searchLayout);

        setClassName("background");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        addFeed(user);
    }

    public void addFeed(Users user) throws SQLException {
        VerticalLayout feedLayout = new VerticalLayout();

        FriendsDAO friendsDAO = new FriendsDAO();
        List<Integer> friendUserIds = friendsDAO.getFriendUserIds(user.getUser_id());

        PostsDAO postsDAO = new PostsDAO();
        List<Posts> friendsPosts = postsDAO.getFriendsPosts(friendUserIds);
        PetsDAO petsDAO = new PetsDAO();

        for (Posts post : friendsPosts) {
            Users postUser = postsDAO.getPostUser(post.getUser_id());
            addPost(post, postUser, petsDAO.getById(post.getPet_id()));
//            add(new PostComponent(post, postUser, petsDAO.getById(post.getPet_id())));
        }

        add(feedLayout);
    }

    private void savePostImage(MemoryBuffer buffer, int postId) throws IOException {
        String originalFileName = buffer.getFileName();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        if (fileExtension.isEmpty()) {
            fileExtension = ".png";
        }

        InputStream inputStream = buffer.getInputStream();

        java.io.File imageFolder = new java.io.File("src/main/resources/static/images/");
        java.io.File targetFile = new java.io.File(imageFolder, "post_" + postId + fileExtension);

        Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    void addPost(Posts post, Users user, Pets pet) {
        VerticalLayout postLayout = new VerticalLayout();
        postLayout.addClassName("post-layout");
        postLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        postLayout.add(getPosters(user, pet));

        Span description = new Span(post.getDescription());
        description.addClassName("description");
        postLayout.add(description);

        postLayout.add(getPostImage(post));

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

        add(postLayout);
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
    private void openCommentDialog(Users user, Posts post) {
        Dialog dialog = new Dialog();
        dialog.add(new H2("Comments"));

        VerticalLayout commentsLayout = new VerticalLayout();

        List<String> existingComments = fetchExistingComments(post);
        for (String comment : existingComments) {
            String[] commentParts = comment.split(" by ");
            if (commentParts.length == 2) {
                String commentText = commentParts[0];
                String userInfo = commentParts[1];

                String[] userInfoParts = userInfo.split(" at ");
                if (userInfoParts.length == 2) {
                    String username = userInfoParts[0];
                    String timestamp = userInfoParts[1];

                    Div commentDiv = new Div();
                    commentDiv.getStyle().set("border", "1px solid #cccccc");
                    commentDiv.getStyle().set("padding", "10px");
                    commentDiv.getStyle().set("margin-bottom", "10px");
                    commentDiv.getStyle().set("width", "250px");
                    commentDiv.getStyle().set("maxWidth", "100%");

                    String pathToUserProfilePicture = "./images/user_" + userDAO.getIdByUsername(username) + ".png";

                    Image userProfilePicture = new Image(pathToUserProfilePicture, "Profile Picture");
                    userProfilePicture.setWidth("25px");
                    commentDiv.add(userProfilePicture);

                    commentDiv.add(new Div(new Span(username)));
                    commentDiv.add(new Div(new Span(commentText)));
                    commentDiv.add(new Div(new Span(timestamp)));

                    commentsLayout.add(commentDiv);
                }
            }
        }

        TextArea commentTextArea = new TextArea("Add a comment");
        Button addCommentButton = new Button("Add Comment", event -> {
            String newComment = commentTextArea.getValue();
            saveComment(post, newComment);
            refreshComments(commentsLayout, post);
        });

        commentsLayout.add(commentTextArea, addCommentButton);
        dialog.add(commentsLayout);
        dialog.open();
    }

    private CommentsDAO commentsDAO = new CommentsDAO();
    private List<String> fetchExistingComments(Posts post) {
        return commentsDAO.getByPostId(post.getId());
    }

    private void saveComment(Posts post, String commentText) {
        Users user = VaadinSession.getCurrent().getAttribute(Users.class);
//        commentsDAO.addComment(post.getId(), user.getUser_id(), commentText);
        Users currentUser = VaadinSession.getCurrent().getAttribute(Users.class);
        if (currentUser != null) {
            commentsDAO.addComment(post.getId(), currentUser.getUser_id(), commentText);

            String notificationText = currentUser.getUsername() + " commented on your post";
            ProfileView.NotificationService.notifyObservers(post.getUser_id(), notificationText);
        }
    }

    private void refreshComments(VerticalLayout commentsLayout, Posts post) {
        commentsLayout.removeAll();
        List<String> existingComments = fetchExistingComments(post);
        for (String comment : existingComments) {
            commentsLayout.add(new Span(comment));
        }
    }

    private Div getPostImage(Posts post) {
        return imageDisplayStrategy.displayImage(post);
    }

    private HorizontalLayout getPosters(Users user, Pets pet) {
        HorizontalLayout posters = new HorizontalLayout();
        posters.add(getUser(user));
        posters.add(getPetPost(pet));
        return posters;
    }

    private VerticalLayout getUser(Users user) {
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

    private VerticalLayout getPetPost(Pets pet) {
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
}