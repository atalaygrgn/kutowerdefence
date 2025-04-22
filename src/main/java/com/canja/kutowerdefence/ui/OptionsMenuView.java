package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.Routing;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionsMenuView  implements Initializable{
    public Button exitButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void exitButtonOnClick(ActionEvent actionEvent) {
        try {
            Routing.openMainMenu(exitButton);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}