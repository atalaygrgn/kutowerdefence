package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.Routing;
import com.canja.kutowerdefence.domain.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditModeView implements Initializable {

    public GridPane mapGridPane;
    public MapEditor mapEditor;
    public GridPane tilePaletteGridPane;

    public Button exitButton;
    public Button penButton;
    public Button clearButton;
    public Button saveButton;
    public Button loadButton;
    public ImageView fileIcon;

    public Button setPathStartButton;
    public Button setPathEndButton;

    private TileView previousTileView;

    private boolean isSettingPathStart = false;
    private boolean isSettingPathEnd = false;
    private boolean tileEdit = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mapEditor = new MapEditor();
        initializeMapGridPane();
        initializeTilePaletteGridPane();
        initializeToolButtons();
        fileIcon.setFitHeight(40);
        fileIcon.setPreserveRatio(true);
        fileIcon.setImage(new Image("file:src/main/resources/assets/ui/button/button_2.png"));
    }

    private void initializeMapGridPane() {
        for (int i = 0; i < mapEditor.getMap().getArray().length; i++) {
            for (int j = 0; j < mapEditor.getMap().getArray()[i].length; j++) {
                TileView tileView = new TileView(mapEditor.getMap().getTile(i, j));
                int finalI = i;
                int finalJ = j;
                tileView.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) { // Left click
                        if (isSettingPathStart || isSettingPathEnd) {
                            if (Map.isEdgeTile(finalI, finalJ)) {
                                if (isSettingPathStart) {
                                    mapEditor.getMap().setPathStartEnd(finalI, finalJ,
                                        mapEditor.getMap().getPathStartEnd()[1] != null ?
                                        mapEditor.getMap().getPathStartEnd()[1].getX() : -1,
                                        mapEditor.getMap().getPathStartEnd()[1] != null ?
                                        mapEditor.getMap().getPathStartEnd()[1].getY() : -1);
                                } else if (isSettingPathEnd) {
                                    mapEditor.getMap().setPathStartEnd(
                                        mapEditor.getMap().getPathStartEnd()[0] != null ?
                                        mapEditor.getMap().getPathStartEnd()[0].getX() : -1,
                                        mapEditor.getMap().getPathStartEnd()[0] != null ?
                                        mapEditor.getMap().getPathStartEnd()[0].getY() : -1,
                                        finalI, finalJ);
                                }
                                isSettingPathStart = false;
                                isSettingPathEnd = false;
                                showAlert("Success", "Path point set successfully.");
                            } else {
                                showAlert("Error", "Path points must be set on edge tiles.");
                            }
                        } else {
                            if (tileEdit && getObjectFromGridPane(mapGridPane, finalI, finalJ) == null) {
                                mapEditor.penTool(finalI, finalJ, tileView);
                            } else {
                                if (getObjectFromGridPane(mapGridPane, finalI, finalJ) == null &&
                                        getTileFromGridPane(mapGridPane, finalI, finalJ).getTileType().equals(TileType.EMPTY)) {
                                    MapObject newObject = MapObjectFactory.createMapObject(mapEditor.getSelectedObjectType(), finalI, finalJ);
                                    mapEditor.placeObject(newObject);
                                    putObjectOnMapView(newObject);
                                    System.out.println("New object placed on map");
                                }
                            }
                        }
                    } else if (event.getButton() == MouseButton.SECONDARY) { // Right click
                        mapEditor.getMap().editTile(finalI, finalJ, TileType.EMPTY);
                        tileView.setTile(new Tile());
                    }
                });
                mapGridPane.add(tileView, i, j);
            }
        }
    }

    private void putObjectOnMapView(MapObject mapObject) {
        MapObjectView newObjectView = new MapObjectView(mapObject);
        newObjectView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) { // Left click
                mapEditor.removeObject(mapObject);
                mapGridPane.getChildren().remove(newObjectView);
                System.out.println("Object removed from map");
            }});
        mapGridPane.add(newObjectView, mapObject.getPosition().getX(), mapObject.getPosition().getY());

    }

    private void initializeTilePaletteGridPane() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                TileView tileView = new TileView(new Tile(i, j));
                int finalI = i;
                int finalJ = j;
                tileView.setOnMouseClicked(event -> {
                    TileType tileType = Tile.getTileType(finalI, finalJ);
                    if (tileType.ordinal() >= 16) {
                        tileEdit = false;
                        if (finalI >= 6 && finalI <= 7 && finalJ >= 0 && finalJ <= 1) {
                            // Handle 2x2 big castle object
                            mapEditor.changeSelectedObject(MapObjectType.CASTLE);
                            System.out.println("Object selection: CASTLE");
                        } else {
                            mapEditor.changeSelectedObject(MapObject.getObjectType(finalI, finalJ));
                            System.out.println("Object selection: " + MapObject.getObjectType(finalI, finalJ));
                        }
                    } else {
                        tileEdit = true;
                        mapEditor.changeSelectedTile(tileType);
                    }
                    tileView.highlight();
                    if (previousTileView != null && !previousTileView.equals(tileView)) {
                        previousTileView.unhighlight();
                    }
                    previousTileView = tileView;
                });
                if (mapEditor.getSelectedTileType().equals(tileView.getTileType())) {
                    previousTileView = tileView;
                    tileView.highlight();
                }
                tilePaletteGridPane.add(tileView, j, i);
            }
        }
    }

    private void initializeToolButtons() {
        initializeToolButton(penButton, 0);
        initializeToolButton(clearButton, 1);
        initializeToolButton(exitButton, 3);

        setPathStartButton.setOnAction(event -> handleSetPathStart());
        setPathEndButton.setOnAction(event -> handleSetPathEnd());
    }

    private void initializeToolButton(Button button, int index) {
        ImageView view = new ImageView(new Image("file:src/main/resources/assets/ui/button/button_"+ index + ".png"));
        view.setFitHeight(40);
        view.setPreserveRatio(true);
        button.setGraphic(view);
    }

    @FXML
    private void exitButtonOnClick() {
        try {
            Routing.returnToPreviousScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void saveButtonOnClick() {
        if(!mapEditor.getMap().validatePath()){
            showError("Error Saving Map", "Make sure a valid path exists.");
            return;
        }

        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Save Map");
        textInputDialog.setHeaderText("Enter the name of the map:");
        textInputDialog.setContentText("Map Name:");

        textInputDialog.showAndWait().ifPresent(mapName -> {
            if (mapName.trim().isEmpty()) {
            showError("Error Saving Map", "Map name cannot be empty.");
            return;
            }

            File saveDirectory = new File("src/main/resources/maps");
            if (!saveDirectory.exists()) {
            saveDirectory.mkdirs();
            }

            File saveFile = new File(saveDirectory, mapName + ".kutdmap");
            mapEditor.saveMap(saveFile.getPath());
            showAlert("Success", "Map saved successfully as " + mapName + ".kutdmap");
        });
    }

    @FXML
    private void clearButtonOnClick() {
        // domain update
        mapEditor.resetMap();

        // UI update
        for (int i = mapGridPane.getChildren().size() - 1; i >= 0; i--) {
            Node view = mapGridPane.getChildren().get(i);
            if (view instanceof TileView) {
            ((TileView) view).setTile(new Tile());
            } else if (view instanceof MapObjectView) {
            mapGridPane.getChildren().remove(i);
            }
        }
    }

    @FXML
    private void loadButtonOnClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Map");
        fileChooser.setInitialFileName("map");
        File selectedFile = fileChooser.showOpenDialog(loadButton.getScene().getWindow());
        if (selectedFile != null) {

            for (int i = mapGridPane.getChildren().size() - 1; i >= 0; i--) {
                Node view = mapGridPane.getChildren().get(i);
                if (view instanceof MapObjectView) {
                    mapGridPane.getChildren().remove(i);
                }
            }

            try {
                mapEditor.loadMap(selectedFile.getPath());


                for (int i = 0; i < mapEditor.getMap().getArray().length; i++) {
                    for (int j = 0; j < mapEditor.getMap().getArray()[i].length; j++) {
                        getTileFromGridPane(mapGridPane, i, j).setTile(mapEditor.getMap().getTile(i, j));
                    }
                }

                for (MapObject mapObject : mapEditor.getMap().getObjects()) {
                    putObjectOnMapView(mapObject);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleSetPathStart() {
        isSettingPathStart = true;
        isSettingPathEnd = false;
        showAlert("Set Path Start", "Click on an edge tile to set the path start point.");
    }

    private void handleSetPathEnd() {
        isSettingPathStart = false;
        isSettingPathEnd = true;
        showAlert("Set Path End", "Click on an edge tile to set the path end point.");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static TileView getTileFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof TileView) {
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                    return (TileView) node;
                }
            }
        }
        return null;
    }

    private static MapObjectView getObjectFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof MapObjectView) {
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                    return (MapObjectView) node;
                }
            }
        }
        return null;
    }
}
