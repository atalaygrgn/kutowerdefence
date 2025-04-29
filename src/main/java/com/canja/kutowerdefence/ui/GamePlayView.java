package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.Routing;
import com.canja.kutowerdefence.controller.GamePlayController;
import com.canja.kutowerdefence.domain.Map;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GamePlayView implements Initializable {

    @FXML
    private GridPane mapGridPane;

    @FXML
    private Label healthLabel;

    @FXML
    private Label goldLabel;

    @FXML
    private Label waveLabel;

    @FXML
    private Button pauseButton;

    @FXML
    private Button restartButton;

    @FXML
    private Button exitButton;

    private GamePlayController controller;

    public void setController(GamePlayController controller) {
        this.controller = controller;
        initializeMapGridPane();
        updateUI();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeButtons();
    }

    private void initializeMapGridPane() {
        Map gameMap = controller.getMap();
        for (int i = 0; i < gameMap.getArray().length; i++) {
            for (int j = 0; j < gameMap.getArray()[i].length; j++) {
                TileView tileView = new TileView(gameMap.getTile(i, j));
                mapGridPane.add(tileView, i, j);
            }
        }
    }

    private void initializeButtons() {
        pauseButton.setOnAction(event -> controller.pauseGame());
        restartButton.setOnAction(event -> controller.restartGame());
        exitButton.setOnAction(event -> handleExit());
    }

    private void updateUI() {
        healthLabel.setText(String.valueOf(controller.getHealth()));
        goldLabel.setText(String.valueOf(controller.getGold()));
        waveLabel.setText(controller.getWaveInfo());
    }

    private void handleExit() {
        try {
            Routing.returnToPreviousScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
