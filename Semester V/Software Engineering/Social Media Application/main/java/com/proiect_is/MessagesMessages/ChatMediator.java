package com.proiect_is.MessagesMessages;


import com.proiect_is.model.Users;

import java.sql.SQLException;
import java.util.List;

public interface ChatMediator {
    void sendMessage(Users sender,Users reciever, String message) throws SQLException;

    void addUser(Users user);
    List<Users> getAllUsers() throws SQLException;
}