package com.canja.kutowerdefence.state;

import java.util.List;

import com.canja.kutowerdefence.controller.WaveController;
import com.canja.kutowerdefence.domain.Enemy;
import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.domain.Tower;
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

        int currentDuration = ProjectileView.getAnimationDuration();
        ProjectileView.setAnimationDuration(currentDuration/2);
        
        long cooldown = Tower.getCooldown();
        Tower.setCooldown(cooldown/2);
        
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
