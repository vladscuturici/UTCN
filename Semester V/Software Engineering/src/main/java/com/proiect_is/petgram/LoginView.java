package com.proiect_is.petgram;

import com.proiect_is.model.Users;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import com.proiect_is.dataaccess.UsersDAO;
import com.vaadin.flow.server.VaadinSession;

import java.net.URL;
import java.sql.SQLException;

@StyleSheet("context://styles/styles.css")
@Route("login")
public class LoginView extends VerticalLayout {

    private UsersDAO usersDAO;
    public LoginView() {
        try {
            this.usersDAO = new UsersDAO();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        setSizeFull(); // Make the root div full size

        getStyle()
                .set("position", "absolute") // Changed from relative to absolute
                .set("top", "0") // Align top
                .set("left", "0") // Align left
                //.set("height", "94vh") // Set the height to viewport height to prevent scrolling
                //.set("width", "95.8vw") // Set the width to viewport width
                .set("overflow", "hidden") // Prevent internal scrolling
                .set("background-image", "url('images/bg.jpg')")
                .set("background-repeat", "no-repeat")
                .set("background-position", "center center")
                .set("background-size", "cover")
                .set("background-attachment", "fixed");

        // Additional styles for demo purposes
        getStyle()
                .set("background-color", "var(--lumo-contrast-5pct)")
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center") // Vertically center the contents
                .set("padding", "var(--lumo-space-l)");

        Image logo = new Image("images/logo.png", "Company logo");
        logo.getStyle()
                .set("position", "absolute")
                .set("left", "30px") // Adjust as needed for positioning from the left
                .set("top", "30px");  // Adjust as needed for positioning from the top
        logo.setWidth("130px"); // Adjust the width as needed
        logo.setHeight("124px"); // Adjust the height as needed (if necessary)
        add(logo);

        Image tree = new Image("images/tree.webp", "Tree photo");
        tree.getStyle()
                .set("position", "absolute")
                .set("left", "170px") // Adjust as needed for positioning from the left
                .set("top", "17px");  // Adjust as needed for positioning from the top
        tree.setWidth("720px"); // Adjust the width as needed
        tree.setHeight("720px"); // Adjust the height as needed (if necessary)
        add(tree);

        Image animals = new Image("images/pets.png", "Pet photo");
        animals.getStyle()
                .set("position", "absolute")
                .set("left", "150px") // Adjust as needed for positioning from the left
                .set("top", "340px");  // Adjust as needed for positioning from the top
        animals.setWidth("790px"); // Adjust the width as needed
        animals.setHeight("434px"); // Adjust the height as needed (if necessary)
        add(animals);


        Image pawprints = new Image("images/pawprints.png", "Paws photo");
        pawprints.getStyle()
                .set("position", "absolute")
                .set("left", "750px") // Adjust as needed for positioning from the left
                .set("top", "265px") // Adjust as needed for positioning from the top
                .set("transform", "rotate(240deg)"); // Rotate the image -10 degrees
        pawprints.setWidth("908"); // Adjust the width as needed
        pawprints.setHeight("190"); // Adjust the height as needed (if necessary)
        add(pawprints);


        // Container for the LoginForm
        Div container = new Div();
        container.addClassNames("login-container");

        // Title and description
        H2 title = new H2("Welcome Back! \uD83D\uDC3E");
        Paragraph description = new Paragraph("Please login to your account");
        container.add(title, description);

        // Register button
        Button registerButton = new Button("Or register here", event -> UI.getCurrent().navigate("register"));
        registerButton.addClassName("register-button");
        container.add(registerButton);


        // LoginForm
        LoginForm loginForm = new LoginForm();
        loginForm.addLoginListener(this::login);
        loginForm.addClassNames("login-form");

        container.add(loginForm);
        add(container);
        getStyle().set("font-family", "'Comic Sans MS', 'Comic Sans', cursive, sans-serif");

    }
    private void login(AbstractLogin.LoginEvent loginEvent) {
        try {
            boolean isAuthenticated = usersDAO.authenticateUser(loginEvent.getUsername(), loginEvent.getPassword());
            if (isAuthenticated) {
                Users user = usersDAO.getUserByUsername(loginEvent.getUsername()); // Assuming you have a method to get user object
                VaadinSession.getCurrent().setAttribute(Users.class, user);
                UI.getCurrent().navigate("my-profile");
            } else {
                Notification.show("Username or password incorrect, try again", 3000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate("register");
                UI.getCurrent().navigate("login");
            }
        } catch (SQLException e) {
            e.printStackTrace();

            // Handle the error condition, maybe show a notification to the user
        }
    }
}


