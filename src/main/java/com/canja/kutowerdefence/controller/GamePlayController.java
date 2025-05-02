package com.canja.kutowerdefence.controller;

import com.canja.kutowerdefence.domain.*;
import com.canja.kutowerdefence.ui.GamePlayView;
import com.canja.kutowerdefence.ui.MapObjectView;
import com.canja.kutowerdefence.ui.TileView;
import com.canja.kutowerdefence.ui.TowerPopupPanel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.scene.input.MouseButton;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;


public class GamePlayController {
    private final GameSession gameSession;
    private final EnemyDescription goblin = EnemyFactory.GOBLIN;
    private final EnemyDescription knight = EnemyFactory.KNIGHT;
    private int waveNumber = 0;
    private int currentWave = 1;
    private int archerCost;
    private int artilleryCost;
    private int mageCost;
    private GamePlayView view;
    private final Player player;
    private boolean isGamePaused;

    public GamePlayController(GameSession gameSession) {
        this.gameSession = gameSession;
        int[] options = gameSession.getOptionValues();

        this.waveNumber = options[Option.WAVE_NUMBER.ordinal()];
        player = new Player(options[Option.GOLD.ordinal()], options[Option.PLAYER_HITPOINT.ordinal()]);
        configureTowers(options);
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

    public void configureTowers(int[] options) {
        TowerFactory.setRange(options[Option.TOWER_RANGE.ordinal()], options[Option.TOWER_RANGE.ordinal()], options[Option.TOWER_RANGE.ordinal()]);
        TowerFactory.setDamage(options[Option.ARROW_DAMAGE.ordinal()], options[Option.ARTILLERY_DAMAGE.ordinal()], options[Option.SPELL_DAMAGE.ordinal()]);
        TowerFactory.setAoeRadius(options[Option.AOE_RANGE.ordinal()]);

        this.archerCost = options[Option.ARCHER_TOWER_COST.ordinal()];
        this.artilleryCost = options[Option.ARTILLERY_TOWER_COST.ordinal()];
        this.mageCost = options[Option.MAGE_TOWER_COST.ordinal()];
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

    public void setCurrentWave(int val) {
        this.currentWave = val;
    }

    public String getWaveInfo() {
        String waveInfo = currentWave + "/" + waveNumber;

        return waveInfo;
    }
    
    public boolean getPauseState() {
        return isGamePaused;
    }

    public void setView(GamePlayView view) {
        this.view = view;
        gameSession.setGamePlayView(view);
    }


    public GameSession getGameSession() {
        return gameSession;
    }

    public void loseHealth() {
        player.loseHealth();
    }

    public void rewardPlayer(int val) {
        player.gainGold(val);
    }

    public void pauseGame() {
        // Implement pause logic
        isGamePaused = !isGamePaused;
        System.out.println("Game Paused");
    }

    public void restartGame() {
        // Implement restart logic
        System.out.println("Game Restarted");
    }

    public void saveGame() {
        String filename = "src/main/resources/saves/save.kutdsave";
        String mapPath = gameSession.getMapPath();
        String optionPath = gameSession.getOptionPath();

        Gson gson = new GsonBuilder().create();

        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(new Object[]{mapPath, optionPath, currentWave}, writer);
            System.out.println("Game saved successfully to " + filename);
        } catch (IOException e) {
            System.err.println("Failed to save map: " + e.getMessage());
        }
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
                int requiredGold = 0;
                
                switch (selectedType) {
                    case TOWER_ARCHER:
                        requiredGold = archerCost;
                        break;
                    case TOWER_ARTILLERY:
                        requiredGold = artilleryCost;
                        break;
                    case TOWER_MAGE:
                        requiredGold = mageCost;
                }

                if (player.getGoldAmount() < requiredGold) return;

                tileView.setTileType(TileType.EMPTY);
                Tower newTower = TowerFactory.createTower(selectedType, new Point(x, y), gameSession);

                gameSession.addTower(newTower);
                putObjectOnMapView(newTower);
                player.deductGold(requiredGold);
                view.updateUI();
            }
        });
    }

    private void putObjectOnMapView(MapObject mapObject) {
        MapObjectView newObjectView = new MapObjectView(mapObject);
        view.getMapGridPane().add(newObjectView, mapObject.getPosition().getX(), mapObject.getPosition().getY());
    }
}