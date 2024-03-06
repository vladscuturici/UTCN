package com.proiect_is.testing;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import com.proiect_is.connection.*;
import com.proiect_is.dataaccess.PetsDAO;
import com.proiect_is.dataaccess.PostsDAO;
import com.proiect_is.dataaccess.UsersDAO;
import com.proiect_is.model.Pets;
import com.proiect_is.model.Posts;
import com.proiect_is.model.Users;

import static java.lang.Boolean.TRUE;
//Clasa main pentru testat chestii pana facem interfata / site-ul

public class Main {

    public static void main(String[] args) throws SQLException, IOException {
            try {

                // Încercați să creați o conexiune folosind clasa ConnectionFactory
                Connection connection = ConnectionFactory.createConnection();

                // Verificăm dacă conexiunea a fost stabilită cu succes
                if (connection != null) {
                    System.out.println("Conexiunea la baza de date a fost stabilită cu succes.");
                    // Aici puteți adăuga codul pentru operațiile cu baza de date

                    connection.close();
                } else {
                    System.out.println("Nu s-a putut stabili conexiunea la baza de date.");
                }
            } catch (SQLException e) {
                System.err.println("Eroare la crearea conexiunii: " + e.getMessage());
            }
            PetsDAO petsDAO = new PetsDAO();

            List<Pets> petList = petsDAO.getPetByUserID(1);
            for(Pets pet : petList) {
                System.out.println(pet.getPet_id() + " " + pet.getUser_id() + " " + pet.getName());
            }
//            for(int i=11; i<100; i++) {
//                petsDAO.deletePet(i);
//            }

            System.out.println(petsDAO.getLatestId());
            File imageFolder = new File("src/main/webapp/images/");
            System.out.println("Image folder path: " + imageFolder.getAbsolutePath());
            ensureDirectoryExists(imageFolder);
            if (!canWriteToDirectory(imageFolder)) {
                System.out.println("cannot write");
            }
            else
                System.out.println("can write");

        }
        private static void ensureDirectoryExists(File directory) throws IOException {
        if (!directory.exists()) {
            System.out.println("doesn t exist");
        }
        else
            System.out.println("exist");
        }

        private static boolean canWriteToDirectory(File directory) {
            return directory.exists() && directory.canWrite();
        }
}
