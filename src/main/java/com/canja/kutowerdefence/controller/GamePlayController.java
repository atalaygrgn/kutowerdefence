package com.canja.kutowerdefence.controller;

import com.canja.kutowerdefence.domain.*;
import com.canja.kutowerdefence.ui.GamePlayView;

import java.util.LinkedList;


public class GamePlayController {
    private final GameSession gameSession;

    private GamePlayView view;

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

    public void setView(GamePlayView view) {
        this.view = view;
    }

    public void pauseGame() {
        // Implement pause logic
        System.out.println("Game Paused");
    }

    public void restartGame() {
        // Implement restart logic
        System.out.println("Game Restarted");
    }

    public void spawnTestEnemy() {
        //for testing enemy movement
        LinkedList<Point> path = gameSession.getMap().getPath();

        Enemy goblin = new Enemy(EnemyFactory.GOBLIN, path);
        view.spawnEnemy(goblin);
        Enemy knight = new Enemy(EnemyFactory.KNIGHT, path);
        view.spawnEnemy(knight);
    }

}