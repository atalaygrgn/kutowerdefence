package com.canja.kutowerdefence.domain;

import com.canja.kutowerdefence.Routing;
import com.canja.kutowerdefence.ui.GamePlayView;
import com.canja.kutowerdefence.ui.ProjectileView;
import com.canja.kutowerdefence.ui.TileView;
import com.canja.kutowerdefence.state.*;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GameSession {
    private final Player player;
    private final File mapFile;
    private final Map map; // Assuming a Map object is part of GameSession
    private final int dimX = 16;
    private final int dimY = 12;
    private int[] optionValues;

    private SpeedState normalState;
    private SpeedState fastState;
    private SpeedState ultraFastState;
    private SpeedState slowState;
    private SpeedState speedState;
    private FlowState flowState;
    private FlowState pausedState;
    private FlowState playingState;

    private int waveNumber;
    private int currentWave;
    public int gameOver;

    private boolean waveActive = false;
    private boolean isCampaign = false;
    private int level; 

    private final List<Tower> activeTowers = new ArrayList<>();
    private final List<Enemy> activeEnemies = new ArrayList<>();

    private int enemiesKilled = 0;

    public void incrementEnemiesKilled() {
        enemiesKilled++;
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }


    public List<Tower> getTowers() { return activeTowers; }
    public List<Enemy> getEnemies() { return activeEnemies; }

    public void addTower(Tower t) { activeTowers.add(t); }
    public void addEnemy(Enemy e) { activeEnemies.add(e); }

    private GamePlayView view;
    public void setGamePlayView(GamePlayView v) { this.view = v; }
    public GamePlayView getView() { return view; }

    public GameSession(File mapFile) {
        File optionFile = new File("src/main/resources/options/options.kutdopt");
        this.mapFile = mapFile;
        this.map = new Map(dimX, dimY);

        extractOptionValues(optionFile);
        
        this.player = new Player(optionValues[Option.GOLD.ordinal()], optionValues[Option.PLAYER_HITPOINT.ordinal()]);

        InitializeSession();
    }

    public GameSession(File mapFile, int[] options, int[] playerInfo) {
        this.optionValues = options;
        this.mapFile = mapFile;
        this.map = new Map(dimX, dimY);
        this.player = new Player(optionValues[Option.GOLD.ordinal()], optionValues[Option.PLAYER_HITPOINT.ordinal()]);

        player.setGoldAmount(playerInfo[0]);
        player.setHealth(playerInfo[1]);
        isCampaign = true;
        
        this.gameOver=0;
        InitializeSession();
    }
    
    public GameSession(File mapFile, int[] options, int level) {
        this.optionValues = options;
        this.mapFile = mapFile;
        this.map = new Map(dimX, dimY);
        this.player = new Player(optionValues[Option.GOLD.ordinal()], optionValues[Option.PLAYER_HITPOINT.ordinal()]);

        isCampaign = true;
        this.level = level;

        this.gameOver=0;
        InitializeSession();
     }
    
    public void InitializeSession() {
        Tower.setCooldownToDefault();
        ProjectileView.setAnimationDurationToDefault();

        normalState = new NormalState(this);
        fastState = new FastState(this);
        ultraFastState = new UltraFastState(this);
        slowState = new SlowState(this);
        speedState = normalState;
        pausedState = new PausedState(this);
        playingState = new PlayingState(this);
        flowState = playingState;

        waveNumber = optionValues[Option.WAVE_NUMBER.ordinal()];
        currentWave = optionValues[Option.CURRENT_WAVE.ordinal()];

        try {
            map.loadFromFile(mapFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isCampaign() {
        return this.isCampaign;
    }

    public int getLevel() {
        return level;
    }

    public void start() {
        System.out.println("Starting new game session with map: " + mapFile.getName());
    }

    public Map getMap() {
        return map;
    }

    public void extractOptionValues(File optionFile) {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(optionFile)) {
            optionValues = gson.fromJson(reader, int[].class);
        } catch (IOException e) {
            System.err.println("Failed to save options: " + e.getMessage());
        }
    }

    public int[] getOptionValues() {
        return optionValues;
    }

    public SpeedState getNormalState() {
        return normalState;
    }

    public SpeedState getFastState() {
        return fastState;
    }

    public SpeedState getUltraFastState() {
        return ultraFastState;
    }

    public SpeedState getSlowState() {
        return slowState;
    }
    
    public SpeedState getSpeedState() {
        return speedState;
    }

    public FlowState getPlayingState() {
        return playingState;
    }

    public FlowState getPausedState() {
        return pausedState;
    }

    public FlowState getFlowState() {
        return flowState;
    }
    
    public void setSpeedState(SpeedState state) {
        speedState = state;
    }

    public void setFlowState(FlowState state) {
        flowState = state;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public void setCurrentWave(int number) {
        currentWave = number;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public void setWaveNumber(int number) {
        waveNumber = number;
    }

    public int isGameOver() {return gameOver;}

    public void setGameOver(int over) {gameOver = over;}

    public String getWaveInfo() {
        String text = "" + currentWave;
        if (currentWave == 0) {
            text = "1";
        }
        
        String waveInfo = text + "/" + waveNumber;

        return waveInfo;
    }
    
    public Player getPlayer() {
        return this.player;
    }

    public int getPlayerGold() {
        return this.player.getGoldAmount();
    }

    public void tick(float deltaTime) {
        for (Enemy enemy : activeEnemies) {
            enemy.update(deltaTime);
        }
        for (Tower tower : activeTowers) {
            tower.tryAttack(activeEnemies);

        }

        activeEnemies.removeIf(e -> {
            if (e.getHitpoint() <= 0) {
                incrementEnemiesKilled();
                return true;
            }
            return e.reachedEnd();
        });

    }

    public int getPlayerHitpoint() {
        return this.player.getHealth();
    }

    public String getMapPath() {
        return mapFile.getPath();
    }
    
    public Tower getNewestTower() {
        if (activeTowers.isEmpty()) return null;

        return activeTowers.get(activeTowers.size() - 1);
    }

    public boolean buyNewTower(int x, int y, MapObjectType selectedType) {
        /*
        * REQUIRES:
        * - The game must be started.
        * - The coordinates (x, y) must be valid (within the dimensions of the map).
        * - The selectedType must correspond to a valid tower type.
        *
        * MODIFIES:
        * - player.goldAmount: Cost of the tower is deducted if purchase is successful.
        * - this.activeTowers: A new tower is added to the active towers list.
        *
        * EFFECTS:
        * - If the player has enough gold and the inputs are valid:
        *     - A new tower of the selected type is created at (x, y).
        *     - The tower is added to the active towers list.
        *     - The cost is deducted from the player’s gold.
        *     - Returns true, indicating the operation was successful.
        * - Otherwise:
        *     - No changes are made to the game state.
        *     - Returns false, indicating the operation failed.
        */

        int cost = 0;

        switch (selectedType) {
            case TOWER_ARCHER:
                cost = optionValues[Option.ARCHER_TOWER_COST.ordinal()];
                break;
            case TOWER_ARTILLERY:
                cost = optionValues[Option.ARTILLERY_TOWER_COST.ordinal()];
                break;
            case TOWER_MAGE:
                cost = optionValues[Option.MAGE_TOWER_COST.ordinal()];
                break;
            default:
                return false;
        }

        if (player.getGoldAmount() < cost) return false;
        
        if (x < 0 || x >= dimX || y < 0 || y >= dimY) return false;

        Tower newTower = TowerFactory.createTower(selectedType, new Point(x, y), this);
        addTower(newTower);
        player.deductGold(cost);
        System.out.println(player.getGoldAmount());

        return true;
    }

    public void loseHealth() {
        player.loseHealth();
    }

    public void rewardPlayer(int val) {
        player.gainGold(val);
    }

    public void clearActiveEnemiesTowers() {
        activeEnemies.clear();
        activeTowers.clear();
    }

    public void resetSession() {
        this.gameOver = 0;
        this.currentWave = 1;
        this.waveNumber = optionValues[Option.WAVE_NUMBER.ordinal()];

        this.player.setHealth(optionValues[Option.PLAYER_HITPOINT.ordinal()]);
        this.player.setGoldAmount(optionValues[Option.GOLD.ordinal()]);

        clearActiveEnemiesTowers();

        this.flowState = playingState;
        this.speedState = normalState;

        Tower.setCooldownToDefault();
        ProjectileView.setAnimationDurationToDefault();
        EnemyFactory.GOBLIN.setSpeed(optionValues[Option.GOBLIN_SPEED.ordinal()]);
        EnemyFactory.KNIGHT.setSpeed(optionValues[Option.KNIGHT_SPEED.ordinal()]);
    }

    public boolean isWaveActive() {
        return waveActive;
    }

    public void setWaveState(boolean bool) {
        waveActive = bool;
    }
}

