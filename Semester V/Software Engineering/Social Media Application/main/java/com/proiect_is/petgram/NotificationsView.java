package com.proiect_is.petgram;

import com.proiect_is.model.Users;
import com.proiect_is.petgram.patterns.Observer;
import com.proiect_is.dataaccess.NotificationsDAO;
import com.proiect_is.model.Notifications;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.VaadinSession;

import java.sql.SQLException;

@Route("notifications")
@StyleSheet("context://styles/styles.css")
public class NotificationsView extends VerticalLayout implements Observer{

    private NotificationsDAO notificationsDAO;

    public NotificationsView() {
        setSizeFull();
        setClassName("background");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        addLogo();
        try {
            notificationsDAO = new NotificationsDAO();
            loadNotifications();
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
        }
    }

    @Override
    public void update(String notificationText) {
        addNotificationToUI(notificationText);
    }

    private void addHeader() {
        H2 header = new H2("Notifications");
        header.addClassNames("page-header");
        add(header);
    }
    Div notificationDiv = new Div();
    private void loadNotifications() {
        try {
            int userId = getCurrentUserId();
            if (userId != -1) {
                for (Notifications notification : notificationsDAO.getNotificationsForUser(userId)) {
                    notificationDiv.add(addNotificationCard(notification));
                }
                notificationDiv.setClassName("notification-div");
                add(notificationDiv);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error
        }
    }

    private Div addNotificationCard(Notifications notification) {
        Div card = new Div();
        card.addClassNames("notification-card");

        Span text = new Span(notification.getText());
        text.addClassNames("notification-text");

        //Span timestamp = new Span("Received on: " + notification.getCreatedAt()); // Assuming you have a getCreatedAt method
        //timestamp.addClassNames("notification-timestamp");

        card.add(text);
        return card;
    }


    public void addLogo() {
        Image logo = new Image("./images/logo.png", "Logo");
        logo.setWidth("100px");
        logo.setHeight("auto");
        setHorizontalComponentAlignment(Alignment.START, logo);
        add(logo);
    }

    public void update(int userId, String notificationText) {
        if (this.isCurrentUser(userId)) {
            try {
                Notifications notification = new Notifications(0, userId, notificationText);
                notificationsDAO.addNotification(notification);
                addNotificationToUI(notificationText);
            } catch (SQLException e) {
                e.printStackTrace(); // Log the error
            }
        }
    }

    private boolean isCurrentUser(int userId) {
        // Logic to determine if the notification is for the current user
        Users currentUser = VaadinSession.getCurrent().getAttribute(Users.class);
        return currentUser != null && currentUser.getUser_id() == userId;
    }


    private void addNotificationToUI(String notificationText) {
        Span notificationLabel = new Span(notificationText);
        this.add(notificationLabel);
    }

    // Dummy method to represent fetching the current user's ID
    private int getCurrentUserId() {
        Users currentUser = VaadinSession.getCurrent().getAttribute(Users.class);
        return currentUser != null ? currentUser.getUser_id() : -1;
    }

}