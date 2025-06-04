package com.canja.kutowerdefence.state;

import java.util.List;

import com.canja.kutowerdefence.controller.WaveController;
import com.canja.kutowerdefence.domain.Enemy;
import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.domain.Tower;
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

        int currentDuration = ProjectileView.getAnimationDuration();
        ProjectileView.setAnimationDuration(8*currentDuration);
        
        long cooldown = Tower.getCooldown();
        Tower.setCooldown(8*cooldown);

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
