package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.controller.OptionController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Node;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OptionsMenuView  implements Initializable{
    int[] defaultValues = {1, 30, 5, 1, 250, 10, 25, 125, 500, 250, 3, 100, 100, 10, 25, 5, 2, 2, 1, 1};
    private List<Button> optionButtons = new ArrayList<>();
    private OptionController controller;

    @FXML
    private HBox HBoxContainer;

    @FXML
    private Button restoreButton;

    @FXML
    private Button exitButton;
    
    public void setController (OptionController controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        int index = 0;

        for (Button button : buttons) {
            button.setUserData(defaultValues[index++]);
            button.setOnAction(event -> controller.toggleButton(button));
        }

        restoreButton.setOnAction(event -> controller.restoreDefaultSettings(buttons));
        exitButton.setOnAction(event -> controller.exitButtonOnClick());
    }
}