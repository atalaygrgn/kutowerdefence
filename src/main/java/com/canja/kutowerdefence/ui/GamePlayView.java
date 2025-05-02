package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.Routing;
import com.canja.kutowerdefence.controller.GamePlayController;
import com.canja.kutowerdefence.domain.Enemy;
import com.canja.kutowerdefence.domain.Map;
import com.canja.kutowerdefence.domain.MapObject;
import com.canja.kutowerdefence.domain.TileType;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.animation.AnimationTimer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;

public class GamePlayView implements Initializable {

    @FXML
    private Pane enemyLayer;

    @FXML
    private GridPane mapGridPane;

    public GridPane getMapGridPane() {
        return mapGridPane;
    }

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

    private final List<EnemyView> enemyViews = new ArrayList<>();

    public void setController(GamePlayController controller) {
        this.controller = controller;
        initializeMapGridPane();
        updateUI();
        enemyLayer.prefWidthProperty().bind(mapGridPane.widthProperty());
        enemyLayer.prefHeightProperty().bind(mapGridPane.heightProperty());
        controller.spawnTestEnemy();
        startEnemyUpdateLoop();
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
                if (tileView.getTileType().equals(TileType.EMPTY_LOT)) {
                    int finalI = i;
                    int finalJ = j;
                    tileView.setOnMouseClicked(event -> {
                        controller.onEmptyLotClicked(tileView, finalI, finalJ);
                        updateUI();
                    });
                }
                mapGridPane.add(tileView, i, j);
            }
        }
        for (MapObject mapObject : gameMap.getObjects()) {
            MapObjectView objectView = new MapObjectView(mapObject);
            mapGridPane.add(objectView, mapObject.getPosition().getX(), mapObject.getPosition().getY());
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

    public void spawnEnemy(Enemy enemy) {
        EnemyView view = new EnemyView(enemy);
        enemyLayer.getChildren().add(view);
        enemyViews.add(view);
    }

    public void updateEnemies(float deltaTime) {
        List<EnemyView> toRemove = new ArrayList<>();
        for (EnemyView view : enemyViews) {
            Enemy enemy = view.getEnemy();
            enemy.update(deltaTime);
            view.update();

            if (view.isDead()) {
                controller.rewardPlayer(enemy.getGoldReward());
                toRemove.add(view);
            }

            if (enemy.reachedEnd()) {
                controller.loseHealth();
                toRemove.add(view);
            }
        }
        for (EnemyView view : toRemove) {
            enemyLayer.getChildren().remove(view);
            enemyViews.remove(view);
            updateUI();
        }
    }


    private void startEnemyUpdateLoop() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = -1;

            @Override
            public void handle(long now) {
                if (lastTime < 0) {
                    lastTime = now;
                    return;
                }
                float deltaTime = (now - lastTime) / 1_000_000_000f;
                controller.getGameSession().tick(deltaTime);

                updateEnemies(deltaTime);
                lastTime = now;
            }
        };
        timer.start();
    }

    public void launchProjectile(int fromX, int fromY, float toX, float toY,
                                 String spritePath, int frameW, int frameH, int frameCount,
                                 Runnable onHit) {
        ProjectileView projectile = new ProjectileView(fromX, fromY, toX, toY,
                spritePath, frameW, frameH, frameCount, onHit);
        enemyLayer.getChildren().add(projectile);
    }

}