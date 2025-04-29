package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.Routing;
import com.canja.kutowerdefence.domain.GameSession;
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

    private GameSession gameSession;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeButtons();
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
        initializeMapGridPane(); // Initialize map after setting GameSession
    }

    private void initializeMapGridPane() {
        Map gameMap = gameSession.getMap(); // Assuming GameSession has a getMap() method
        for (int i = 0; i < gameMap.getArray().length; i++) {
            for (int j = 0; j < gameMap.getArray()[i].length; j++) {
                TileView tileView = new TileView(gameMap.getTile(i, j));
                mapGridPane.add(tileView, i, j);
            }
        }
    }

    private void initializeButtons() {
        pauseButton.setOnAction(event -> handlePause());
        restartButton.setOnAction(event -> handleRestart());
        exitButton.setOnAction(event -> handleExit());
    }

    private void handlePause() {
        System.out.println("Game Paused");
        // TODO: Implement pause functionality
    }

    private void handleRestart() {
        System.out.println("Game Restarted");
        // TODO: Implement restart functionality
    }

    private void handleExit() {
        try {
            Routing.returnToPreviousScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
