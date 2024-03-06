package com.proiect_is.dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.proiect_is.model.*;
import com.proiect_is.connection.*;
public class PetsDAO {
    /// insert, update for the description, delet by pet_id , viweAllNames
    private Connection connection;
    public PetsDAO() throws SQLException{
        connection=ConnectionFactory.createConnection();
    }

    public void addPet(Pets p) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO pets (user_id, name, description) VALUES (?,?,?)");
        preparedStatement.setInt(1, p.getUser_id());
        preparedStatement.setString(2, p.getName());
        preparedStatement.setString(3, p.getDescription());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public int getIdForPhoto(int user_id, String name, String description) throws SQLException {
        int petId = -1; // Default to -1 to indicate not found

        String query = "SELECT pet_id FROM pets WHERE user_id = ? AND name = ? AND description = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, user_id);
        preparedStatement.setString(2, name);
        preparedStatement.setString(3, description);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            petId = resultSet.getInt("pet_id");
        }

        resultSet.close();
        preparedStatement.close();

        return petId;
    }



    public void updatePet(int pet_id,String description) throws SQLException{
        PreparedStatement preparedStatement= connection.prepareStatement("UPDATE pets SET description=? WHERE pet_id=?");
        preparedStatement.setString(1,description);
        preparedStatement.setInt(2,pet_id);
        preparedStatement.executeUpdate();
        preparedStatement.close();

    }
    public void deletePet(int pet_id) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM pets WHERE pet_id=?");
        preparedStatement.setInt(1,pet_id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void deletePetbyName(String name) throws SQLException{
        PreparedStatement preparedStatement=connection.prepareStatement("DELETE FROM pets WHERE name=?");
        preparedStatement.setString(1,name);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public int getLatestId() throws SQLException {
        String query = "SELECT MAX(pet_id) FROM pets";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    return -1;
                }
            }
        }
    }

    public List<String> viewNamePets() throws SQLException{
        List<String> names = new ArrayList<>();
        PreparedStatement preparedStatement= connection.prepareStatement("SELECT name FROM pets");
        ResultSet resultSet=preparedStatement.executeQuery();
        while(resultSet.next()){
            String name= resultSet.getString("name");
            names.add(name);
        }
        resultSet.close();
        preparedStatement.close();
        return names;
    }

    public Pets getById(int id) throws SQLException {
        String query = "SELECT * FROM pets WHERE pet_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int petId = resultSet.getInt("pet_id");
                    int userId = resultSet.getInt("user_id");
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");

                    // Assuming Pets constructor: Pets(int petId, int userId, String name, String description)
                    return new Pets(petId, userId, name, description);
                } else {
                    // No pet found with the given id
                    return null;
                }
            }
        }
    }

    public List<Pets> getPetByUserID(int userId) throws SQLException {
        List<Pets> petsList = new ArrayList<>();
        String query = "SELECT * FROM pets WHERE user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int petId = resultSet.getInt("pet_id");
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    // Assuming Pets constructor: Pets(int petId, int userId, String name, String description)
                    Pets pet = new Pets(petId, userId, name, description);
                    petsList.add(pet);
                }
            }
        }

        return petsList;
    }
}
