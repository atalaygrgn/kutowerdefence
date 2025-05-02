package com.canja.kutowerdefence.controller;

import com.canja.kutowerdefence.model.Wave;
import com.canja.kutowerdefence.model.EnemyGroup;
import com.canja.kutowerdefence.model.EnemySpec;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the scheduling and spawning of waves and enemy groups during the game.
 */
public class WaveController {

    private final GamePlayController gamePlayController;
    private final List<Wave> waves;
    private final List<Timeline> activeTimelines;

    private int currentWaveIndex = 0;

    public WaveController(GamePlayController gamePlayController, List<Wave> waves) {
        this.gamePlayController = gamePlayController;
        this.waves = waves;
        this.activeTimelines = new ArrayList<>();
    }

    public void startWaves() {
        if (waves.isEmpty()) return;

        double initialDelay = gamePlayController.getOptions().getInitialGraceSeconds();
        scheduleNextWave(initialDelay);
    }

    private void scheduleNextWave(double delaySeconds) {
        Timeline waveTimeline = new Timeline(new KeyFrame(
            Duration.seconds(delaySeconds),
            e -> runWave(currentWaveIndex)
        ));
        waveTimeline.setCycleCount(1);
        waveTimeline.play();
        activeTimelines.add(waveTimeline);
    }

    private void runWave(int waveIndex) {
        if (waveIndex >= waves.size()) return;

        Wave wave = waves.get(waveIndex);
        spawnGroups(wave);
        currentWaveIndex++;

        if (currentWaveIndex < waves.size()) {
            double delayBetweenWaves = gamePlayController.getOptions().getDelayBetweenWaves();
            scheduleNextWave(delayBetweenWaves);
        }
    }

    private void spawnGroups(Wave wave) {
        List<EnemyGroup> groups = wave.getGroups();
        spawnGroupAtIndex(groups, 0);
    }

    private void spawnGroupAtIndex(List<EnemyGroup> groups, int index) {
        if (index >= groups.size()) return;

        EnemyGroup group = groups.get(index);
        List<EnemySpec> specs = group.getEnemySpecs();

        for (int i = 0; i < specs.size(); i++) {
            EnemySpec spec = specs.get(i);
            int finalI = i;
            Timeline spawnEnemyTimeline = new Timeline(new KeyFrame(
                Duration.seconds(group.getDelayBetweenEnemies() * i),
                e -> gamePlayController.spawnEnemy(spec.getType())
            ));
            spawnEnemyTimeline.setCycleCount(1);
            spawnEnemyTimeline.play();
            activeTimelines.add(spawnEnemyTimeline);
        }

        Timeline nextGroupTimeline = new Timeline(new KeyFrame(
            Duration.seconds(group.getDelayAfterGroup()),
            e -> spawnGroupAtIndex(groups, index + 1)
        ));
        nextGroupTimeline.setCycleCount(1);
        nextGroupTimeline.play();
        activeTimelines.add(nextGroupTimeline);
    }

    public void stopAll() {
        for (Timeline t : activeTimelines) {
            t.stop();
        }
        activeTimelines.clear();
    }
}
