package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.Routing;
import com.canja.kutowerdefence.domain.MapEditor;
import com.canja.kutowerdefence.domain.Tile;
import com.canja.kutowerdefence.domain.TileType;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditModeView implements Initializable {

    public Button exitButton;
    public GridPane mapGridPane;
    public MapEditor mapEditor;
    public GridPane tilePaletteGridPane;
    private TileView previousTileView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mapEditor = new MapEditor();
        initializeMapGridPane();
        initializeTilePaletteGridPane();
    }

    private void initializeMapGridPane() {
        for (int i = 0; i < mapEditor.getMap().getArray().length; i++) {
            for (int j = 0; j < mapEditor.getMap().getArray()[i].length; j++) {
                TileView tileView = new TileView(mapEditor.getMap().getTile(i, j));
                int finalI = i;
                int finalJ = j;
                tileView.setOnMouseClicked(event -> {
                    mapEditor.penTool(finalI, finalJ, tileView);
                });
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

    public void exitButtonOnClick(ActionEvent actionEvent) {
        try {
            Routing.returnToPreviousScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
