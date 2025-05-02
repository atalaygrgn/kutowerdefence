package com.canja.kutowerdefence.domain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.canja.kutowerdefence.Routing;
import com.google.gson.Gson;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class SaveSelectionOverlay {
    public static void show(List<File> saves) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Select a Save");

        ListView<File> listView = new ListView<>();
        listView.getItems().addAll(saves);

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
            File selectedSave = listView.getSelectionModel().getSelectedItem();
            if (selectedSave != null) {
                try {
                    Object[] content = extractFile(selectedSave);
                    File map = (File) content[0];
                    File options = (File) content[1];
                    int currentWave = (int) content[2];
                    
                    GameSession session = new GameSession(map, options);
                    popupStage.close();
                    try {
                        Routing.openGamePlay(session, currentWave);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } catch (IOException e) {
                    System.err.println("Failed to open save: " + e.getMessage());
                }
            }
        });

        VBox vbox = new VBox(listView, selectButton);
        vbox.setSpacing(10);

        Scene scene = new Scene(vbox, 400, 300);
        return scene;
    }

    private static Object[] extractFile(File saveFile) throws IOException{
        String content = new String(Files.readAllBytes(saveFile.toPath()));
        Gson gson = new Gson();
        Object[] data = gson.fromJson(content, Object[].class);

        String mapPath = gson.fromJson(gson.toJson(data[0]), String.class);
        String optionPath = gson.fromJson(gson.toJson(data[1]), String.class);
        int currentWave = gson.fromJson(gson.toJson(data[2]), int.class);

        File mapFile = new File(mapPath);
        File optionFile = new File(optionPath);

        return new Object[] {mapFile, optionFile, currentWave};
    }
}
