package com.canja.kutowerdefence.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.lang.reflect.Type;

import com.canja.kutowerdefence.Routing;
import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.domain.SaveData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
                    SaveData content = extractFile(selectedSave);
                    File map = content.getMapFile();
                    int[] options = content.getOptions();
                    int[] playerInfo = content.getPlayerInfo();
                    List<int[]> towerInfo = content.getTowerInfo();

                    GameSession session = new GameSession(map, options, playerInfo);
                    popupStage.close();
                    try {
                        Routing.openGamePlayFromSave(session, towerInfo);
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

    private static SaveData extractFile(File saveFile) throws IOException{
        String content = new String(Files.readAllBytes(saveFile.toPath()));
        Gson gson = new Gson();
        Object[] data = gson.fromJson(content, Object[].class);

        String mapPath = gson.fromJson(gson.toJson(data[0]), String.class);
        int[] options = gson.fromJson(gson.toJson(data[1]), int[].class);
        int[] playerInfo = gson.fromJson(gson.toJson(data[3]), int[].class);
        
        Type listOfIntArrayType = new TypeToken<List<int[]>>() {}.getType();
        List<int[]> towerInfo = gson.fromJson(gson.toJson(data[2]), listOfIntArrayType);

        File mapFile = new File(mapPath);

        return new SaveData(mapFile, options, towerInfo, playerInfo);
    }
}
