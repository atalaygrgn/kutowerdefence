package com.canja.kutowerdefence.controller;

import com.canja.kutowerdefence.domain.WaveDescription;
import com.canja.kutowerdefence.ui.EnemyView;
import com.canja.kutowerdefence.ui.GamePlayView;
import com.canja.kutowerdefence.domain.Enemy;
import com.canja.kutowerdefence.domain.EnemyDescription;
import com.canja.kutowerdefence.domain.EnemyGroup;
import com.canja.kutowerdefence.domain.EnemyGroupFactory;
import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.domain.Option;
import com.canja.kutowerdefence.domain.Point;
import com.canja.kutowerdefence.domain.Wave;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the scheduling and spawning of waves and enemy groups during the game.
 */
public class WaveController {

    private final GameSession gameSession;
    private GamePlayView view;
    private final LinkedList<Point> enemyPath;
    private final List<Timeline> activeTimelines;
    private final WaveDescription waveDescription;
    private Wave wave;

    private int delayBetweenWaves;
    private int waveReward = 50;
    private int enemyIndex;

    public WaveController(GameSession gameSession) {
        this.gameSession = gameSession;
        this.enemyPath = gameSession.getMap().getPath();
        this.activeTimelines = new ArrayList<>();

        int[] options = gameSession.getOptionValues();

        waveDescription = new WaveDescription(
            options[Option.WAVE_GROUP_COUNT.ordinal()],
            options[Option.ENEMY_GROUP_SIZE.ordinal()],
            waveReward, 
            options[Option.WAVE_GROUP_DELAY.ordinal()]
        );

        delayBetweenWaves = options[Option.WAVE_DELAY.ordinal()];
        EnemyGroupFactory.setDelay(options[Option.ENEMY_SPAWN_DELAY.ordinal()]);

        setStates();
    }

    public void setStates() {
        gameSession.getSlowState().setWaveController(this);
        gameSession.getNormalState().setWaveController(this);
        gameSession.getFastState().setWaveController(this);
        gameSession.getUltraFastState().setWaveController(this);
    }

    public void setView(GamePlayView view) {
        this.view = view;
    }

    public WaveDescription getDescription() {
        return waveDescription;
    }
    
    public Wave getCurrentWave() {
        return wave;
    }

    public void startWaves() {
        runWaves();
    }

    public boolean hasWaves() {
        return gameSession.getCurrentWave() <= gameSession.getWaveNumber();
    }

    private void runWaves() {
        if (!hasWaves()) return;

        Timeline initialDelay = new Timeline();
        IntegerProperty remainingSeconds = new SimpleIntegerProperty(delayBetweenWaves);

        KeyFrame countdownFrame = new KeyFrame(Duration.seconds(1), e -> {
            System.out.printf("Next wave in: %d seconds%n", remainingSeconds.get());
            remainingSeconds.set(remainingSeconds.get() - 1);
        });

        initialDelay.getKeyFrames().add(countdownFrame);
        initialDelay.setCycleCount(delayBetweenWaves);

        initialDelay.setOnFinished(e -> {
            wave = new Wave(waveDescription);
            spawnEnemyGroups(wave, () -> {
                int waveIndex = gameSession.getCurrentWave();
                gameSession.setCurrentWave(++waveIndex);
                view.updateUI();
                runWaves();  
            });
        });

        initialDelay.play();
        activeTimelines.add(initialDelay);
    }    

    private void spawnEnemyGroups(Wave wave, Runnable onFinished) {
        if (!wave.hasEnemies()) {
            onFinished.run();
            return;
        }

        wave.proceedNextGroup();
        EnemyGroup group = wave.getActiveGroup();
        System.out.printf("Current enemy group index is %d\n", wave.getCurrentGroup());
        enemyIndex = 0;
        spawnEnemyGroup(group, wave.getDelay(), () -> {
            spawnEnemyGroups(wave, onFinished);
        });
    }

    private void spawnEnemyGroup(EnemyGroup group, float delay, Runnable onFinished) {
        List<EnemyDescription> enemies = group.getEnemies();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(group.getDelay()), e -> {
            if (enemyIndex < enemies.size()) {
                EnemyDescription description = enemies.get(enemyIndex);
                System.out.printf("Enemy %s initialized with index %d and size %d\n", description.getName(), enemyIndex, enemies.size());
                Enemy enemy = new Enemy(description, enemyPath);
                gameSession.addEnemy(enemy);
                view.spawnEnemy(enemy);
                enemyIndex++;
            }
        }));
        timeline.setCycleCount(enemies.size());
        timeline.setOnFinished(e -> {
            activeTimelines.remove(timeline);
            Timeline delayTimeline = new Timeline(new KeyFrame(
                Duration.seconds(delay),  
                ev -> onFinished.run() 
            ));
            delayTimeline.setCycleCount(1);
            delayTimeline.setOnFinished(event -> activeTimelines.remove(delayTimeline));
            delayTimeline.play();
            activeTimelines.add(delayTimeline);
        });
        timeline.play();
        activeTimelines.add(timeline);
    }

    public List<Timeline> getActiveTimelines() {
        return activeTimelines;
    }

    public void stopAll() {
        for (Timeline t : activeTimelines) {
            t.stop();
        }
        activeTimelines.clear();
    }

    public void pauseWaves() {
        for (Timeline t : activeTimelines) {
            t.pause();
        }
    }

    public void resumeWaves() {
        for (Timeline t : activeTimelines) {
            t.play();
        }
    }
}