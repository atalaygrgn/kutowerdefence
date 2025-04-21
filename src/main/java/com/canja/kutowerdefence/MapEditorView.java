package com.canja.kutowerdefence;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MapEditorView implements Initializable {

    public Button exitButton;
    public ImageView tilesetImageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File tilesetFile = new File("src/main/resources/assets/tileset64.png");
        Image tilesetImage = new Image(tilesetFile.toURI().toString());
        tilesetImageView.setImage(tilesetImage);
        tilesetImageView.setCache(true);
    }

    public void exitButtonOnClick(ActionEvent actionEvent) {
        try {
            Routing.openMainMenu(exitButton);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
