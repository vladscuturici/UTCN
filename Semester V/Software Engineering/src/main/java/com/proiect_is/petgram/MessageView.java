package com.proiect_is.petgram;

import com.proiect_is.MessagesMessages.ChatMediator;
import com.proiect_is.MessagesMessages.ChatMediatorImpl;
import com.proiect_is.dataaccess.MessagesDAO;
import com.proiect_is.dataaccess.UsersDAO;
import com.proiect_is.model.Messages;
import com.proiect_is.model.Posts;
import com.proiect_is.model.Users;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.sql.SQLException;
import java.util.List;



@Route("messages")
public class MessageView extends VerticalLayout {

    private final MessagesDAO messagesDAO;
    private final UsersDAO usersDAO;
    private final ChatMediator chatMediator;
    private Users currentUser;
    private Div messageArea;
    private ComboBox<Users> userListBox;
    private TextArea messageInput;

    public MessageView() throws SQLException {
        setClassName("background");
        this.messagesDAO = new MessagesDAO();
        this.usersDAO = new UsersDAO();
        this.chatMediator = new ChatMediatorImpl();
        this.currentUser = VaadinSession.getCurrent().getAttribute(Users.class);
        userListBox = createUserListBox();
        messageInput = new TextArea();

        for (Users user : usersDAO.getAllUsers()) {
            chatMediator.addUser(user);
        }

        setSizeFull();

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.add(createUserListBox(), createMessageDisplayArea());

        add(mainLayout);
    }

    private ComboBox<Users> createUserListBox() throws SQLException {
        userListBox = new ComboBox<>();
        userListBox.setLabel("Users");
        userListBox.addClassName("user-list-box");

        List<Users> allUsers = usersDAO.getAllUsers();
        userListBox.setItems(allUsers);

        userListBox.addValueChangeListener(event -> {
            Users selectedUser = event.getValue();
            if (selectedUser != null) {
                try {
                    displayMessages(selectedUser,currentUser);
                } catch (SQLException e) {
                    e.printStackTrace(); // Handle exception
                }
            }
        });

        userListBox.setWidth("30%");
        return userListBox;
    }

    private Component createMessageDisplayArea() {
        messageArea = new Div();
        messageArea.addClassName("message-area");
        messageArea.setSizeFull();

        messageInput = new TextArea();
        messageInput.setPlaceholder("Type a message...");
        messageInput.setWidthFull();

        Button sendButton = new Button("Send", event -> sendMessage());
        sendButton.addClassName("send-button");

        VerticalLayout layout = new VerticalLayout(messageArea, messageInput, sendButton);
        layout.setAlignItems(Alignment.END);
        layout.setWidth("70%");
        layout.expand(messageArea);

        return layout;
    }

    private void displayMessages(Users receiver, Users sender) throws SQLException {
        messageArea.removeAll();
        List<Messages> messages = messagesDAO.getByReceiverAndSender(receiver.getUser_id(), sender.getUser_id());
        int currentUserId = VaadinSession.getCurrent().getAttribute(Users.class).getUser_id();

        for (Messages message : messages) {
            Div messageDiv = new Div();
            messageDiv.addClassName("message");

            // Display user profile picture
            String pathToUserProfilePicture = "./images/user_" + message.getSender_id() + ".png";
            Image userProfilePicture = new Image(pathToUserProfilePicture, "Profile Picture");
            userProfilePicture.setWidth("40px");
            userProfilePicture.setHeight("40px");
            messageDiv.add(userProfilePicture);

            // Display message content
            Div contentDiv = new Div();
            Span messageContent = new Span(message.getMessage_text());
            messageContent.addClassName("message-content");
            contentDiv.add(messageContent);
            messageDiv.add(contentDiv);

            // Display timestamp below the message
            Div timestampDiv = new Div();
            Span timestamp = new Span(String.valueOf((message.getTime())));
            timestamp.addClassName("timestamp");
            timestampDiv.add(timestamp);
            messageDiv.add(timestampDiv);

            // Align messages from the current user to the right
            if (message.getSender_id() == currentUserId) {
                messageDiv.addClassName("message-right");
            }

            messageArea.add(messageDiv);
        }
    }


    private void sendMessage() {
        System.out.println("am intrat:))");

        Users selectedUser = userListBox.getValue();System.out.println(selectedUser.getUsername());
        if (selectedUser != null) {
            String messageText = messageInput.getValue();
            Messages message = new Messages(currentUser.getUser_id(), selectedUser.getUser_id(), messageText);

            try {
                chatMediator.sendMessage(usersDAO.getById(message.getSender_id()),usersDAO.getById(message.getReceiver_id()),messageText);
                //messagesDAO.addMessage(message);

                // Use the ChatMediator to send the message to the receiver
                // chatMediator.sendMessage(currentUser, selectedUser, messageText);

                // Optionally, clear the input field and refresh the message display
                messageInput.clear();
                displayMessages(selectedUser,currentUser);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("not saved");// Handle the exception appropriately
            }
        }
    }

}