package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.controller.GamePlayController;
import com.canja.kutowerdefence.controller.WaveController;
import com.canja.kutowerdefence.domain.Enemy;
import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.ui.EnemyView;
import com.canja.kutowerdefence.ui.KuTowerView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.Group;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class GamePlayView implements Initializable {

    // Gold bag drop feature constants
    private static final double GOLD_BAG_DROP_CHANCE = 0.5;        // %50 şans
    private static final String GOLD_BAG_SPRITE_PATH = "/assets/items/goldbag/";

    @FXML private Pane enemyLayer;
    @FXML private GridPane mapGridPane;
    @FXML private Label healthLabel;
    @FXML private Label goldLabel;
    @FXML private Label waveLabel;
    @FXML private Button pauseButton, speedButton, exitButton, saveButton, restartButton;
    @FXML private ImageView healthIcon, goldIcon, waveIcon;
    @FXML private VBox gameOverOverlay;
    @FXML private Button restartGameOverBtn, exitGameOverBtn;
    @FXML private Label enemiesKilledLabel;

    private GamePlayController controller;
    private WaveController waveController;
    private final List<EnemyView> enemyViews = new ArrayList<>();
    private KuTowerView kuTowerView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pauseButton.setOnAction(e -> controller.togglePause());
        speedButton.setOnAction(e -> controller.toggleSpeed());
        restartButton.setOnAction(e -> controller.restart());
        exitButton.setOnAction(e -> controller.exitToMenu());
        saveButton.setOnAction(e -> controller.saveGame());
        restartGameOverBtn.setOnAction(e -> controller.restart());
        exitGameOverBtn.setOnAction(e -> controller.exitToMenu());
        goldIcon.setImage(new Image("file:src/main/resources/assets/ui/button/0.png"));
        healthIcon.setImage(new Image("file:src/main/resources/assets/ui/button/1.png"));
        waveIcon.setImage(new Image("file:src/main/resources/assets/ui/button/2.png"));
    }

    public void setController(GamePlayController controller, WaveController waveController) {
        this.controller = controller;
        this.waveController = waveController;
        initializeMapGrid();
        updateUI();
        enemyLayer.prefWidthProperty().bind(mapGridPane.widthProperty());
        enemyLayer.prefHeightProperty().bind(mapGridPane.heightProperty());
        waveController.startWaves();
        startEnemyUpdateLoop();
    }

    private void initializeMapGrid() {
        // ... mevcut harita satır ve tile init kodu ...
    }

    private void updateUI() {
        healthLabel.setText(String.valueOf(controller.getHealth()));
        goldLabel.setText(String.valueOf(controller.getGold()));
        waveLabel.setText(String.valueOf(waveController.getCurrentWave()));
        enemiesKilledLabel.setText(String.valueOf(controller.getEnemiesKilled()));
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
                if (!controller.isPaused()) {
                    float dt = (now - lastTime) / 1_000_000_000f;
                    GameSession session = controller.getGameSession();
                    session.tick(dt);

                    List<EnemyView> toRemove = new ArrayList<>();
                    for (EnemyView view : new ArrayList<>(enemyViews)) {
                        Enemy enemy = view.getEnemy();
                        enemy.update(dt);
                        view.update();

                        if (view.isDead()) {
                            toRemove.add(view);
                            if (Math.random() < GOLD_BAG_DROP_CHANCE) {
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
                            toRemove.add(view);
                        }
                    }

                    for (EnemyView ev : toRemove) {
                        enemyLayer.getChildren().remove(ev);
                        enemyViews.remove(ev);
                        updateUI();
                    }
                }
                lastTime = now;
            }
        };
        timer.start();
    }

    // ... diğer mevcut metotlar (proje spawn, tuş kontrol vs.) ...

    /**
     * Animasyonlu çanta, tıklanınca içindeki altını verir.
     */
    private class GoldBagView extends Group {
        private final int goldAmount;
        private final List<Image> frames = new ArrayList<>();
        private final ImageView imageView;
        private int frameIndex = 0;

        public GoldBagView(int goldAmount, double x, double y) {
            this.goldAmount = goldAmount;
            for (int i = 0; ; i++) {
                String res = GOLD_BAG_SPRITE_PATH + i + ".png";
                if (getClass().getResourceAsStream(res) == null) break;
                frames.add(new Image(getClass().getResourceAsStream(res)));
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

            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                controller.rewardPlayer(goldAmount);
                updateUI();
                enemyLayer.getChildren().remove(this);
            });
        }
    }
}