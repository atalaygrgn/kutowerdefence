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

public class UltraFastState implements SpeedState{
    GameSession gameSession;
    WaveController controller;

    public UltraFastState(GameSession gameSession) {
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
            enemy.setSpeed(currentSpeed/8);
        }

        float speed = EnemyFactory.GOBLIN.getSpeed();
        EnemyFactory.GOBLIN.setSpeed(speed/8);
        speed = EnemyFactory.KNIGHT.getSpeed();
        EnemyFactory.KNIGHT.setSpeed(speed/8);

        int currentDuration = ProjectileView.getAnimationDuration();
        ProjectileView.setAnimationDuration(8*currentDuration);
        
        long cooldown = Tower.getCooldown();
        Tower.setCooldown(8*cooldown);

        WaveDescription description = controller.getDescription();
        float delay = description.getDelay();

        Wave wave = controller.getCurrentWave();
        if (wave != null) wave.setDelay(delay*8);
        
        description.setDelay(delay*8);
        EnemyGroupFactory.setDelay(EnemyGroupFactory.getDelay()*8);

        List<Timeline> timelines = controller.getActiveTimelines();
        for (Timeline timeline : timelines) {
            timeline.setRate(0.5);
        }
        
        gameSession.setSpeedState(gameSession.getSlowState());
    }
    
    public String getNextText() {
        return "x0.5";
    }
}
