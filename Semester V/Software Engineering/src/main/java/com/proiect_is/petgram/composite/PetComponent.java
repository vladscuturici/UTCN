package com.proiect_is.petgram.composite;

import com.proiect_is.model.Pets;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


public class PetComponent extends VerticalLayout {
    public PetComponent(Pets pet) {
        Image petPicture = new Image("./images/pet_" + pet.getPet_id() + ".png", "Pet Picture");
        petPicture.addClassName("pet-profile-picture");

        Span petName = new Span(pet.getName());
        petName.addClassName("pet-name");

        add(petPicture, petName);
        setAlignItems(Alignment.CENTER);
    }
}