package com.proiect_is.petgram;

import com.proiect_is.dataaccess.PetsDAO;
import com.proiect_is.dataaccess.PostsDAO;
import com.proiect_is.dataaccess.StoriesDAO;
import com.proiect_is.dataaccess.UsersDAO;
import com.proiect_is.model.Pets;
import com.proiect_is.model.Posts;
import com.proiect_is.model.Stories;
import com.proiect_is.model.Users;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.VaadinSession;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Route("stories")
@StyleSheet("context://styles/styles.css")
public class StoriesView extends VerticalLayout {
    private Div timeDiv;
    private VerticalLayout storiesLayout = new VerticalLayout();
    private StoriesDAO storiesDAO = new StoriesDAO();
    private UsersDAO usersDAO = new UsersDAO();
    private List<Stories> stories;
    private int currentStoryIndex = 0;
    private Div storyContainer;

    public StoriesView() throws SQLException {
        setClassName("story-background");
        // Existing time display setup
//        timeDiv = new Div();
//        Span time = new Span();
//        timeDiv.add(time);
//        UI.getCurrent().setPollInterval(1000);
//        UI.getCurrent().addPollListener(event -> updateLabel());
//        add(timeDiv);
//        addLogo();
        try {
            addStories();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void addLogo() {
        Image logo = new Image("./images/logo.png", "Logo");
        logo.setWidth("100px");
        logo.setHeight("auto");
        setHorizontalComponentAlignment(Alignment.START, logo);
        add(logo);
    }
    public void addStories() throws SQLException {
        stories = storiesDAO.getAllStories();
        LocalDateTime now = LocalDateTime.now();

        Iterator<Stories> iterator = stories.iterator();
        while (iterator.hasNext()) {
            Stories story = iterator.next();
            LocalDateTime timePosted = story.getPostTime().toLocalDateTime();
            System.out.println(timePosted + " " + now.minusHours(24) + "\n");
            if (timePosted.isBefore(now.minusHours(24))) {
                iterator.remove();
            }
        }
        Users user = VaadinSession.getCurrent().getAttribute(Users.class);
        Button addStoryButton = new Button("Add story", event -> {
            try {
                openAddStoryDialog(user);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        HorizontalLayout buttonLayout = new HorizontalLayout(addStoryButton);
        buttonLayout.addClassName("center-top"); // Apply the custom CSS class
        add(buttonLayout);
        storyContainer = new Div();
        storyContainer.addClassName("story-container");
        add(storyContainer);

        if (!stories.isEmpty()) {
            updateStoryDisplay();
        }

        Button leftArrow = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        leftArrow.addClickListener(e -> {
            try {
                navigateStories(-1);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        Button rightArrow = new Button(new Icon(VaadinIcon.ARROW_RIGHT));
        rightArrow.addClickListener(e -> {
            try {
                navigateStories(1);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        HorizontalLayout navigationLayout = new HorizontalLayout(leftArrow, storyContainer, rightArrow);
        navigationLayout.expand(storyContainer);
        navigationLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        navigationLayout.setSizeFull();

        add(navigationLayout);
    }

    private void navigateStories(int direction) throws SQLException {
        currentStoryIndex += direction;
        if (currentStoryIndex < 0) {
            currentStoryIndex = stories.size() - 1;
        } else if (currentStoryIndex >= stories.size()) {
            currentStoryIndex = 0;
        }
        updateStoryDisplay();
    }

    private void updateStoryDisplay() throws SQLException {
        storyContainer.removeAll();
        Stories currentStory = stories.get(currentStoryIndex);
        Div storyDiv = getStory(currentStory.getStoryId());
        storyContainer.add(storyDiv);
    }
    public Div getStory(int id) throws SQLException {
        Div story = new Div();
        String pathToStoryPicture = "./images/story_" + id + ".png";
        Image storyPicture = new Image(pathToStoryPicture, "Story Picture");
        Div imageContainer = new Div(storyPicture);
        imageContainer.addClassName("story-picture");

        VerticalLayout profilePicture = addProfilePicture(storiesDAO.getUserIdForStory(id));

        Stories currentStory = stories.get(currentStoryIndex);
        LocalDateTime timePosted = currentStory.getPostTime().toLocalDateTime();
        long hoursAgo = Duration.between(timePosted, LocalDateTime.now()).toHours();
        long minutesAgo = Duration.between(timePosted, LocalDateTime.now()).toMinutes();
        long secondsAgo = Duration.between(timePosted, LocalDateTime.now()).toSeconds();
        String timeAgo;
        if(hoursAgo > 0)
            timeAgo = hoursAgo + " hours ago";
        else
            if(minutesAgo > 0)
                timeAgo = minutesAgo + " minutes ago";
            else
                timeAgo = secondsAgo + " seconds ago";
        Span timeLabel = new Span(timeAgo);
        timeLabel.addClassName("story-time-label");

        Span descriptionLabel = new Span(currentStory.getStoryDescription());
        descriptionLabel.addClassName("story-description-label");

        story.add(imageContainer, profilePicture, timeLabel, descriptionLabel);
        story.addClassName("story");

        return story;
    }
    private void updateLabel() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        timeDiv.setText(currentTime.format(formatter));
        timeDiv.addClassName("time");
    }
    public VerticalLayout addProfilePicture(int userId) throws SQLException {
        VerticalLayout profileLayout = new VerticalLayout();
        profileLayout.addClassName("profile-layout");

        String pathToProfilePicture = "./images/user_" + userId + ".png";
        Image profilePicture = new Image(pathToProfilePicture, "Profile Picture");
        Div imageContainer = new Div(profilePicture);
        imageContainer.addClassName("story-profile-picture");

        Users user = usersDAO.getById(userId);
        Span usernameLabel = new Span(user.getUsername());
        usernameLabel.addClassName("username-label");

        profileLayout.add(imageContainer, usernameLabel);
        return profileLayout;
    }

    private void openAddStoryDialog(Users user) throws SQLException {
        Dialog dialog = new Dialog();
        dialog.add(new H2("Add your story"));

        VerticalLayout formLayout = new VerticalLayout();

        TextArea descriptionArea = new TextArea("Story Description");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/png");

        Button saveButton = new Button("Save", event -> {
            try {
                Stories newStory = saveStory(user, descriptionArea.getValue(), buffer);
                dialog.close();
                UI.getCurrent().getPage().executeJs("window.location.reload();");
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });

        formLayout.add(descriptionArea, upload, saveButton);
        dialog.add(formLayout);
        dialog.open();
    }

    private Stories saveStory(Users user, String description, MemoryBuffer buffer) throws SQLException, IOException {
        // Create a new post object
        Stories story = new Stories();
        story.setStoryDescription(description);
        story.setPostTime(new Timestamp(System.currentTimeMillis()));
        story.setUserId(user.getUser_id());

        int storyId = storiesDAO.addStory(story);
        saveStoryImage(buffer, storyId);

        return story;
    }

    private void saveStoryImage(MemoryBuffer buffer, int storyId) throws IOException {
        String originalFileName = buffer.getFileName();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        if (fileExtension.isEmpty()) {
            fileExtension = ".png";
        }

        InputStream inputStream = buffer.getInputStream();

        File imageFolder = new File("src/main/resources/static/images/");
        File targetFile = new File(imageFolder, "story_" + storyId + fileExtension);

        Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
