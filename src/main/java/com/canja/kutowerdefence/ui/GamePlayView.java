package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.Routing;
import com.canja.kutowerdefence.controller.GamePlayController;
import com.canja.kutowerdefence.controller.LevelManager;
import com.canja.kutowerdefence.controller.WaveController;
import com.canja.kutowerdefence.domain.Enemy;
import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.domain.Map;
import com.canja.kutowerdefence.domain.MapObject;
import com.canja.kutowerdefence.domain.MapObjectType;
import com.canja.kutowerdefence.domain.Point;
import com.canja.kutowerdefence.domain.SaveService;
import com.canja.kutowerdefence.domain.TileType;
import com.canja.kutowerdefence.domain.Tower;
import com.canja.kutowerdefence.domain.TowerFactory;
import com.canja.kutowerdefence.domain.TowerLevel;
import com.canja.kutowerdefence.domain.Option;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.animation.AnimationTimer;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

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
    @FXML private ImageView nextLevelBtn;

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
    private PauseTransition messageTimer;

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

        List<Tower> towers = controller.getGameSession().getTowers();
        for (Tower tower : towers) {
            if (tower.getPosition().getX() == x && tower.getPosition().getY() == y && tower.getLevel() == TowerLevel.LEVEL1) {
                int upgradeCost = tower.getUpgradeCost();
                String message = "Upgrade Cost: " + upgradeCost;
                String bgColor = "#f4e9bc";
                String borderColor = "#5e4b1c";

                Platform.runLater(() -> {
                    towerUpgradeCostLabel.setText(message);
                    towerUpgradeCostLabel.setStyle(
                            "-fx-background-color: linear-gradient(" + bgColor + ", " + bgColor + ");" +
                                    "-fx-text-fill: black;" +
                                    "-fx-font-size: 18px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-padding: 10px 20px;" +
                                    "-fx-background-radius: 16;" +
                                    "-fx-border-color: " + borderColor + ";" +
                                    "-fx-border-width: 2px;" +
                                    "-fx-border-radius: 16;" +
                                    "-fx-alignment: center;"
                    );
                    towerUpgradeCostLabel.setWrapText(true);
                    towerUpgradeCostLabel.setTextAlignment(TextAlignment.CENTER);
                    towerUpgradeCostLabel.setEffect(new Glow(0.4));
                    towerUpgradeCostLabel.setPrefWidth(200);
                    towerUpgradeCostLabel.setPrefHeight(80);

                    double localX = 300;
                    double localY = -20;

                    towerUpgradeCostLabel.setLayoutX(localX);
                    towerUpgradeCostLabel.setLayoutY(localY);
                    towerUpgradeCostLabel.setVisible(true);

                    if (messageTimer != null) {
                        messageTimer.stop();
                    }

                    messageTimer = new PauseTransition(Duration.seconds(2));
                    messageTimer.setOnFinished(e -> towerUpgradeCostLabel.setVisible(false));
                    messageTimer.play();
                });
                break;
            }
        }
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
                        if (selectedTowerType != null && !isTowerPlaced(finalI, finalJ)) {
                            boolean success = controller.buyNewTower(finalI, finalJ, selectedTowerType);
                            if (success) {
                                tileView.setTileType(TileType.EMPTY);
                                Tower newTower = controller.getGameSession().getNewestTower();
                                MapObjectView objectView = controller.putObjectOnMapView(newTower);

                                final int towerX = finalI;
                                final int towerY = finalJ;
                                objectView.setOnMouseEntered(e -> {
                                    if (isTowerView(objectView)) {
                                        showRangePreview(towerX, towerY, newTower.getType());
                                    }
                                });
                                objectView.setOnMouseExited(e -> hideRangePreview());

                                objectView.setOnMouseClicked(e -> tryUpgradeTower(objectView));

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
                    final int towerX = mapObject.getPosition().getX();
                    final int towerY = mapObject.getPosition().getY();
                    final MapObjectType towerType = mapObject.getType();

                    objectView.setOnMouseEntered(e -> showRangePreview(towerX, towerY, towerType));
                    objectView.setOnMouseExited(e -> hideRangePreview());
                    objectView.setOnMouseClicked(e -> tryUpgradeTower(objectView));
                }
                mapGridPane.add(objectView, mapObject.getPosition().getX(), mapObject.getPosition().getY());
            }
        }
    }

    private void tryUpgradeTower(MapObjectView objectView) {
        if (objectView == null) {
            if (!controller.isUpgradeMode()) return;

            showTowerMessage("Upgrade mode active", "#cceabb", "#4b792d");
            return;
        }

        if (!controller.isUpgradeMode()) return;
        if (!(objectView.getMapObject() instanceof Tower tower)) return;

        TowerLevel level = tower.getLevel();
        int upgradeCost = tower.getUpgradeCost();
        int playerGold = controller.getGameSession().getPlayer().getGoldAmount();

        // Debug logging
        System.out.println("[DEBUG] Tower type: " + tower.getType());
        System.out.println("[DEBUG] Tower level: " + level);
        System.out.println("[DEBUG] Upgrade cost: " + upgradeCost);
        System.out.println("[DEBUG] Player gold: " + playerGold);

        String message;
        String bgColor = "#f4e9bc";
        String borderColor = "#5e4b1c";

        if (controller.getPauseState()) {
            message = "Cannot upgrade in pause mode";
            System.out.println("[DEBUG] Message: " + message + " (Pause state)");
        } else if (level == TowerLevel.LEVEL2 || upgradeCost == 0) {
            message = "This tower is maxed out!";
            System.out.println("[DEBUG] Message: " + message + " (Tower is maxed out)");
        } else if (playerGold < upgradeCost) {
            message = "Not enough gold!";
            System.out.println("[DEBUG] Message: " + message + " (Not enough gold)");
        }
        else {
            controller.getGameSession().getPlayer().deductGold(upgradeCost);
            tower.upgrade();

            String newAsset = switch (tower.getType()) {
                case TOWER_ARCHER -> "/assets/tile64/archerl2.png";
                case TOWER_MAGE -> "/assets/tile64/magel2.png";
                case TOWER_ARTILLERY -> "/assets/tile64/artilleryl2.png";
                default -> null;
            };

            if (newAsset != null) {
                Image image = new Image(getClass().getResource(newAsset).toExternalForm());
                objectView.setImage(image);
            }

            updateUI();
            controller.toggleUpgradeMode();
            towerUpgradeButton.setEffect(null);

            showTowerMessage("Tower upgraded successfully!", "#cceabb", "#4b792d");
            return;
        }

        showTowerMessage(message, bgColor, borderColor);
    }
    private void showTowerMessage(String message, String bgColor, String borderColor) {
        Platform.runLater(() -> {
            towerUpgradeCostLabel.setText(message);
            towerUpgradeCostLabel.setStyle(
                    "-fx-background-color: linear-gradient(" + bgColor + ", " + bgColor + ");" +
                            "-fx-text-fill: black;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 10px 20px;" +
                            "-fx-background-radius: 16;" +
                            "-fx-border-color: " + borderColor + ";" +
                            "-fx-border-width: 2px;" +
                            "-fx-border-radius: 16;" +
                            "-fx-alignment: center;"
            );
            towerUpgradeCostLabel.setWrapText(true);
            towerUpgradeCostLabel.setTextAlignment(TextAlignment.CENTER);
            towerUpgradeCostLabel.setEffect(new Glow(0.4));
            towerUpgradeCostLabel.setPrefWidth(200);
            towerUpgradeCostLabel.setPrefHeight(80);

            // Position the message just below the buttons
            double localX = 300; // Position to the left
            double localY = -10; // Position just below the buttons

            towerUpgradeCostLabel.setLayoutX(localX);
            towerUpgradeCostLabel.setLayoutY(localY);
            towerUpgradeCostLabel.setVisible(true);

            // Cancel any existing message timer
            if (messageTimer != null) {
                messageTimer.stop();
            }

            messageTimer = new PauseTransition(Duration.seconds(1));
            messageTimer.setOnFinished(e -> towerUpgradeCostLabel.setVisible(false));
            messageTimer.play();
        });
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
            MapObjectView objectView = controller.putObjectOnMapView(newTower);
            updateUI();

            int level = info[3];
            
            //while (level--) newTower.upgrade() tarzı bi yapı daha şık olabilir 2den çok seviye olacaksa
            if (level == 2) {
                newTower.upgrade();
                String newAsset = switch (newTower.getType()) {
                    case TOWER_ARCHER -> "src/main/resources/assets/tile64/archerl2.png";
                    case TOWER_MAGE -> "src/main/resources/assets/tile64/magel2.png";
                    case TOWER_ARTILLERY -> "src/main/resources/assets/tile64/artilleryl2.png";
                    default -> null;
                };
                if (newAsset != null) {
                    objectView.setImageFromPath(newAsset);
                }

                updateUI();
            }
        }
    }

    private static final Glow HOVER_GLOW = new Glow(0.5);

    private void initializeButtons() {
        configurePauseButton();
        configureSpeedButton();
        configureExitButton();
        configureRestartButton();
        configureSaveButton();
        towerUpgradeButton.setOnMouseClicked(event -> {
            boolean wasUpgradeMode = controller.isUpgradeMode();
            controller.toggleUpgradeMode();
            if (controller.isUpgradeMode()) {
                towerUpgradeButton.setEffect(glow);
                towerUpgradeButton.setOnMouseExited(e -> {
                    if (controller.isUpgradeMode()) {
                        towerUpgradeButton.setEffect(glow);
                    }
                });

                if (selectedTowerButton != null) {
                    selectedTowerButton.setEffect(null);
                    selectedTowerButton = null;
                    selectedTowerType = null;
                    hideRangePreview();
                }
                Platform.runLater(() -> tryUpgradeTower(null));
            } else {
                towerUpgradeButton.setEffect(null);
                // Show message when upgrade mode is disabled
                showTowerMessage("Upgrade mode disabled", "#f4e9bc", "#5e4b1c");
            }
        });

        setupHoverEffect(towerUpgradeButton);
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
        saveButton.setOnMouseClicked(event -> controller.getSelection());
        setupHoverEffect(saveButton);
        deactivateSaveButton();
    }

    public void deactivateSaveButton() {
        saveButton.setDisable(true);
        saveButton.setOpacity(0.5);
    }

    public void activateSaveButton() {
        saveButton.setDisable(false);
        saveButton.setOpacity(1);
    }

    private void setupHoverEffect(ImageView button) {
        button.setOnMouseEntered(e -> button.setEffect(HOVER_GLOW));
        button.setOnMouseExited(e -> button.setEffect(null));
        pauseButton.setOnMouseClicked(event -> controller.pauseGame(pauseButton));
        speedButton.setOnMouseClicked(event -> controller.toggleSpeed(speedLabel));
        exitButton.setOnMouseClicked(event -> handleExit());
        restartButton.setOnMouseClicked(event -> restartGame());
        saveButton.setOnMouseClicked(event -> controller.getSelection());
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

    public void showGameOver(boolean success, boolean campaign) {
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
            if(campaign) {
                nextLevelBtn.setVisible(true);
                nextLevelBtn.setDisable(false);
            }
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
                toRemove.add(view);
                if (Math.random() < 0.3) {
                    int bagAmount = new Random().nextInt(enemy.getGoldReward()) + 1;
                    double x = view.getTranslateX();
                    double y = view.getTranslateY();
                    GoldBagView bag = new GoldBagView(bagAmount, x, y);
                    enemyLayer.getChildren().add(bag);
                } else {
                    controller.rewardPlayer(enemy.getGoldReward());
                }
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
                        showGameOver(false,true);
                    } else if (controller.getGameState()==1) {
                        int level = LevelManager.getCurrentLevel();

                        if (controller.getLevel() == level & controller.getGameSession().isCampaign()) {
                            LevelManager.saveLevel(++level);
                            showGameOver(true,true);
                        }

                        showGameOver(true,false);
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

        // Deselect upgrade button when tower button is clicked
        if (controller != null && controller.isUpgradeMode()) {
            controller.toggleUpgradeMode();
            towerUpgradeButton.setEffect(null);
        }

        // Select new button
        selectedTowerButton = clickedButton;
        selectedTowerButton.setEffect(glow);
        selectedTowerType = MapObjectType.valueOf((String)clickedButton.getUserData());
    }


    public void updateRemainingTime(String time) {
        remainingTimeLabel.setText(time);
    }

    private class GoldBagView extends Group {
        private final int goldAmount;
        private final List<Image> frames = new ArrayList<>();
        private final ImageView imageView;
        private int frameIndex = 0;

        public GoldBagView(int goldAmount, double x, double y) {
            this.goldAmount = goldAmount;
            
            // Load all 7 goldbag frames
            for (int i = 0; i < 7; i++) {
                String path = String.format("file:src/main/resources/assets/enemies/goldbag/goldbag00%d.png", i);
                frames.add(new Image(path));
            }

            imageView = new ImageView(frames.get(0));
            imageView.setFitWidth(48);
            imageView.setFitHeight(48);
            getChildren().add(imageView);

            setTranslateX(x - 24);
            setTranslateY(y - 24);

            Timeline anim = new Timeline(new KeyFrame(Duration.millis(100), e -> {
                frameIndex = (frameIndex + 1) % frames.size();
                imageView.setImage(frames.get(frameIndex));
            }));
            anim.setCycleCount(Timeline.INDEFINITE);
            anim.play();

            Timeline removalTimer = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
                enemyLayer.getChildren().remove(this);
            }));
            removalTimer.setCycleCount(1);
            removalTimer.play();

            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                controller.rewardPlayer(goldAmount);
                updateUI();
                enemyLayer.getChildren().remove(this);
            });
        }
    }

    public void showSaveDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Save Game");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label("Select a save file");
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        vbox.getChildren().add(label);

        List<File> saveFiles = SaveService.getSaveFiles();

        for (int i = 0; i < 3; i++) {
            String name;
            final boolean isEmpty;
            if (i < saveFiles.size() && saveFiles.get(i).exists()) {
                File file = saveFiles.get(i);
                name = file.getName();
                name = name.substring(0, name.lastIndexOf(".kutdsave"));
                isEmpty = false;
            } else {
                name = "Empty Slot" + i;
                isEmpty = true;
            }

            Button btn = new Button(name);
            final int slotIndex = i + 1;
            btn.setMaxWidth(Double.MAX_VALUE);

            btn.setOnAction(e -> {
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setTitle(null);
                inputDialog.setHeaderText("Enter save name:");
                inputDialog.setContentText("Save name:");

                Optional<String> result = inputDialog.showAndWait();
                if (result.isPresent()) {
                    String saveName = result.get().trim();
                    if (!saveName.isEmpty()) {                        
                        String alertText = "Are you sure you want to save as \"" + saveName + "\"?";
                        if (!isEmpty) alertText += "\nThis will overwrite existing data!";

                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle(null);
                        confirm.setHeaderText(null);
                        confirm.setContentText(alertText);

                        Optional<ButtonType> confirmation = confirm.showAndWait();
                        if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                            dialog.close();
                            
                            if (!isEmpty) {
                                String path = "src/main/resources/saves/" + btn.getText() + ".kutdsave";
                                File file = new File(path);

                                if (file.exists()) file.delete();
                            }

                            controller.saveGame(saveName);
                        }
                    }
                }
            });

            vbox.getChildren().add(btn);
        }

        Scene scene = new Scene(vbox, 300, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}