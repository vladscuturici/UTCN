package com.proiect_is.petgram.composite;

import com.proiect_is.dataaccess.CommentsDAO;
import com.proiect_is.dataaccess.PetsDAO;
import com.proiect_is.dataaccess.PostsDAO;
import com.proiect_is.dataaccess.UsersDAO;
import com.proiect_is.model.Comments;
import com.proiect_is.model.Pets;
import com.proiect_is.model.Posts;
import com.proiect_is.model.Users;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.server.VaadinSession;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FeedComponent extends VerticalLayout {
    private UsersDAO usersDAO = new UsersDAO();
    private CommentsDAO commentsDAO = new CommentsDAO();
    public FeedComponent(Users user) throws SQLException {
        addFeed(user);
    }

    public void addFeed(Users user) throws SQLException {
        VerticalLayout feedLayout = new VerticalLayout();
        PostsDAO postsDAO = new PostsDAO();
        List<Posts> postList = PostsDAO.getPostsByUserId(user.getUser_id());
        PetsDAO petsDAO = new PetsDAO();
        for(Posts post : postList) {
            Pets pet = petsDAO.getById(post.getPet_id());
            PostComponent postComponent = new PostComponent(post, user, pet);
            add(postComponent);
        }
//        Posts firstPost = new Posts(3, 1, 1, String.valueOf(petsDAO.getLatestId()), 0, "NULL", 5, new Timestamp(123, 10, 10, 12, 4, 23, 0));
//        addPost(firstPost, user, new Pets(1, 1, "Bobo", "Just a little hamster"));
        add(feedLayout);
    }


}