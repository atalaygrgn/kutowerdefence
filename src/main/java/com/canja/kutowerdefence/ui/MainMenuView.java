package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.Routing;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuView implements Initializable {


    public ImageView logoimage;
    public VBox pane;
    public Button editModeButton;
    public Button optionsButton;

    public void onQuitButtonClick(ActionEvent actionEvent) {
        Platform.exit();
    }


    public void showImages() {
        try {
            File titleFile = new File("src/main/resources/assets/logo.png");
            Image titleImage = new Image(titleFile.toURI().toString());
            logoimage.setImage(titleImage);
            logoimage.setCache(true);

            File bgFile = new File("src/main/resources/assets/titlebg.png");
            BackgroundImage bgImage= new BackgroundImage(new Image(bgFile.toURI().toString()),
                    BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(720, 1280, false, false, true, true));
            pane.setBackground(new Background(bgImage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showImages();
        editModeButton.setOnAction((event) -> {
            try {
                Routing.openEditMode();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        optionsButton.setOnAction((event) -> {
            try {
                Routing.openOptionsMenu();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}
