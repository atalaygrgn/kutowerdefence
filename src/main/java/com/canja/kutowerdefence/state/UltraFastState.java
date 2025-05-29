package com.canja.kutowerdefence.state;

import java.util.List;

import com.canja.kutowerdefence.domain.Enemy;
import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.domain.Tower;
import com.canja.kutowerdefence.ui.ProjectileView;

public class UltraFastState implements SpeedState{
    GameSession gameSession;

    public UltraFastState(GameSession gameSession) {
        this.gameSession = gameSession;
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
        
        gameSession.setSpeedState(gameSession.getSlowState());
    }
    
    public String getNextText() {
        return "x0.5";
    }
}
