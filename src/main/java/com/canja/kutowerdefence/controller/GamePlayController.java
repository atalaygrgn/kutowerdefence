package com.canja.kutowerdefence.controller;

import com.canja.kutowerdefence.domain.*;
import com.canja.kutowerdefence.ui.GamePlayView;
import com.canja.kutowerdefence.ui.MapObjectView;
import com.canja.kutowerdefence.ui.TileView;
import com.canja.kutowerdefence.ui.TowerPopupPanel;
import javafx.scene.input.MouseButton;

import java.util.LinkedList;


public class GamePlayController {
    private final GameSession gameSession;
    private int waveNumber = 0;
    private int goldAmount = 0;
    private int currentHealth = -1;
    private int currentWave = 1;
    private GamePlayView view;

    public GamePlayController(GameSession gameSession) {
        this.gameSession = gameSession;
        int[] options = gameSession.getOptionValues();
        this.waveNumber = options[0];
        this.goldAmount = options[4];
        this.currentHealth = options[10];
    }

    public Map getMap() {
        return gameSession.getMap();
    }

    public int getHealth() {
        return currentHealth;
    }

    public void setHealth(int value) {
        this.currentHealth = value;
    }

    public int getGold() {
        return goldAmount;
    }

    public void setGold(int value) {
        this.goldAmount = value;
    }

    public void setWaveNumber(int value) {
        this.waveNumber = value;
    }

    public String getWaveInfo() {
        String waveInfo = currentWave + "/" + waveNumber;

        return waveInfo;
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

    public void onEmptyLotClicked(TileView tileView, int x, int y) {
        TowerPopupPanel.show(x, y, selectedType -> {
            if (selectedType != null) {
                // deduct gold from player
                tileView.setTileType(TileType.EMPTY);
                Tower newTower = TowerFactory.createTower(selectedType, new Point(x, y));
                putObjectOnMapView(newTower);
            }
        });
    }


    private void putObjectOnMapView(MapObject mapObject) {
        MapObjectView newObjectView = new MapObjectView(mapObject);
        view.getMapGridPane().add(newObjectView, mapObject.getPosition().getX(), mapObject.getPosition().getY());

    }
}