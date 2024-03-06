package com.proiect_is.petgram;

import com.proiect_is.dataaccess.UsersDAO;
import com.proiect_is.model.Users;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.sql.SQLException;

@Route("register")
@StyleSheet("context://styles/styles.css")
public class RegisterView extends VerticalLayout {

    private UsersDAO usersDAO;
    private Div errorMessageDiv;


    public RegisterView() throws SQLException {
        this.usersDAO = new UsersDAO();
        setupUI();
    }
    private void setupUI() {
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


        TextField usernameField = new TextField("Username");
        EmailField emailfield = new EmailField("Email");
        PasswordField passwordField = new PasswordField("Password");

        Button registerButton = new Button("Register", event -> registerUser(usernameField.getValue(), emailfield.getValue(), passwordField.getValue()));

        errorMessageDiv = new Div();
        errorMessageDiv.getStyle().set("color", "red");
        errorMessageDiv.setVisible(false);

        Div container = new Div();
        container.addClassNames("login-container");
        container.add(new H2("Welcome! \uD83D\uDC3E"), new Paragraph("Please register by entering your information"), usernameField, emailfield, passwordField, registerButton, errorMessageDiv);
        add(container);
    }
    private void registerUser(String username, String email, String password) {
        try {
            if (usersDAO.usernameExists(username)) {
                showErrorMessage("Username already exists. Please try another one.");
                return;
            }

            // Create a new Users object with the provided information
            Users newUser = new Users(username, email, password);
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password); // Ensure this password is hashed for security

            usersDAO.add_user(newUser); // Insert user data into the database
            // Show success notification
            Notification successNotification = Notification.show("Account created successfully", 2000, Notification.Position.MIDDLE);

            // Delayed navigation to login page
            successNotification.addOpenedChangeListener(event -> {
                if (!event.isOpened()) {
                    UI.getCurrent().navigate("login");
                }
            });

        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorMessage("Registration failed due to a database error.");
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorMessage("An unexpected error occurred during registration.");
        }
    }


    private void showErrorMessage(String message) {
        errorMessageDiv.setText(message);
        errorMessageDiv.setVisible(true);
    }

}
