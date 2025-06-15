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
import com.canja.kutowerdefence.domain.Option;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.animation.AnimationTimer;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import javafx.scene.input.MouseEvent;

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
    private ImageView pauseButton;

    @FXML
    private ImageView speedButton;

    @FXML
    private Label speedLabel;

    @FXML
    private ImageView exitButton;

    @FXML
    private ImageView saveButton;

    @FXML
    private ImageView restartButton;

    @FXML
    private ImageView healthIcon;

    @FXML
    private ImageView waveIcon;

    @FXML
    private ImageView archerTowerSelectButton;

    @FXML
    private ImageView artilleryTowerSelectButton;

    @FXML
    private ImageView mageTowerSelectButton;

    @FXML
    private Label archerCostLabel;

    @FXML
    private Label artilleryCostLabel;

    @FXML
    private Label mageCostLabel;

    @FXML
    private Label remainingTimeLabel;

    @FXML
    private ImageView towerUpgradeButton;

    @FXML
    private Label towerUpgradeCostLabel;

    @FXML private VBox gameOverOverlay;
    @FXML private ImageView restartGameOverBtn;
    @FXML private ImageView exitGameOverBtn;

    @FXML
    private Label enemiesKilledLabel;

    private GamePlayController controller;
    private WaveController waveController;

    private final List<EnemyView> enemyViews = new ArrayList<>();
    private MapObjectType selectedTowerType = null;
    private ImageView selectedTowerButton = null;

    private static final Glow glow = new Glow(0.5);
    private Circle rangePreview;
    private static final int TILE_SIZE = 64;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeButtons();
        healthIcon.setImage(new Image("file:src/main/resources/assets/ui/button/1.png"));
        waveIcon.setImage(new Image("file:src/main/resources/assets/ui/button/2.png"));
        remainingTimeLabel.setText("--:--");
    }

    public void updateTowerCosts() {
        int[] options = controller.getGameSession().getOptionValues();
        archerCostLabel.setText(String.valueOf(options[Option.ARCHER_TOWER_COST.ordinal()]));
        artilleryCostLabel.setText(String.valueOf(options[Option.ARTILLERY_TOWER_COST.ordinal()]));
        mageCostLabel.setText(String.valueOf(options[Option.MAGE_TOWER_COST.ordinal()]));
    }

    private KuTowerView kuTowerView;

    public void setController(GamePlayController controller, WaveController waveController) {
        this.controller = controller;
        this.waveController = waveController;
        initializeMapGridPane();
        updateUI();
        updateTowerCosts();  // Add this line
        enemyLayer.prefWidthProperty().bind(mapGridPane.widthProperty());
        enemyLayer.prefHeightProperty().bind(mapGridPane.heightProperty());
        //controller.spawnTestEnemy();
        waveController.startWaves();
        startEnemyUpdateLoop();
    }

    public WaveController getWaveController() {
        return waveController;
    }

    private boolean isTowerView(Node node) {
        if (node instanceof MapObjectView) {
            MapObjectView view = (MapObjectView) node;
            MapObjectType type = view.getMapObject().getType();
            return type == MapObjectType.TOWER_ARCHER || 
                   type == MapObjectType.TOWER_ARTILLERY || 
                   type == MapObjectType.TOWER_MAGE;
        }
        return false;
    }

    private void showRangePreview(int x, int y, MapObjectType towerType) {
        if (rangePreview != null) {
            enemyLayer.getChildren().remove(rangePreview);
        }

        int range = switch (towerType) {
            case TOWER_ARCHER, TOWER_ARTILLERY, TOWER_MAGE ->
                controller.getGameSession().getOptionValues()[Option.TOWER_RANGE.ordinal()];
            default -> 0;
        };

        rangePreview = new Circle(
            (x + 0.5) * TILE_SIZE,
            (y + 0.5) * TILE_SIZE,
            range * TILE_SIZE
        );
        rangePreview.setFill(Color.rgb(255, 255, 255, 0.2));
        rangePreview.setStroke(Color.WHITE);
        rangePreview.setStrokeWidth(2);
        rangePreview.setMouseTransparent(true);

        enemyLayer.getChildren().add(rangePreview);
    }

    private void hideRangePreview() {
        if (rangePreview != null) {
            enemyLayer.getChildren().remove(rangePreview);
            rangePreview = null;
        }
    }

    private boolean isTowerPlaced(int x, int y) {
        for (Node node : mapGridPane.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);

            if ((nodeCol == null ? 0 : nodeCol) == x &&
                (nodeRow == null ? 0 : nodeRow) == y) {
                if (node instanceof MapObjectView) {
                    return true;
                }
            }
        }
        return false;
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
                        if (selectedTowerType != null) {
                            boolean success = controller.buyNewTower(finalI, finalJ, selectedTowerType);
                            if (success) {
                                tileView.setTileType(TileType.EMPTY);
                                Tower newTower = controller.getGameSession().getNewestTower();
                                MapObjectView objectView = controller.putObjectOnMapView(newTower);
                                
                                // Add hover events for the new tower
                                final int towerX = finalI;
                                final int towerY = finalJ;
                                objectView.setOnMouseEntered(e -> {
                                    if (isTowerView(objectView)) {
                                        showRangePreview(towerX, towerY, newTower.getType());
                                    }
                                });
                                objectView.setOnMouseExited(e -> hideRangePreview());
                                
                                updateUI();
                                hideRangePreview();
                            }
                        }
                    });
                }
                mapGridPane.add(tileView, i, j);
            }
        }
        
        for (MapObject mapObject : gameMap.getObjects()) {
            if (mapObject.getType() == MapObjectType.KU_TOWER) {
                int maxHealth = controller.getGameSession().getPlayerHitpoint();
                kuTowerView = new KuTowerView(mapObject, maxHealth);
                kuTowerView.setHealth(controller.getHealth());
                mapGridPane.add(kuTowerView, mapObject.getPosition().getX(), mapObject.getPosition().getY());
            } else {
                MapObjectView objectView = new MapObjectView(mapObject);
                if (isTowerView(objectView)) {
                    // Store position values to avoid potential issues with closure
                    final int towerX = mapObject.getPosition().getX();
                    final int towerY = mapObject.getPosition().getY();
                    final MapObjectType towerType = mapObject.getType();
                    
                    objectView.setOnMouseEntered(e -> {
                        showRangePreview(towerX, towerY, towerType);
                    });
                    objectView.setOnMouseExited(e -> hideRangePreview());
                }
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

    private static final Glow HOVER_GLOW = new Glow(0.5);

    private void initializeButtons() {
        configurePauseButton();
        configureSpeedButton();
        configureExitButton();
        configureRestartButton();
        configureSaveButton();
    }

    private void configurePauseButton() {
        pauseButton.setOnMouseClicked(event -> controller.pauseGame(pauseButton));
    }

    private void configureSpeedButton() {
        speedButton.setOnMouseClicked(event -> controller.toggleSpeed(speedLabel));
        setupHoverEffect(speedButton);
    }

    private void configureExitButton() {
        exitButton.setOnMouseClicked(event -> handleExit());
        setupHoverEffect(exitButton);
    }

    private void configureRestartButton() {
        restartButton.setOnMouseClicked(event -> restartGame());
        setupHoverEffect(restartButton);
    }

    private void configureSaveButton() {
        saveButton.setOnMouseClicked(event -> controller.saveGame());
        setupHoverEffect(saveButton);
    }

    private void setupHoverEffect(ImageView button) {
        button.setOnMouseEntered(e -> button.setEffect(HOVER_GLOW));
        button.setOnMouseExited(e -> button.setEffect(null));
        pauseButton.setOnMouseClicked(event -> controller.pauseGame(pauseButton));
        speedButton.setOnMouseClicked(event -> controller.toggleSpeed(speedLabel));
        exitButton.setOnMouseClicked(event -> handleExit());
        restartButton.setOnMouseClicked(event -> restartGame());
        saveButton.setOnMouseClicked(event -> controller.saveGame());
        restartGameOverBtn.setOnMouseClicked(event -> {
            System.out.println("Restart clicked!");
            setControlsDisabled(false); // Enable controls when restarting
            controller.restartGameSession();
            speedLabel.setText(" x1 ");
            mapGridPane.getChildren().clear();
            initializeMapGridPane();
            updateUI();
            gameOverOverlay.setVisible(false);
            waveController.restartWaves();
        });
        exitGameOverBtn.setOnMouseClicked(event -> {
            System.out.println("Exit clicked!");
            try {
                waveController.stopAll();
                Routing.returnToPreviousScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void showGameOver(boolean success) {
        waveController.stopAll();
        controller.getGameSession().clearActiveEnemiesTowers();
        enemyViews.clear();
        enemyLayer.getChildren().clear();
        int kills = controller.getGameSession().getEnemiesKilled();
        enemiesKilledLabel.setText("Enemies Killed: " + kills);

        // Disable all game controls
        setControlsDisabled(true);

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

    private void setControlsDisabled(boolean disabled) {
        // Disable tower selection buttons
        archerTowerSelectButton.setDisable(disabled);
        artilleryTowerSelectButton.setDisable(disabled);
        mageTowerSelectButton.setDisable(disabled);
        towerUpgradeButton.setDisable(disabled);

        // Disable game control buttons
        speedButton.setDisable(disabled);
        pauseButton.setDisable(disabled);
        saveButton.setDisable(disabled);
        restartButton.setDisable(disabled);
        exitButton.setDisable(disabled);

        // Clear tower selection if game is over
        if (disabled && selectedTowerButton != null) {
            selectedTowerButton.setEffect(null);
            selectedTowerButton = null;
            selectedTowerType = null;
            hideRangePreview();
        }
    }

    private void restartGame() {
        // Enable controls when restarting
        setControlsDisabled(false);
        
        controller.restartGameSession();
        speedLabel.setText(" x1 ");

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

    public void launchProjectile(int fromX, int fromY, float toX, float toY,
                                 String spritePath, int frameW, int frameH, int frameCount,
                                 Runnable onHit, boolean useBezier) {
        ProjectileView projectile = new ProjectileView(fromX, fromY, toX, toY,
                spritePath, frameW, frameH, frameCount, onHit, useBezier);
        enemyLayer.getChildren().add(projectile);
    }

    public void setButtonImage(ImageView button, String resourcePath) {
        Image image = new Image(resourcePath);
        button.setImage(image);
    }

    @FXML
    private void onTowerToolSelect(MouseEvent event) {
        ImageView clickedButton = (ImageView) event.getSource();

        // Deselect previous button
        if (selectedTowerButton != null) {
            selectedTowerButton.setEffect(null);
        }

        // If clicking the same button, deselect it
        if (clickedButton == selectedTowerButton) {
            selectedTowerButton = null;
            selectedTowerType = null;
            hideRangePreview();
            return;
        }

        // Select new button
        selectedTowerButton = clickedButton;
        selectedTowerButton.setEffect(glow);
        selectedTowerType = MapObjectType.valueOf((String)clickedButton.getUserData());
    }

    public void updateRemainingTime(String time) {
        remainingTimeLabel.setText(time);
    }
}