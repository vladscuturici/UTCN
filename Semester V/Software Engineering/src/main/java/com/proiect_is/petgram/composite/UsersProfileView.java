package com.proiect_is.petgram.composite;

import com.proiect_is.dataaccess.CommentsDAO;
import com.proiect_is.dataaccess.PetsDAO;
import com.proiect_is.dataaccess.PostsDAO;
import com.proiect_is.dataaccess.UsersDAO;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.component.button.Button;

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
import com.vaadin.flow.server.VaadinConfig;
import com.vaadin.flow.server.VaadinSession;

@Route("profile/:username")
@StyleSheet("context://styles/styles.css")
public class UsersProfileView extends VerticalLayout implements BeforeEnterObserver {

    private UsersDAO usersDAO;

    public UsersProfileView() {
        setClassName("background");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
//        addLogo();
        try {
            usersDAO = new UsersDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addLogo() {
        Image logo = new Image("./images/logo.png", "Logo");
        logo.addClassName("logo");
        logo.setWidth("100px");
        logo.setHeight("auto");
        setHorizontalComponentAlignment(Alignment.START, logo);
        add(logo);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters params = event.getRouteParameters();
        String username = params.get("username").orElse(null);

        if (username != null) {
            try {
                Users user = usersDAO.getUserByUsername(username);
                Users currentUser = VaadinSession.getCurrent().getAttribute(Users.class);

                if (currentUser != null && username.equals(currentUser.getUsername())) {
                    UI.getCurrent().navigate("my-profile");
                } else {
                    updateProfileView(user);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            UI.getCurrent().navigate("login");
        }
    }

    private void updateProfileView(Users user) throws SQLException {
        removeAll();
//        addLogo();
        add(new UserProfileComponent(user));
        try {
            add(new PetListComponent(user));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            add(new FeedComponent(user));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}