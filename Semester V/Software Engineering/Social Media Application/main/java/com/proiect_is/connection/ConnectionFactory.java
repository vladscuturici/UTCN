package com.proiect_is.connection;
import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.SQLException;


public class ConnectionFactory {
///https://youtu.be/e8g9eNnFpHQ, tutorial to connect intellij to the database
public ConnectionFactory() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection createConnection() throws SQLException {
    ////here you put yours, both the password and the name of the database
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3316/schooldb", "root", "18martie");
//        System.out.println("Database connection established.");
        return connection;
    }


    }
