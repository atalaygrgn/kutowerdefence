package com.canja.kutowerdefence.controller;

import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.domain.Map;

public class GamePlayController {
    private final GameSession gameSession;

    public GamePlayController(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public Map getMap() {
        return gameSession.getMap();
    }

    public int getHealth() {
        // Placeholder for health logic
        return 100;
    }

    public int getGold() {
        // Placeholder for gold logic
        return 500;
    }

    public String getWaveInfo() {
        // Placeholder for wave info logic
        return "1/10";
    }

    public void pauseGame() {
        // Implement pause logic
        System.out.println("Game Paused");
    }

    public void restartGame() {
        // Implement restart logic
        System.out.println("Game Restarted");
    }
}
