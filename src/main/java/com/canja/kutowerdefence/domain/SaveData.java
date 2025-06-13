package com.canja.kutowerdefence.domain;

import java.io.File;
import java.util.List;

public class SaveData {
    private File mapFile;
    private int[] options;
    private List<int[]> towerInfo;
    private int[] playerInfo;

    public SaveData(File mapFile, int[] options, List<int[]> towerInfo, int[] playerInfo) {
        this.mapFile = mapFile;
        this.options = options;
        this.towerInfo = towerInfo;
        this.playerInfo = playerInfo;
    }

    public File getMapFile() {
        return mapFile;
    }

    public int[] getOptions() {
        return options;
    }

    public List<int[]> getTowerInfo() {
        return towerInfo;
    }

    public int[] getPlayerInfo() {
        return playerInfo;
    }
}
