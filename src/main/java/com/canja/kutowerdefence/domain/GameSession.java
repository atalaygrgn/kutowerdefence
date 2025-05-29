package com.canja.kutowerdefence.domain;

import com.canja.kutowerdefence.Routing;
import com.canja.kutowerdefence.state.*;
import com.canja.kutowerdefence.ui.GamePlayView;
import com.canja.kutowerdefence.ui.ProjectileView;
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
    private File optionFile;
    private int[] optionValues;

    private SpeedState normalState;
    private SpeedState fastState;
    private SpeedState ultraFastState;
    private SpeedState slowState;
    private SpeedState speedState;

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
        this.optionFile = new File("src/main/resources/options/options.kutdopt");
        this.mapFile = mapFile;
        this.map = new Map(dimX, dimY);

        extractOptionValues();
        
        this.player = new Player(optionValues[Option.GOLD.ordinal()], optionValues[Option.PLAYER_HITPOINT.ordinal()]);

        InitializeSession();
    }

    public GameSession(File mapFile, File optionFile) {
        this.optionFile = optionFile;
        this.mapFile = mapFile;
        this.map = new Map(dimX, dimY);

        extractOptionValues();
        
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

    public void extractOptionValues() {
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

    public Player getPlayer() {
        return this.player;
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

    public void tick(float deltaTime) {
        for (Enemy enemy : activeEnemies) {
            enemy.update(deltaTime);
        }
        for (Tower tower : activeTowers) {
            tower.tryAttack(activeEnemies);

        }
        activeEnemies.removeIf(e -> e.getHitpoint() <= 0);
    }

    public String getMapPath() {
        return mapFile.getPath();
    }

    public String getOptionPath() {
        return optionFile.getPath();
    }
    
    public Tower getNewestTower() {
        if (activeTowers.isEmpty()) return null;

        return activeTowers.get(activeTowers.size() - 1);
    }

    public boolean buyNewTower(int x, int y, MapObjectType selectedType) {
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
}

