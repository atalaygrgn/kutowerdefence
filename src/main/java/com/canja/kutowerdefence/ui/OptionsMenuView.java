package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.controller.OptionController;
import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.Node;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class OptionsMenuView  implements Initializable{
    int[] defaultValues = {1, 1, 30, 5, 1, 250, 10, 25, 125, 500, 250, 3, 3, 100, 100, 10, 25, 5, 2, 2, 1, 1, 3};
    int[] optionValues;
    private List<Button> optionButtons = new ArrayList<>();
    private List<Button> option2Buttons = new ArrayList<>();
    private OptionController controller;
    public VBox opane;

    @FXML
    private HBox HBoxContainer;

    @FXML
    private Button restoreButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button saveButton;
    
    public void setController (OptionController controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            getOptionValues();
        } catch (IOException e) {
            System.err.println("Failed to get options: " + e.getMessage());
        }
        File bgFile = new File("src/main/resources/assets/optionsblank.png");
        BackgroundImage bgImage = new BackgroundImage(new Image(bgFile.toURI().toString()),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(720, 1280, false, false, true, true));
        opane.setBackground(new Background(bgImage));

        for (Node vb : HBoxContainer.getChildren()) {
            if (vb instanceof VBox vbox) {
                for (Node hb : vbox.getChildren()) {
                    if (hb instanceof HBox hbox && hbox.getChildren().size() >= 3) {
                        Node child = hbox.getChildren().get(2);
                        Node child2 = hbox.getChildren().get(0);

                        if (child instanceof Button button) optionButtons.add(button);
                        if (child2 instanceof Button button2) option2Buttons.add(button2);
                    }
                }
            }
        }
        for (Button button : option2Buttons) {
            loadButtonBackgroundImages(button, 2);
        }

            initializeButtons(optionButtons);
    }

    private void initializeButtons(List<Button> buttons) {
        int index = 1;

        for (Button button : buttons) {
            loadButtonBackgroundImages(button,1);
            button.setUserData(defaultValues[index]);
            button.setOnAction(event -> controller.toggleButton(button));

            if (optionValues != null) button.setText("" + optionValues[index++]);
        }
        loadButtonBackgroundImages(restoreButton,0);
        loadButtonBackgroundImages(exitButton,-1);
        loadButtonBackgroundImages(saveButton,0);
        restoreButton.setOnAction(event -> controller.restoreDefaultSettings(buttons));
        exitButton.setOnAction(event -> controller.exitButtonOnClick());
        saveButton.setOnAction(event -> {
            try {
                controller.saveButtonOnClick(buttons);
            } catch (IOException e) {
                System.err.println("Failed to save options: " + e.getMessage());
            }
        });
    }

    public void getOptionValues() throws IOException{
        File saveFile = new File("src/main/resources/options/options.kutdopt");

        if (!saveFile.exists()) {
            optionValues = Arrays.copyOf(defaultValues, defaultValues.length);
            return;
        }

        Gson gson = new Gson();

        try (FileReader reader = new FileReader(saveFile)) {
            optionValues = gson.fromJson(reader, int[].class);
        }
    }
    private void loadButtonBackgroundImages(Button button, int value) {
        String path;
        if (value==2) {
            path = "file:src/main/resources/assets/buttonopt.png";
        } else if (value==-1) {
            path = "file:src/main/resources/assets/buttonoptexit.png";
        } else {
            path = "file:src/main/resources/assets/buttonoptlittle.png";
        }
        BackgroundImage backgroundImage = new BackgroundImage(new Image(path), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        button.setBackground(background);
    }
}