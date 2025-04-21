package com.canja.kutowerdefence;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

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
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("mapeditor-view.fxml"));

                Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
                Stage stage = new Stage();
                stage.setTitle("Edit Mode");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        optionsButton.setOnAction((event) -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("optionsmenu-view.fxml"));

                Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
                Stage stage = new Stage();
                stage.setTitle("Options");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}
