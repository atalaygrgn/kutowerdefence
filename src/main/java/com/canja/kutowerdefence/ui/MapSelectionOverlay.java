package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.Routing;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
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

        Scene scene = getScene(listView, popupStage);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private static Scene getScene(ListView<File> listView, Stage popupStage) {
        Button selectButton = new Button("Start Game");
        selectButton.setOnAction(event -> {
            File selectedMap = listView.getSelectionModel().getSelectedItem();
            if (selectedMap != null) {
                GameSession session = new GameSession(selectedMap);
                popupStage.close();
                try {
                    Routing.openGamePlay(session);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        VBox vbox = new VBox(listView, selectButton);
        vbox.setSpacing(10);

        Scene scene = new Scene(vbox, 400, 300);
        return scene;
    }

}
