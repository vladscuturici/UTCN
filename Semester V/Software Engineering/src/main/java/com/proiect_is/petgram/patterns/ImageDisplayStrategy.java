package com.proiect_is.petgram.patterns;

import com.proiect_is.model.Posts;
import com.vaadin.flow.component.html.Div;

public interface ImageDisplayStrategy {
    Div displayImage(Posts post);
}