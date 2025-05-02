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
    private final EnemyDescription goblin = EnemyFactory.GOBLIN;
    private final EnemyDescription knight = EnemyFactory.KNIGHT;
    private int waveNumber = 0;
    private int currentWave = 1;
    private GamePlayView view;
    private final Player player;

    public GamePlayController(GameSession gameSession) {
        this.gameSession = gameSession;
        int[] options = gameSession.getOptionValues();

        this.waveNumber = options[Option.WAVE_NUMBER.ordinal()];
        player = new Player(options[Option.GOLD.ordinal()], options[Option.PLAYER_HITPOINT.ordinal()]);
        configureGoblin(options);
        configureKnight(options);
    }
    
    public void configureGoblin(int[] options) {
        goblin.setGold(options[Option.GOBLIN_REWARD.ordinal()]);
        goblin.setHitpoints(options[Option.GOBLIN_HITPOINT.ordinal()]);
        goblin.setSpeed(options[Option.GOBLIN_SPEED.ordinal()]);
    }

    public void configureKnight(int[] options) {
        knight.setGold(options[Option.KNIGHT_REWARD.ordinal()]);
        knight.setHitpoints(options[Option.KNIGHT_HITPOINT.ordinal()]);
        knight.setSpeed(options[Option.KNIGHT_SPEED.ordinal()]);
    }

    public Player getPlayer() {
        return player;
    }

    public Map getMap() {
        return gameSession.getMap();
    }

    public int getHealth() {
        return player.getHealth();
    }

    public int getGold() {
        return player.getGoldAmount();
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
        gameSession.setGamePlayView(view);
    }


    public GameSession getGameSession() {
        return gameSession;
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
      gameSession.addEnemy(goblin);
      view.spawnEnemy(goblin);
      Enemy knight = new Enemy(EnemyFactory.KNIGHT, path);
      gameSession.addEnemy(knight);
      view.spawnEnemy(knight);





    }

    public void onEmptyLotClicked(TileView tileView, int x, int y) {
        TowerPopupPanel.show(x, y, selectedType -> {
            if (selectedType != null) {
                // deduct gold from player
                tileView.setTileType(TileType.EMPTY);
                Tower newTower = TowerFactory.createTower(selectedType, new Point(x, y), gameSession);
                gameSession.addTower(newTower);
                putObjectOnMapView(newTower);
            }
        });
    }


    private void putObjectOnMapView(MapObject mapObject) {
        MapObjectView newObjectView = new MapObjectView(mapObject);
        view.getMapGridPane().add(newObjectView, mapObject.getPosition().getX(), mapObject.getPosition().getY());

    }
}