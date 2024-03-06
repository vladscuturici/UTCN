package com.proiect_is.petgram.composite;

import com.proiect_is.dataaccess.PetsDAO;
import com.proiect_is.model.Pets;
import com.proiect_is.model.Users;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.sql.SQLException;
import java.util.List;

public class PetListComponent extends HorizontalLayout {
    private final PetsDAO petsDAO;

    public PetListComponent(Users user) throws SQLException {
        this.petsDAO = new PetsDAO();
        setClassName("pet-list");
        setWidthFull();
        setDefaultVerticalComponentAlignment(Alignment.CENTER);

        addPetListItems(user);
    }

    private void addPetListItems(Users user) throws SQLException {
        List<Pets> pets = petsDAO.getPetByUserID(user.getUser_id());
        for (Pets pet : pets) {
            add(new PetComponent(pet));
        }
    }
}