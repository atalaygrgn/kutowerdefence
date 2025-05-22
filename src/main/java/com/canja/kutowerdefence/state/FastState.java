package com.canja.kutowerdefence.state;

import java.util.List;

import com.canja.kutowerdefence.domain.Enemy;
import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.domain.Tower;
import com.canja.kutowerdefence.ui.ProjectileView;

public class FastState implements SpeedState{
    GameSession gameSession;

    public FastState(GameSession gameSession) {
        this.gameSession = gameSession;
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
        
        gameSession.setSpeedState(gameSession.getUltraFastState());
    }
    
    public String getNextText() {
        return "x4";
    }
}
