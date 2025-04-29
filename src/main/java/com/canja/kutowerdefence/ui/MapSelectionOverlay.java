package com.canja.kutowerdefence.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

import com.canja.kutowerdefence.domain.GameSession;

public class MapSelectionOverlay {

    public static void show(List<File> maps) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Select a Map");

        ListView<File> listView = new ListView<>();
        listView.getItems().addAll(maps);

        listView.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                setText((empty || file == null) ? "" : file.getName());
            }
        });



        Button selectButton = new Button("Start Game");
        selectButton.setOnAction(event -> {
            File selectedMap = listView.getSelectionModel().getSelectedItem();
            if (selectedMap != null) {
                GameSession session = new GameSession(selectedMap);
                session.start();
                popupStage.close();
            }
        });

        VBox vbox = new VBox(listView, selectButton);
        vbox.setSpacing(10);

        Scene scene = new Scene(vbox, 400, 300);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }


}
