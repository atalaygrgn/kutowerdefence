package com.canja.kutowerdefence.controller;

import com.canja.kutowerdefence.domain.*;
import com.canja.kutowerdefence.state.SpeedState;
import com.canja.kutowerdefence.ui.GamePlayView;
import com.canja.kutowerdefence.ui.MapObjectView;
import com.canja.kutowerdefence.ui.ProjectileView;
import com.canja.kutowerdefence.ui.TileView;
import com.canja.kutowerdefence.ui.TowerPopupPanel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class GamePlayController {
    private final GameSession gameSession;
    private final EnemyDescription goblin = EnemyFactory.GOBLIN;
    private final EnemyDescription knight = EnemyFactory.KNIGHT;
    private GamePlayView view;

    public GamePlayController(GameSession gameSession) {
        this.gameSession = gameSession;
        int[] options = gameSession.getOptionValues();
    
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
        TowerFactory.setCost(options[Option.ARCHER_TOWER_COST.ordinal()], options[Option.ARTILLERY_TOWER_COST.ordinal()], options[Option.MAGE_TOWER_COST.ordinal()]);
        TowerFactory.setAoeRadius(options[Option.AOE_RANGE.ordinal()]);
    }

    public Map getMap() {
        return gameSession.getMap();
    }

    public int getHealth() {
        return gameSession.getPlayer().getHealth();
    }

    public int getGold() {
        return gameSession.getPlayer().getGoldAmount();
    }

    public String getWaveInfo() {
       return gameSession.getWaveInfo();
    }
    
    public boolean getPauseState() {
        return gameSession.getPauseState();
    }

    public boolean getGameState(){return gameSession.isGameOver();}

    public void setView(GamePlayView view) {
        this.view = view;
        gameSession.setGamePlayView(view);
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void loseHealth() {
        gameSession.loseHealth();
    }

    public void rewardPlayer(int val) {
        gameSession.rewardPlayer(val);
    }

    public void pauseGame() {
        gameSession.togglePauseState();

        if (gameSession.getPauseState()) {
            view.getWaveController().pauseWaves();
        }
        else {
            view.getWaveController().resumeWaves();
        }
    }

    public void toggleSpeed(Button clickedButton) {
        SpeedState currentState = gameSession.getSpeedState();
        
        currentState.toggleSpeed();
        String text = currentState.getNextText();

        clickedButton.setText(text);
    }

    public void saveGame() {
        List<File> saveFiles = SaveService.getSaveFiles();
        String filename = "src/main/resources/saves/save" + String.valueOf(saveFiles.size() + 1) + ".kutdsave";
        String mapPath = gameSession.getMapPath();

        int[] options = gameSession.getOptionValues();
        int[] playerInfo = {gameSession.getPlayerGold(), gameSession.getPlayerHitpoint()};
        options[Option.CURRENT_WAVE.ordinal()] = gameSession.getCurrentWave();
    
        List<Tower> activeTowers = gameSession.getTowers();
        List<int[]> towerInfo = new ArrayList<>();

        for (Tower tower : activeTowers) {
            int[] info = {tower.getType().ordinal(), tower.getPosition().getX(), tower.getPosition().getY()};
            towerInfo.add(info);
        }

        Gson gson = new GsonBuilder().create();

        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(new Object[]{mapPath, options, towerInfo, playerInfo}, writer);
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
                boolean success = gameSession.buyNewTower(x, y, selectedType);

                if (!success) return;
                
                tileView.setTileType(TileType.EMPTY);
                Tower newTower = gameSession.getNewestTower();
                putObjectOnMapView(newTower);
                view.updateUI();
            }
        });
    }

    public void updateGameState(){
        if(getHealth()<=0 || (gameSession.getCurrentWave() > gameSession.getWaveNumber() && gameSession.getEnemies().isEmpty())) {
            gameSession.setGameOver(true);
        }
    }
    public void putObjectOnMapView(MapObject mapObject) {
        MapObjectView newObjectView = new MapObjectView(mapObject);
        view.getMapGridPane().add(newObjectView, mapObject.getPosition().getX(), mapObject.getPosition().getY());
    }

    public void restartGameSession() {
        gameSession.resetSession();
    }
}