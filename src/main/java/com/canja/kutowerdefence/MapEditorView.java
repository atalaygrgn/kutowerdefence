package com.canja.kutowerdefence;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MapEditorView implements Initializable {

    public Button exitButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void exitButtonOnClick(ActionEvent actionEvent) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}
