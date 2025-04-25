package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.Routing;
import com.canja.kutowerdefence.domain.MapEditor;
import com.canja.kutowerdefence.domain.Tile;
import com.canja.kutowerdefence.domain.TileType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    private TileView previousTileView;

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
                tileView.setOnMouseClicked(event -> mapEditor.penTool(finalI, finalJ, tileView));
                mapGridPane.add(tileView, i, j);
            }
        }
    }

    private void initializeTilePaletteGridPane() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                TileView tileView = new TileView(new Tile(i, j));
                int finalI = i;
                int finalJ = j;
                tileView.setOnMouseClicked(event -> {
                    TileType tileType = Tile.getTileType(finalI, finalJ);
                    mapEditor.changeSelectedTile(tileType);
                    tileView.highlight();
                    if (previousTileView != null) if (!previousTileView.equals(tileView)) {
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Map");
        fileChooser.setInitialFileName("map");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All files","*.kutdmap"));
        File selectedFile = fileChooser.showSaveDialog(saveButton.getScene().getWindow());
        mapEditor.saveMap(selectedFile.getPath());
    }

    @FXML
    private void clearButtonOnClick() {
        // domain update
        mapEditor.resetMap();

        //UI update
        for (Node tileView : mapGridPane.getChildren()) {
            if(tileView instanceof TileView) ((TileView) tileView).setTile(new Tile());
        }
    }

    @FXML
    private void loadButtonOnClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Map");
        fileChooser.setInitialFileName("map");
        File selectedFile = fileChooser.showOpenDialog(loadButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                mapEditor.loadMap(selectedFile.getPath());

                for (int i = 0; i < mapEditor.getMap().getArray().length; i++) {
                    for (int j = 0; j < mapEditor.getMap().getArray()[i].length; j++) {
                        ((TileView) getNodeFromGridPane(mapGridPane, i, j)).setTile(mapEditor.getMap().getTile(i, j));
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (node instanceof TileView) {
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                    return node;
                }
            }
        }
        return null;
    }
}
