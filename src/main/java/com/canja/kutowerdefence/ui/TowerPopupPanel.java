package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.domain.MapObjectType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.function.Consumer;

public class TowerPopupPanel {

    public static void show(int x, int y, Consumer<MapObjectType> onTowerSelected) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Build Tower");

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10; -fx-alignment: center;");

        Label label = new Label("Select a tower to build:");
        Button archerButton = new Button("Archer Tower");
        Button artilleryButton = new Button("Artillery Tower");
        Button mageButton = new Button("Mage Tower");
        Button cancelButton = new Button("Cancel");

        archerButton.setOnAction(event -> {
            onTowerSelected.accept(MapObjectType.TOWER_ARCHER);
            popupStage.close();
        });

        artilleryButton.setOnAction(event -> {
            onTowerSelected.accept(MapObjectType.TOWER_ARTILLERY);
            popupStage.close();
        });

        mageButton.setOnAction(event -> {
            onTowerSelected.accept(MapObjectType.TOWER_MAGE);
            popupStage.close();
        });

        cancelButton.setOnAction(event -> {
            onTowerSelected.accept(null);
            popupStage.close();
        });

        layout.getChildren().addAll(label, archerButton, artilleryButton, mageButton, cancelButton);

        Scene scene = new Scene(layout, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
}
