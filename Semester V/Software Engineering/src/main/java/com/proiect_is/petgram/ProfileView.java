package com.proiect_is.petgram;

import com.proiect_is.dataaccess.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.File;

import com.proiect_is.model.Users;
import com.proiect_is.model.Posts;
import com.proiect_is.model.Pets;
import com.proiect_is.dataaccess.*;
import com.proiect_is.model.Notifications;
import com.vaadin.flow.server.VaadinSession;
import org.atmosphere.config.service.Post;
//netstat -ano | findstr :8080
//taskkill /F /PID <pid>


@Route("my-profile")
@StyleSheet("context://styles/styles.css")
public class ProfileView extends VerticalLayout {
    UsersDAO userDAO = new UsersDAO();
    FriendsDAO friendsDAO = new FriendsDAO();

    private NotificationsDAO notificationsDAO;
//    private Button notificationButton;
    public ProfileView() throws SQLException {
        Users user = VaadinSession.getCurrent().getAttribute(Users.class);
        // user = new Users(1, "cameron_burrel@yahoo.com", "CameronBurrel", "cambur");
        setClassName("background");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
//        HorizontalLayout topBar = new HorizontalLayout();
//        topBar.setWidthFull();
//        topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
//        topBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        //addLogo();
        SearchView searchBar = new SearchView();
        searchBar.setWidth("1600px");
        searchBar.setHeight("100px");
        /*notificationButton = new Button("Notifications", event -> {
            UI.getCurrent().navigate("notifications");
            hideNotificationAlert();
        });*/
//        topBar.add(notificationButton);
        add(searchBar);

//        add(topBar);

        addProfilePicture(user);
        addUsername(user);
        try {
            addPetList(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        addFeed(user);
        notificationsDAO = new NotificationsDAO();
        Button openMessageButton = new Button("Open Messages", event -> {
            openMessages(user);

        });
        openMessageButton.addClassName("open-messages-button");
        add(openMessageButton);
        checkForUnreadNotifications();

    }

    private void openMessages(Users user) {
        // Navigate to the MessagesView page with the selected user
        UI.getCurrent().navigate(MessageView.class);
    }

    private void checkForUnreadNotifications() {
        try {
            Users currentUser = VaadinSession.getCurrent().getAttribute(Users.class);
            if (currentUser != null && notificationsDAO.hasUnreadNotifications(currentUser.getUser_id())) {
//                notificationButton.setText("You have unread notifications!");
            } else {
//                notificationButton.setText("Notifications");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Gestionarea erorilor
        }
    }

    private void hideNotificationAlert() {
        try {
            Users currentUser = VaadinSession.getCurrent().getAttribute(Users.class);
            if (currentUser != null) {
                notificationsDAO.markAllAsRead(currentUser.getUser_id());
//                notificationButton.setText("Notificări");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Gestionarea erorilor
        }
    }

    public void addLogo() {
        Image logo = new Image("./images/logo.png", "Logo");
        logo.addClassName("logo");
        logo.setWidth("100px");
        logo.setHeight("auto");
        setHorizontalComponentAlignment(Alignment.START, logo);
        add(logo);
    }
    public void addProfilePicture(Users user) {
        String pathToProfilePicture = "./images/user_" + user.getUser_id() + ".png";
        Image profilePicture = new Image(pathToProfilePicture, "Profile Picture");
        Div imageContainer = new Div(profilePicture);
        imageContainer.addClassName("profile-picture");

        // Edit Profile Picture Button
        Button editProfilePictureButton = new Button("Edit Profile Picture", event -> openEditProfilePictureDialog());
        editProfilePictureButton.addClassName("edit-profile-picture-button");

        HorizontalLayout profileLayout = new HorizontalLayout(imageContainer, editProfilePictureButton);
        profileLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        add(profileLayout);
        setHorizontalComponentAlignment(Alignment.CENTER, profileLayout);
    }

    // Method to open dialog for profile picture editing
    private void openEditProfilePictureDialog() {
        Dialog dialog = new Dialog();
        dialog.add(new H2("Change Profile Picture"));
        Users user = VaadinSession.getCurrent().getAttribute(Users.class);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png");

        Button saveButton = new Button("Save", event -> {
            try {
                saveUserImage(buffer, user.getUser_id());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            UI.getCurrent().getPage().executeJs("window.location.reload();");
            dialog.close();
        });

        VerticalLayout dialogLayout = new VerticalLayout(upload, saveButton);
        dialog.add(dialogLayout);
        dialog.open();
    }
    public void addUsername(Users user) {
        String username = user.getUsername();
        Span usernameLabel = new Span(username);
        usernameLabel.addClassName("username");
        add(usernameLabel);

        String email = user.getEmail();
        Span emailLabel = new Span(email);
        emailLabel.addClassName("user-email");
        add(emailLabel);
        Button viewFriendsButton = new Button("View Friends", click -> onViewFriendsClicked(user));
        viewFriendsButton.setClassName("view-friends-button");
        add(viewFriendsButton);
        setHorizontalComponentAlignment(Alignment.CENTER, usernameLabel, emailLabel);
    }

    private void onViewFriendsClicked(Users user) {
        Dialog friendsDialog = new Dialog();
        friendsDialog.add(new H2("Friends"));

        HorizontalLayout friendsLayout = new HorizontalLayout();
        try {
            List<Integer> friendIds = friendsDAO.getFriendUserIds(user.getUser_id());
            for (Integer friendId : friendIds) {
                Users friend = userDAO.getById(friendId);
                Div friendDiv = new Div();
                friendDiv.add(getUser(friend));
                friendDiv.addClickListener(e -> {
                    friendsDialog.close();
                    UI.getCurrent().navigate("profile/" + friend.getUsername());
                });
                friendDiv.getStyle().set("cursor", "pointer");
                friendsLayout.add(friendDiv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        friendsDialog.add(friendsLayout);
        friendsDialog.open();
    }

    public void addPetList(Users user) throws SQLException {
        HorizontalLayout petList = new HorizontalLayout();

        Image iconImage = new Image("./images/add_pet.png", "Add pet icon");
        iconImage.setHeight("30px");
        Button addPetButton = new Button("Add Pet", iconImage);
        addPetButton.addClassName("add-pet-button");
        addPetButton.addClickListener(e -> openAddPetDialog(user));
        petList.add(addPetButton);

        petList.addClassName("pet-list");
        petList.setWidthFull();
        petList.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        PetsDAO petsDAO = new PetsDAO();
        List<Pets> pets = petsDAO.getPetByUserID(user.getUser_id());
        for(Pets pet : pets) {
            petList.add(getPet(pet));
        }
//        petList.add(getPet(new Pets(1, 1, "Bobo", "Just a little hamster")));
//        petList.add(getPet(new Pets(2, 1, "Albert", "Just a little capybara")));
//        petList.add(getPet(new Pets(3, 1, "Ocho", "Just a little turtle")));
        add(petList);
    }
    private void openAddPetDialog(Users user) {
        Dialog dialog = new Dialog();
        dialog.add(new H2("Add New Pet"));

        VerticalLayout formLayout = new VerticalLayout();
        TextField nameField = new TextField("Pet Name");
        TextArea descriptionArea = new TextArea("Pet Description");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png");

        Button saveButton = new Button("Save", event -> {
            try {
                int petId = savePet(user, nameField.getValue(), descriptionArea.getValue());
                savePetImage(buffer, petId);
                UI.getCurrent().getPage().executeJs("window.location.reload();");
                dialog.close();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });

        formLayout.add(nameField, descriptionArea, upload, saveButton);
        dialog.add(formLayout);
        dialog.open();
    }

    private void savePetImage(MemoryBuffer buffer, int petId) throws IOException {
        String originalFileName = buffer.getFileName();

        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        if (fileExtension.isEmpty()) {
            fileExtension = ".png";
        }

        InputStream inputStream = buffer.getInputStream();

        // Update the target file path to include the file extension
        File imageFolder = new File("src/main/resources/static/images");
        File targetFile = new File(imageFolder, "pet_" + petId + fileExtension);

        //System.out.println("Absolute path: " + targetFile.getAbsolutePath());

        Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private void saveUserImage(MemoryBuffer buffer, int userId) throws IOException {
        String originalFileName = buffer.getFileName();

        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        if (fileExtension.isEmpty()) {
            fileExtension = ".png";
        }

        InputStream inputStream = buffer.getInputStream();

        File imageFolder = new File("src/main/resources/static/images");
        File targetFile = new File(imageFolder, "user_" + userId + fileExtension);

        //System.out.println("Absolute path: " + targetFile.getAbsolutePath());

        Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private int savePet(Users user, String name, String description) throws SQLException {
        PetsDAO petsDAO = new PetsDAO();
        Pets newPet = new Pets(0, user.getUser_id(), name ,description);
        newPet.setUser_id(user.getUser_id());
        newPet.setName(name);
        newPet.setDescription(description);
        petsDAO.addPet(newPet);
        return petsDAO.getIdForPhoto(newPet.getUser_id(), newPet.getName(), newPet.getDescription());
    }
    VerticalLayout getPet(Pets pet) {
        VerticalLayout container = new VerticalLayout();
        container.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        String pathToPetPicture = "./images/pet_" + pet.getPet_id() + ".png";
        Image petPicture = new Image(pathToPetPicture, "Pet Picture");
        Div imageContainer = new Div(petPicture);
        imageContainer.addClassName("pet-profile-picture");

        Span petName = new Span(pet.getName());
        petName.addClassName("pet-name");

        petName.addClickListener(e -> openPetDetailsDialog(pet));

        Button removeButton = new Button("Delete pet");
        removeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        removeButton.addClassName("remove-pet-button");
        removeButton.addClickListener(e -> {
            try {
                removePet(pet);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        HorizontalLayout petInfoContainer = new HorizontalLayout(petName, removeButton);
        petInfoContainer.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        container.add(imageContainer, petInfoContainer);

        return container;
    }

    private void openPetDetailsDialog(Pets pet) {
        Dialog dialog = new Dialog();
        dialog.add(new H2("Pet Details"));

        Image petImage = new Image("./images/pet_" + pet.getPet_id() + ".png", "Pet Image");
        petImage.setWidth("200px");

        Span nameLabel = new Span("Name: " + pet.getName());
        Span descriptionLabel = new Span("Description: " + pet.getDescription());

        VerticalLayout layout = new VerticalLayout();
        layout.add(petImage);
        layout.add(nameLabel);
        layout.add(descriptionLabel);
        dialog.add(layout);
        dialog.open();
    }

    private void removePet(Pets pet) throws SQLException {
        PetsDAO petsDAO = new PetsDAO();
        UsersDAO usersDAO = new UsersDAO();
        PostsDAO postsDAO = new PostsDAO();
        Users user = usersDAO.getById(pet.getUser_id());
        List<Posts> postsList = PostsDAO.getPostsByUserId(pet.getUser_id());
        for(Posts post : postsList) {
            if(post.getPet_id() == pet.getPet_id()) {
                postsDAO.deletePost(post.getId());
            }
        }
        petsDAO.deletePet(pet.getPet_id());

        UI.getCurrent().getPage().executeJs("window.location.reload();");
    }
    public void addFeed(Users user) throws SQLException {
//        Posts firstPost = new Posts(1, 1, 1, "Tiny boy", 0, "NULL", 5, new Timestamp(123, 10, 10, 12, 4, 23, 0));
//        Posts secondPost = new Posts(2, 1, 2, "He's hungry", 0, "NULL", 5, new Timestamp(123, 10, 10, 11, 2, 13, 0));
        VerticalLayout feedLayout = new VerticalLayout();
//        addPost(firstPost, user, new Pets(1, 1, "Bobo", "Just a little hamster"));
//        addPost(secondPost, user, new Pets(2, 1, "Albert", "Just a little capybara"));
        PostsDAO postsDAO = new PostsDAO();
        List<Posts> postList = PostsDAO.getPostsByUserId(user.getUser_id());
        PetsDAO petsDAO = new PetsDAO();
        Button postSomethingButton = new Button("Post Something", event -> {
            try {
                openCreatePostDialog(user);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        postSomethingButton.addClassName("post-something-button");
        feedLayout.add(postSomethingButton);
        for(Posts post : postList) {
            addPost(post, user, petsDAO.getById(post.getPet_id()));
        }
//        Posts firstPost = new Posts(3, 1, 1, String.valueOf(petsDAO.getLatestId()), 0, "NULL", 5, new Timestamp(123, 10, 10, 12, 4, 23, 0));
//        addPost(firstPost, user, new Pets(1, 1, "Bobo", "Just a little hamster"));
        add(feedLayout);
    }

    private void openCreatePostDialog(Users user) throws SQLException {
        Dialog dialog = new Dialog();
        dialog.add(new H2("Create a New Post"));

        VerticalLayout formLayout = new VerticalLayout();

        ComboBox<Pets> petSelect = new ComboBox<>("Select your pet");
        PetsDAO petsDAO = new PetsDAO();
        List<Pets> userPets = petsDAO.getPetByUserID(user.getUser_id());
        petSelect.setItems(userPets);
        petSelect.setItemLabelGenerator(Pets::getName);

        TextArea descriptionArea = new TextArea("Post Description");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/png");

        Button saveButton = new Button("Save", event -> {
            try {
                Posts newPost = savePost(user, petSelect.getValue(), descriptionArea.getValue(), buffer);
                dialog.close();
                UI.getCurrent().getPage().executeJs("window.location.reload();");
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });

        formLayout.add(petSelect, descriptionArea, upload, saveButton);
        dialog.add(formLayout);
        dialog.open();
    }

    private Posts savePost(Users user, Pets selectedPet, String description, MemoryBuffer buffer) throws SQLException, IOException {
        Posts newPost = new Posts();
        newPost.setUser_id(user.getUser_id());
        newPost.setPet_id(selectedPet.getPet_id());
        newPost.setDescription(description);
        newPost.setPost_time(new Timestamp(System.currentTimeMillis()));

        PostsDAO postsDAO = new PostsDAO();
        int postId = postsDAO.addPost(newPost);
        savePostImage(buffer, postId);

        return newPost;
    }

    private void savePostImage(MemoryBuffer buffer, int postId) throws IOException {
        String originalFileName = buffer.getFileName();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        if (fileExtension.isEmpty()) {
            fileExtension = ".png";
        }

        InputStream inputStream = buffer.getInputStream();

        File imageFolder = new File("src/main/resources/static/images");
        File targetFile = new File(imageFolder, "post_" + postId + fileExtension);

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

        Button removePostButton = new Button("Delete post");
        removePostButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        removePostButton.addClassName("remove-post-button");
        removePostButton.addClickListener(e -> {
            try {
                removePost(post);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        postLayout.add(removePostButton);

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
            PostsDAO postsDAO = new PostsDAO();
            postsDAO.likePost(post.getId());
            Users currentUser = VaadinSession.getCurrent().getAttribute(Users.class);
            if (currentUser != null) {
                String notificationText = currentUser.getUsername() + " has liked your post";
                NotificationService.notifyObservers(post.getUser_id(), notificationText);
            }
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

    public class NotificationService {
        public static void notifyObservers(int userId, String notificationText) {
            try {
                NotificationsDAO notificationsDAO = new NotificationsDAO();
                String timestampedNotificationText = notificationText + " at " + new Timestamp(System.currentTimeMillis());
                Notifications notification = new Notifications(0, userId, timestampedNotificationText);
                notificationsDAO.addNotification(notification);
            } catch (SQLException e) {
                e.printStackTrace(); // Handle exception...
            }
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

    /*private void openCommentDialog(Users user, Posts post) {
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

                // Split user info into username and timestamp
                String[] userInfoParts = userInfo.split(" at ");
                if (userInfoParts.length == 2) {
                    String username = userInfoParts[0];
                    String timestamp = userInfoParts[1];

                    // Create a Div to group the comment information
                    Div commentDiv = new Div();
                    commentDiv.getStyle().set("border", "1px solid #cccccc");
                    commentDiv.getStyle().set("padding", "10px"); // Set padding
                    commentDiv.getStyle().set("margin-bottom", "10px"); // Set margin
                    commentDiv.getStyle().set("width", "250px"); // Set width
                    commentDiv.getStyle().set("maxWidth", "100%"); // Set maxWidth to make it responsive

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
    }*/

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



    private CommentsDAO commentsDAO =new CommentsDAO();
    private List<String> fetchExistingComments(Posts post) {
        return commentsDAO.getByPostId(post.getId());
    }
    private void saveComment(Posts post, String commentText) {
        Users user = VaadinSession.getCurrent().getAttribute(Users.class);
        commentsDAO.addComment(post.getId(), user.getUser_id(), commentText);
        Users currentUser = VaadinSession.getCurrent().getAttribute(Users.class);
        if (currentUser != null) {
            commentsDAO.addComment(post.getId(), currentUser.getUser_id(), commentText);

            // Adaugă logica de notificare
            String notificationText = currentUser.getUsername() + " commented on your post";
            NotificationService.notifyObservers(post.getUser_id(), notificationText);
        }
    }
    private void refreshComments(VerticalLayout commentsLayout, Posts post) {
        commentsLayout.removeAll();

        List<String> existingComments = fetchExistingComments(post);
        for (String comment : existingComments) {
            commentsLayout.add(new Span(comment));
        }
    }


    private void removePost(Posts post) throws SQLException {
        PostsDAO postsDAO = new PostsDAO();
        postsDAO.deletePost(post.getId());
        UI.getCurrent().getPage().executeJs("window.location.reload();");
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
}
