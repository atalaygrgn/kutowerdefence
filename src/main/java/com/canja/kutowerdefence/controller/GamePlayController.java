package com.canja.kutowerdefence.controller;

import com.canja.kutowerdefence.domain.*;
import com.canja.kutowerdefence.state.FlowState;
import com.canja.kutowerdefence.state.SpeedState;
import com.canja.kutowerdefence.ui.GamePlayView;
import com.canja.kutowerdefence.ui.MapObjectView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public class GamePlayController {
    private final GameSession gameSession;
    private final EnemyDescription goblin = EnemyFactory.GOBLIN;
    private final EnemyDescription knight = EnemyFactory.KNIGHT;
    private GamePlayView view;
    private boolean upgradeMode = false;

    public void toggleUpgradeMode() {
        upgradeMode = !upgradeMode;
        System.out.println("Upgrade mode: " + upgradeMode);
    }

    public boolean isUpgradeMode() {
        return upgradeMode;
    }


    public void onTowerClicked(Tower tower, MapObjectView view) {
        if (!upgradeMode) return;

        if (tower.canUpgrade()) {
            tower.upgrade();


            String assetPath = switch (tower.getType()) {
                case TOWER_ARCHER -> "/assets/towers/archerl2.png";
                case TOWER_MAGE -> "/assets/towers/magel2.png";
                case TOWER_ARTILLERY -> "/assets/towers/artilleryl2.png";
                default -> null;
            };

            if (assetPath != null) {
                String fullPath = switch (tower.getType()) {
                    case TOWER_ARCHER -> "src/main/resources/assets/tile64/archerl2.png";
                    case TOWER_MAGE -> "src/main/resources/assets/tile64/magel2.png";
                    case TOWER_ARTILLERY -> "src/main/resources/assets/tile64/artilleryl2.png";
                    default -> null;
                };

                if (fullPath != null) {
                    view.setImageFromPath(fullPath);
                }
            }


            this.view.updateUI();
        } else {
            System.out.println("Upgrade not possible (already upgraded or not enough gold).");
        }


        upgradeMode = false;
    }


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

    public int getLevel() {
        return gameSession.getLevel();
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
        return gameSession.getFlowState() == gameSession.getPausedState();
    }

    public int getGameState(){return gameSession.isGameOver();}

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

    public void pauseGame(ImageView clickedButton) {
        FlowState state = gameSession.getFlowState();

        state.togglePauseState();
        String path = state.getNextIcon();
        view.setButtonImage(clickedButton,path);
    }

    public void toggleSpeed(Label speedLabel) {
        SpeedState currentState = gameSession.getSpeedState();
        currentState.toggleSpeed();
        String text = currentState.getNextText();
        speedLabel.setText(" " + text + " ");
    }

    public void getSelection() {
        if (gameSession.isWaveActive()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Cannot save while a wave is active!");
            alert.showAndWait();
            return;
        }

        if (!gameSession.getEnemies().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Cannot save while there are enemies alive!");
            alert.showAndWait();
            return;
        }

        view.showSaveDialog();
    }

    public void saveGame(String saveName) {
        

        String filename = "src/main/resources/saves/" + saveName + ".kutdsave";
        String mapPath = gameSession.getMapPath();

        int[] options = gameSession.getOptionValues();
        int[] playerInfo = {gameSession.getPlayerGold(), gameSession.getPlayerHitpoint()};
        options[Option.CURRENT_WAVE.ordinal()] = gameSession.getCurrentWave();
    
        List<Tower> activeTowers = gameSession.getTowers();
        List<int[]> towerInfo = new ArrayList<>();

        for (Tower tower : activeTowers) {
            int[] info = {tower.getType().ordinal(), tower.getPosition().getX(), tower.getPosition().getY(), tower.getLevel().ordinal()};
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

    /* DEPRECATED
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
    */

    public void updateGameState(){
        if(getHealth()<=0) {
            gameSession.setGameOver(2);
            //view.showGameOver(false);
        } else if (gameSession.getCurrentWave() == gameSession.getWaveNumber() && !gameSession.isWaveActive() && gameSession.getEnemies().isEmpty()){
            gameSession.setGameOver(1);
            //view.showGameOver(true);
        }
    }
    public MapObjectView putObjectOnMapView(MapObject mapObject) {
        MapObjectView newObjectView = new MapObjectView(mapObject);
        view.getMapGridPane().add(newObjectView, mapObject.getPosition().getX(), mapObject.getPosition().getY());
        return newObjectView;
    }

    public void restartGameSession() {
        gameSession.resetSession();
    }

    public boolean buyNewTower(int finalI, int finalJ, MapObjectType selectedTowerType) {
        return gameSession.buyNewTower(finalI, finalJ, selectedTowerType);
    }

    public void setStates() {
        gameSession.getPlayingState().setGamePlayController(this);
        gameSession.getPausedState().setGamePlayController(this);
    }

    public GamePlayView getView() {
        return view;
    }
}