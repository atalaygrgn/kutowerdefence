package com.canja.kutowerdefence.state;

import com.canja.kutowerdefence.controller.WaveController;
import com.canja.kutowerdefence.domain.Enemy;
import com.canja.kutowerdefence.domain.EnemyFactory;
import com.canja.kutowerdefence.domain.EnemyGroupFactory;
import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.domain.Tower;
import com.canja.kutowerdefence.domain.Wave;
import com.canja.kutowerdefence.domain.WaveDescription;
import com.canja.kutowerdefence.ui.ProjectileView;

import javafx.animation.Timeline;

public abstract class AbstractSpeedState implements SpeedState {
    GameSession gameSession;
    WaveController controller;
    
    public AbstractSpeedState(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public void setWaveController(WaveController controller) {
        this.controller = controller;
    }

    protected void speedMultiplier(float speedMultiplier) {        
        for (Enemy enemy : gameSession.getEnemies()) {
            enemy.setSpeed(enemy.getSpeed()*speedMultiplier);
        }

        float speed = EnemyFactory.GOBLIN.getSpeed();
        EnemyFactory.GOBLIN.setSpeed(speed*speedMultiplier);

        speed = EnemyFactory.KNIGHT.getSpeed();
        EnemyFactory.KNIGHT.setSpeed(speed*speedMultiplier);

        int currentDuration = ProjectileView.getAnimationDuration();
        ProjectileView.setAnimationDuration((int) (currentDuration/speedMultiplier));
        
        long cooldown = Tower.getCooldown();
        Tower.setCooldown((int) (cooldown/speedMultiplier));

        WaveDescription description = controller.getDescription();
        float delay = description.getDelay();

        Wave wave = controller.getCurrentWave();
        if (wave != null) wave.setDelay(delay/speedMultiplier);

        description.setDelay(delay/speedMultiplier);
        EnemyGroupFactory.setDelay(EnemyGroupFactory.getDelay()/speedMultiplier);

        for (Timeline timeline : controller.getActiveTimelines()) {
            timeline.setRate(timeline.getRate()*speedMultiplier);
        }
    }

    @Override
    public abstract void toggleSpeed();

    @Override
    public abstract String getNextText();
}
