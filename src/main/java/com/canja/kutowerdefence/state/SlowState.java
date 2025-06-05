package com.canja.kutowerdefence.state;

import java.util.List;

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

public class SlowState implements SpeedState{
    GameSession gameSession;
    WaveController controller;

    public SlowState(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public void setWaveController(WaveController controller) {
        this.controller = controller;
    }

    @Override
    public void toggleSpeed() {
        List<Enemy> enemies = gameSession.getEnemies();
        
        for (Enemy enemy : enemies) {
            float currentSpeed = enemy.getSpeed();
            enemy.setSpeed(2*currentSpeed);
        }

        float speed = EnemyFactory.GOBLIN.getSpeed();
        EnemyFactory.GOBLIN.setSpeed(speed*2);
        speed = EnemyFactory.KNIGHT.getSpeed();
        EnemyFactory.KNIGHT.setSpeed(speed*2);

        int currentDuration = ProjectileView.getAnimationDuration();
        ProjectileView.setAnimationDuration(currentDuration/2);
        
        long cooldown = Tower.getCooldown();
        Tower.setCooldown(cooldown/2);
        
        WaveDescription description = controller.getDescription();
        float delay = description.getDelay();
        
        Wave wave = controller.getCurrentWave();
        if (wave != null) wave.setDelay(delay/2);

        description.setDelay(delay/2);
        EnemyGroupFactory.setDelay(EnemyGroupFactory.getDelay()/2);

        List<Timeline> timelines = controller.getActiveTimelines();
        for (Timeline timeline : timelines) {
            timeline.setRate(1.0);
        }

        gameSession.setSpeedState(gameSession.getNormalState());
    }

    public String getNextText() {
        return "x1";
    }
}
