package com.proiect_is.petgram;

import com.proiect_is.petgram.FeedView;
import com.proiect_is.petgram.ProfileView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.proiect_is.dataaccess.UsersDAO;
import com.proiect_is.model.Users;

import java.sql.SQLException;
import java.util.List;

@Route("search-bar")
@StyleSheet("context://styles/styles.css")
public class SearchView extends VerticalLayout {
    private ComboBox<String> userSearchSelect;

    public SearchView() throws SQLException {
        addClassName("list-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setWidth("100%");

        HorizontalLayout topBar = new HorizontalLayout();
        topBar.addClassName("top-bar");


        Image logoFeed = new Image("./images/logo.png", "Logo Feed");
        logoFeed.addClassName("logo");
        logoFeed.addClassName("logo-feed");
        logoFeed.addClickListener(event -> UI.getCurrent().navigate(FeedView.class));
        topBar.add(logoFeed);


        addProfileButton(topBar);

        userSearchSelect = new ComboBox<>();
        userSearchSelect.setPlaceholder("Search");
        userSearchSelect.setClearButtonVisible(true);
        userSearchSelect.setAllowCustomValue(true);
        userSearchSelect.setWidth("700px");
        updateUsersList(userSearchSelect, "");
        topBar.add(userSearchSelect);

        // Adăugare logo Chat
        Image logoChat = new Image("./images/logo_chat.png", "Logo Chat");
        logoChat.addClassName("logo-chat");
        logoChat.addClickListener(event -> UI.getCurrent().navigate(MessageView.class));
        topBar.add(logoChat);


        Image logoNot = new Image("./images/logo_notification.png", "Logo Notification");
        logoNot.addClassName("logo-not");
        logoNot.addClickListener(event -> UI.getCurrent().navigate(NotificationsView.class));
        topBar.add(logoNot);

        Image logoStories = new Image("./images/logo_stories.png", "Logo Stories");
        logoStories.addClassName("logo-stories");
        logoStories.addClickListener(event -> UI.getCurrent().navigate(StoriesView.class));
        topBar.add(logoStories);

        Image logologout = new Image("./images/logo_logout.png", "Logo Logout");
        logologout.addClassName("logo-log");
        logologout.addClickListener(event -> UI.getCurrent().navigate(LoginView.class));
        topBar.add(logologout);

        topBar.getStyle().set("width", "100%");

        add(topBar);
    }

    private void updateUsersList(ComboBox<String> userSelect, String searchSubstring) {
        try {
            UsersDAO usersDAO = new UsersDAO();
            List<String> matchingUsernames = usersDAO.getByNameSubstring(searchSubstring);
            userSelect.setItems(matchingUsernames);

            // Add a value change listener to handle user selection
            userSelect.addValueChangeListener(event -> {
                String selectedUsername = event.getValue();
                if (selectedUsername != null && !selectedUsername.isEmpty()) {
                    navigateToUserProfile(selectedUsername);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void navigateToUserProfile(String username) {
        // Navigate to the user's profile page
        UI.getCurrent().navigate("profile/" + username);
    }

    private void addProfileButton(HorizontalLayout parentLayout) {
        Users currentUser = VaadinSession.getCurrent().getAttribute(Users.class);
        String profilePicturePath = getProfilePicturePath(currentUser.getUser_id());

        Image userProfileImage = new Image(profilePicturePath, "User Profile Picture");
        userProfileImage.setWidth("45px");
        userProfileImage.setHeight("42px");
        userProfileImage.addClickListener(event -> UI.getCurrent().navigate(ProfileView.class));

        Div userProfileContainer = new Div(userProfileImage);
        userProfileContainer.addClassName("user-profile-image-container");

        HorizontalLayout userLayout = new HorizontalLayout(userProfileContainer);
        userLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        parentLayout.add(userLayout);
    }


    private String getProfilePicturePath(int userId) {
        return "./images/user_" + userId + ".png";
    }
}