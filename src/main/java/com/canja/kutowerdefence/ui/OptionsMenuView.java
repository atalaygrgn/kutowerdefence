package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.controller.OptionController;
import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
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
    private OptionController controller;

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

        for (Node vb : HBoxContainer.getChildren()) {
            if (vb instanceof VBox vbox) {
                for (Node hb : vbox.getChildren()) {
                    if (hb instanceof HBox hbox && hbox.getChildren().size() >= 3) {
                        Node child = hbox.getChildren().get(2);
                        
                        if (child instanceof Button button) optionButtons.add(button);
                    }
                }
            }
        }

        initializeButtons(optionButtons);
    }

    private void initializeButtons(List<Button> buttons) {
        int index = 1;

        for (Button button : buttons) {
            button.setUserData(defaultValues[index]);
            button.setOnAction(event -> controller.toggleButton(button));

            if (optionValues != null) button.setText("" + optionValues[index++]);
        }

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
}