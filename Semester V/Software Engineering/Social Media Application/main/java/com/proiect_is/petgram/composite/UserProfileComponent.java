package com.proiect_is.petgram.composite;

import com.proiect_is.dataaccess.FriendsDAO;
import com.proiect_is.dataaccess.UsersDAO;
import com.proiect_is.model.Users;
import com.proiect_is.petgram.SearchView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.VaadinApplicationConfiguration;

import java.sql.SQLException;
import java.util.List;

public class UserProfileComponent extends VerticalLayout {
    private Button friendButton;
    private Button viewFriendsButton;
    UsersDAO usersDAO;
    Div userProfileComponent = new Div();

    {
        try {
            usersDAO = new UsersDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    FriendsDAO friendsDAO;

    {
        try {
            friendsDAO = new FriendsDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserProfileComponent(Users user) throws SQLException {
        userProfileComponent.addClassName("user-profile-component");
        SearchView searchBar = new SearchView();
        searchBar.setWidth("1600px");
        searchBar.setHeight("100px");
//        topBar.add(notificationButton);
        add(searchBar);
        addProfilePicture(user);
        Span usernameLabel = new Span(user.getUsername());
        usernameLabel.addClassName("username");

        userProfileComponent.add(usernameLabel);
        setAlignItems(Alignment.CENTER);
        if(!friendsDAO.isFriendsWith(user.getUser_id(), VaadinSession.getCurrent().getAttribute(Users.class).getUser_id())) {
            friendButton = new Button("Add as Friend", click -> onAddFriendClicked(user));
            friendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }
        else {
            friendButton = new Button("Remove Friend", click -> onRemoveFriendClicked(user));
            friendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        }
        userProfileComponent.add(friendButton);
        viewFriendsButton = new Button("View Friends", click -> onViewFriendsClicked(user));
        userProfileComponent.add(viewFriendsButton);
        add(userProfileComponent);
    }

    private void onViewFriendsClicked(Users user) {
        Dialog friendsDialog = new Dialog();
        friendsDialog.add(new H2("Friends"));

        HorizontalLayout friendsLayout = new HorizontalLayout();
        try {
            List<Integer> friendIds = friendsDAO.getFriendUserIds(user.getUser_id());
            for (Integer friendId : friendIds) {
                Users friend = usersDAO.getById(friendId);
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

    private void onRemoveFriendClicked(Users user) {
        Users removingUser = VaadinSession.getCurrent().getAttribute(Users.class);
        friendsDAO.removeFriend(removingUser.getUser_id(), user.getUser_id());
        friendsDAO.removeFriend(user.getUser_id(), removingUser.getUser_id());
        UI.getCurrent().getPage().executeJs("window.location.reload();");
    }

    private void onAddFriendClicked(Users user) {
        Users addingUser = VaadinSession.getCurrent().getAttribute(Users.class);
        friendsDAO.addFriend(addingUser.getUser_id(), user.getUser_id());
        UI.getCurrent().getPage().executeJs("window.location.reload();");
    }

    public void addProfilePicture(Users user) {
        String pathToProfilePicture = "./images/user_" + user.getUser_id() + ".png";
        Image profilePicture = new Image(pathToProfilePicture, "Profile Picture");
        Div imageContainer = new Div(profilePicture);
        imageContainer.addClassName("profile-picture");

        HorizontalLayout profileLayout = new HorizontalLayout(imageContainer);
        profileLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        userProfileComponent.add(profileLayout);
        setHorizontalComponentAlignment(Alignment.CENTER, profileLayout);
    }
}
