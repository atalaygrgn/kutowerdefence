package com.canja.kutowerdefence.domain;

import com.canja.kutowerdefence.Routing;
import com.canja.kutowerdefence.state.*;
import com.canja.kutowerdefence.ui.GamePlayView;
import com.canja.kutowerdefence.ui.ProjectileView;
import com.canja.kutowerdefence.ui.TileView;
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
    private boolean pauseState = false;

    private int waveNumber;
    private int currentWave;

    private final List<Tower> activeTowers = new ArrayList<>();
    private final List<Enemy> activeEnemies = new ArrayList<>();

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

    public GameSession(File mapFile, int[] options) {
        this.optionValues = options;
        this.mapFile = mapFile;
        this.map = new Map(dimX, dimY);
        this.player = new Player(optionValues[Option.GOLD.ordinal()], optionValues[Option.PLAYER_HITPOINT.ordinal()]);

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

        waveNumber = optionValues[Option.WAVE_NUMBER.ordinal()];
        currentWave = optionValues[Option.CURRENT_WAVE.ordinal()];

        try {
            map.loadFromFile(mapFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    
    public void setSpeedState(SpeedState state) {
        speedState = state;
    }

    public boolean getPauseState() {
        return pauseState;
    }

    public void togglePauseState() {
        pauseState = !pauseState;
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

    public String getWaveInfo() {
        String waveInfo = currentWave + "/" + waveNumber;

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
        activeEnemies.removeIf(e -> e.getHitpoint() <= 0);
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

    public void resetSession() {
        this.currentWave = 1;
        this.waveNumber = optionValues[Option.WAVE_NUMBER.ordinal()];

        this.player.setHealth(optionValues[Option.PLAYER_HITPOINT.ordinal()]);
        this.player.setGoldAmount(optionValues[Option.GOLD.ordinal()]);

        activeEnemies.clear();
        activeTowers.clear();

        this.pauseState = false;
        this.speedState = normalState;

        Tower.setCooldownToDefault();
    }
}

