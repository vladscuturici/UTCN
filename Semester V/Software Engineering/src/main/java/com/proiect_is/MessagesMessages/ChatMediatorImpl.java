package com.proiect_is.MessagesMessages;

import com.proiect_is.dataaccess.MessagesDAO;
import com.proiect_is.dataaccess.UsersDAO;
import com.proiect_is.model.Messages;
import com.proiect_is.model.Users;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatMediatorImpl implements ChatMediator {
    private List<Users> users;
    private UsersDAO usersDao=new UsersDAO();
    private MessagesDAO messagesDAO=new MessagesDAO();

    public ChatMediatorImpl() throws SQLException {
        this.users = new ArrayList<>();
    }

    public void addUser(Users user) {
        this.users.add(user);
    }

    @Override
    public void sendMessage(Users sender, Users receiver, String message) throws SQLException {
        Messages newMessage=new Messages(sender.getUser_id(),receiver.getUser_id(),message);
        messagesDAO.addMessage(newMessage);
    }

    public List<Users> getAllUsers() throws SQLException {
        return usersDao.getAllUsers();
    }


}
