package com.proiect_is.petgram.patterns;

import com.proiect_is.model.Posts;
import com.proiect_is.petgram.patterns.ImageDisplayStrategy;
import com.vaadin.flow.component.html.Div;

public class MissingImageDisplayStrategy implements ImageDisplayStrategy {

    @Override
    public Div displayImage(Posts post) {
        Div imageContainer = new Div();
        return imageContainer;
    }
}