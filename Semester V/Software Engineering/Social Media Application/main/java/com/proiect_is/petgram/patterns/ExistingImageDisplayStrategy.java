package com.proiect_is.petgram.patterns;

import com.proiect_is.model.Posts;
import com.proiect_is.petgram.patterns.ImageDisplayStrategy;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;

public class ExistingImageDisplayStrategy  implements ImageDisplayStrategy {
    @Override
    public Div displayImage(Posts post) {
        String pathToPostPicture = "./images/post_" + post.getId() + ".png";
        Image postPicture = new Image(pathToPostPicture, "");
        postPicture.addClassName("post-image");

        Div imageContainer = new Div(postPicture);
        return imageContainer;
    }
}