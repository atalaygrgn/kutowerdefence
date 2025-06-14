package com.canja.kutowerdefence.controller;

import com.canja.kutowerdefence.domain.WaveDescription;
import com.canja.kutowerdefence.domain.WaveFactory;
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
    private final WaveDescription waveDescription = WaveFactory.DEFAULT;
    private Wave wave;

    private int delayBetweenWaves;
    private int enemyIndex;
    private int waveIndex = 0;
    private float frameRate = 1f;

    public WaveController(GameSession gameSession) {
        this.gameSession = gameSession;
        this.enemyPath = gameSession.getMap().getPath();
        this.activeTimelines = new ArrayList<>();

        int[] options = gameSession.getOptionValues();

        configureWaves(options);
        delayBetweenWaves = options[Option.WAVE_DELAY.ordinal()];
        EnemyGroupFactory.setDelay(options[Option.ENEMY_SPAWN_DELAY.ordinal()]);

        setStates();
    }

    public void configureWaves(int[] options) {
        waveDescription.setDelay(options[Option.WAVE_GROUP_DELAY.ordinal()]);
        waveDescription.setEnemyGroupNumber(options[Option.WAVE_GROUP_COUNT.ordinal()]);
        waveDescription.setEnemyGroupSize(options[Option.ENEMY_GROUP_SIZE.ordinal()]);
    }

    public void setStates() {
        gameSession.getSlowState().setWaveController(this);
        gameSession.getNormalState().setWaveController(this);
        gameSession.getFastState().setWaveController(this);
        gameSession.getUltraFastState().setWaveController(this);
        gameSession.getPausedState().setWaveController(this);
        gameSession.getPlayingState().setWaveController(this);
    }

    public void setView(GamePlayView view) {
        this.view = view;
    }

    public WaveDescription getDescription() {
        return waveDescription;
    }

    public float getRate() {
        return frameRate;
    }

    public void setRate(float rate) {
        frameRate = rate;
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

        gameSession.setWaveState(false);
        IntegerProperty remainingSeconds = new SimpleIntegerProperty(delayBetweenWaves);
        Timeline initialDelay = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            System.out.printf("Next wave in: %d seconds%n", remainingSeconds.get());
            remainingSeconds.set(remainingSeconds.get() - 1);
        }));
        initialDelay.setCycleCount(delayBetweenWaves);
        initialDelay.setRate(frameRate);
        initialDelay.setOnFinished(e -> {
            activeTimelines.remove(initialDelay);
            gameSession.setWaveState(true);
            gameSession.setCurrentWave(++waveIndex);
            view.updateUI();
            wave = new Wave(waveDescription);
            spawnEnemyGroups(wave, () -> {
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
        timeline.setRate(frameRate);
        timeline.setOnFinished(e -> {
            activeTimelines.remove(timeline);
            Timeline delayTimeline = new Timeline(new KeyFrame(
                Duration.seconds(delay),  
                ev -> onFinished.run() 
            ));
            delayTimeline.setCycleCount(1);
            delayTimeline.setRate(frameRate);
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

    public void restartWaves() {
        stopAll();

        int[] options = gameSession.getOptionValues();

        EnemyGroupFactory.setDelay(options[Option.ENEMY_SPAWN_DELAY.ordinal()]);
        configureWaves(options);

        startWaves();
    }
}