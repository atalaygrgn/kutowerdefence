package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.domain.Map;
import com.canja.kutowerdefence.Routing;
import com.canja.kutowerdefence.domain.TileType;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditModeView implements Initializable {

    public Button exitButton;
    public ImageView tilesetImageView;
    public GridPane mapGridPane;
    public Map editModeMap;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File tilesetFile = new File("src/main/resources/assets/tileset64.png");
        Image tilesetImage = new Image(tilesetFile.toURI().toString());
        tilesetImageView.setImage(tilesetImage);
        tilesetImageView.setCache(true);

        editModeMap = new Map();
        initializeMapGridPane();
    }

    private void initializeMapGridPane() {
        for (int i = 0; i < editModeMap.getMap().length; i++) {
            for (int j = 0; j < editModeMap.getMap()[i].length; j++) {
                TileView tileView = new TileView(editModeMap.getTile(i, j));
                int finalI = i;
                int finalJ = j;
                tileView.setOnMouseClicked(event -> {
                    tileView.setTileType(TileType.HORIZONTAL);
                    editModeMap.getTile(finalI, finalJ).setTileType(TileType.HORIZONTAL);
                });
                mapGridPane.add(tileView, i, j);
            }
        }
    }

    public void exitButtonOnClick(ActionEvent actionEvent) {
        try {
            Routing.openMainMenu(exitButton);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
