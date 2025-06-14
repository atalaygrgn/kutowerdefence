package com.canja.kutowerdefence.state;

import java.util.List;

import com.canja.kutowerdefence.domain.Enemy;
import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.domain.Tower;
import com.canja.kutowerdefence.ui.ProjectileView;

public abstract class AbstractSpeedState implements SpeedState {
    GameSession gameSession;
    
    public AbstractSpeedState(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    protected void speedMultiplier(float speedMultiplier) {        
        for (Enemy enemy : gameSession.getEnemies()) {
            enemy.setSpeed(enemy.getSpeed()*speedMultiplier);
        }

        int currentDuration = ProjectileView.getAnimationDuration();
        ProjectileView.setAnimationDuration((int) (currentDuration/speedMultiplier));
        
        long cooldown = Tower.getCooldown();
        Tower.setCooldown((int) (cooldown/speedMultiplier));
    }

    @Override
    public abstract void toggleSpeed();

    @Override
    public abstract String getNextText();
}
