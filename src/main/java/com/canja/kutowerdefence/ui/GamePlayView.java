package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.Routing;
import com.canja.kutowerdefence.controller.GamePlayController;
import com.canja.kutowerdefence.controller.WaveController;
import com.canja.kutowerdefence.domain.Enemy;
import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.domain.Map;
import com.canja.kutowerdefence.domain.MapObject;
import com.canja.kutowerdefence.domain.MapObjectType;
import com.canja.kutowerdefence.domain.Point;
import com.canja.kutowerdefence.domain.TileType;
import com.canja.kutowerdefence.domain.Tower;
import com.canja.kutowerdefence.domain.TowerFactory;
import com.canja.kutowerdefence.domain.Wave;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.animation.AnimationTimer;

import java.io.File;
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
    private Button speedButton;

    @FXML
    private Button exitButton;
    
    @FXML
    private Button saveButton;

    @FXML
    private Button restartButton;
    @FXML
    private ImageView healthIcon;

    @FXML
    private ImageView goldIcon;

    @FXML
    private ImageView waveIcon;

    @FXML private VBox gameOverOverlay;
    @FXML private Button restartGameOverBtn;
    @FXML private Button exitGameOverBtn;

    @FXML
    private Label enemiesKilledLabel;

    private GamePlayController controller;
    private WaveController waveController;

    private final List<EnemyView> enemyViews = new ArrayList<>();

    private KuTowerView kuTowerView;
  
    public void setController(GamePlayController controller, WaveController waveController) {

      this.controller = controller;
        this.waveController = waveController;
        initializeMapGridPane();
        updateUI();
        enemyLayer.prefWidthProperty().bind(mapGridPane.widthProperty());
        enemyLayer.prefHeightProperty().bind(mapGridPane.heightProperty());
        //controller.spawnTestEnemy();
        waveController.startWaves();
        startEnemyUpdateLoop();
    }

    public WaveController getWaveController() {
        return waveController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeButtons();
        goldIcon.setImage(new Image("file:src/main/resources/assets/ui/button/0.png"));
        healthIcon.setImage(new Image("file:src/main/resources/assets/ui/button/1.png"));
        waveIcon.setImage(new Image("file:src/main/resources/assets/ui/button/2.png"));
    }

    public void initializeMapGridPane() {
        Map gameMap = controller.getMap();
        for (int i = 0; i < gameMap.getArray().length; i++) {
            for (int j = 0; j < gameMap.getArray()[i].length; j++) {
                TileView tileView = new TileView(gameMap.getTile(i, j));
                if (tileView.getTileType().equals(TileType.EMPTY_LOT)) {
                    int finalI = i;
                    int finalJ = j;
                    tileView.setOnMouseClicked(event -> {
                        controller.onEmptyLotClicked(tileView, finalI, finalJ);
                    });
                }
                mapGridPane.add(tileView, i, j);
            }
        }
        for (MapObject mapObject : gameMap.getObjects()) {
            if (mapObject.getType() == MapObjectType.KU_TOWER) {
                kuTowerView = new KuTowerView(mapObject); // your custom view
                kuTowerView.setHealth(controller.getPlayer().getHealth());
                mapGridPane.add(kuTowerView, mapObject.getPosition().getX(), mapObject.getPosition().getY());
            } else {
                MapObjectView objectView = new MapObjectView(mapObject);
                mapGridPane.add(objectView, mapObject.getPosition().getX(), mapObject.getPosition().getY());
            }
        }

    }

        public TileView getTileView(int x, int y) {
        for (Node node : mapGridPane.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);

            if ((nodeCol == null ? 0 : nodeCol) == x && (nodeRow == null ? 0 : nodeRow) == y) {

                if (node instanceof TileView) {
                    return (TileView) node;
                }
            }
        }

        return null;
    }

        public void reloadTowers(List<int[]> towerInfo, GameSession gameSession) {
        for (int[] info : towerInfo) {
            int x = info[1];
            int y = info[2];

            Tower newTower = TowerFactory.createTower(MapObjectType.values()[info[0]], new Point(x, y), gameSession);
            gameSession.addTower(newTower);
            TileView tileView = getTileView(x, y);
            tileView.setTileType(TileType.EMPTY);
            controller.putObjectOnMapView(newTower);
            updateUI();
        }
    }

    private void initializeButtons() {
        pauseButton.setOnAction(event -> controller.pauseGame());
        setButtonImage(pauseButton,"file:src/main/resources/assets/ui/button/button_6.png");
        speedButton.setOnAction(event -> controller.toggleSpeed(speedButton));
        setButtonImage(speedButton,"file:src/main/resources/assets/ui/button/button_5.png");
        exitButton.setOnAction(event -> handleExit());
        setButtonImage(exitButton,"file:src/main/resources/assets/ui/button/button_3.png");
        restartButton.setOnAction(event -> restartGame());
        setButtonImage(restartButton,"file:src/main/resources/assets/ui/button/button_14.png");
        saveButton.setOnAction(event -> controller.saveGame());
        setButtonImage(saveButton,"file:src/main/resources/assets/ui/button/button_2.png");
        restartGameOverBtn.setOnAction(event -> {
            System.out.println("Restart clicked!");
            controller.restartGameSession();
            speedButton.setText(" x1 ");
            mapGridPane.getChildren().clear();
            initializeMapGridPane();
            updateUI();
            gameOverOverlay.setVisible(false);
            waveController.startWaves();
        });
        setButtonImage(restartGameOverBtn, "file:src/main/resources/assets/ui/button/button_14.png");
        exitGameOverBtn.setOnAction(event -> {
            System.out.println("Exit clicked!");
            try {
                waveController.stopAll();
                Routing.returnToPreviousScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        setButtonImage(exitGameOverBtn, "file:src/main/resources/assets/ui/button/button_3.png");
    }

    private void restartGame() {
        controller.restartGameSession();
        speedButton.setText(" x1 ");

        enemyViews.clear();
        enemyLayer.getChildren().clear();
        mapGridPane.getChildren().clear();

        initializeMapGridPane();
        waveController.restartWaves();
        updateUI();
    }

    public void updateUI() {
        healthLabel.setText(String.valueOf(controller.getHealth()));
        goldLabel.setText(String.valueOf(controller.getGold()));
        waveLabel.setText(controller.getWaveInfo());
    }

    private void handleExit() {
        try {
            waveController.stopAll();
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

    public void updateEnemies(float deltaTime, boolean isGamePaused) {
        if (isGamePaused) return;

        List<EnemyView> toRemove = new ArrayList<>();
        for (EnemyView view : new ArrayList<>(enemyViews)) {
            Enemy enemy = view.getEnemy();
            enemy.update(deltaTime);
            view.update();
            
            if (view.isDead()) {
                controller.rewardPlayer(enemy.getGoldReward());
                toRemove.add(view);
            }

            if (enemy.reachedEnd()) {
                controller.loseHealth();
                controller.updateGameState();
                toRemove.add(view);
            }
            if (kuTowerView != null) {
                kuTowerView.setHealth(controller.getHealth());
            }

        }
        for (EnemyView view : toRemove) {
            enemyLayer.getChildren().remove(view);
            enemyViews.remove(view);
            System.out.println("This removed: "+view.getEnemy().getDescription().getName());
            updateUI();
        }
        
        controller.updateGameState();
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

                if (!controller.getPauseState()) {
                    float deltaTime = (now - lastTime) / 1_000_000_000f;
                    controller.getGameSession().tick(deltaTime);

                    if(controller.getGameState()==2){
                        showGameOver(false);
                    } else if (controller.getGameState()==1) {
                        showGameOver(true);
                    }
                    updateEnemies(deltaTime, controller.getPauseState());
                }
                lastTime = now;
            }
        };
        timer.start();
    }

    public void showGameOver(boolean success) {
        waveController.stopAll();
        controller.getGameSession().clearActiveEnemiesTowers();
        enemyViews.clear();
        enemyLayer.getChildren().clear();
        int kills = controller.getGameSession().getEnemiesKilled();
        enemiesKilledLabel.setText("Enemies Killed: " + kills);

        File bgFile;
        if (success) {
            bgFile = new File("src/main/resources/assets/gamesuccess.png");
        } else {
            bgFile = new File("src/main/resources/assets/gameover.png");
        }
        BackgroundImage bgImage = new BackgroundImage(new Image(bgFile.toURI().toString()),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(600, 800, false, false, true, true));
        gameOverOverlay.setBackground(new Background(bgImage));
        gameOverOverlay.setVisible(true);
    }


    public void launchProjectile(int fromX, int fromY, float toX, float toY,
                                 String spritePath, int frameW, int frameH, int frameCount,
                                 Runnable onHit) {
        ProjectileView projectile = new ProjectileView(fromX, fromY, toX, toY,
                spritePath, frameW, frameH, frameCount, onHit);
        enemyLayer.getChildren().add(projectile);
    }

    private void setButtonImage(Button button, String resourcePath) {
        Image image = new Image(resourcePath);
        ImageView imageView = new ImageView(image);
        button.setGraphic(imageView);
    }
}