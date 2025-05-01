package com.canja.kutowerdefence.domain;

import com.canja.kutowerdefence.Routing;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GameSession {
    private final File optionFile;
    private final File mapFile;
    private final Map map; // Assuming a Map object is part of GameSession
    private int[] optionValues; 

    public GameSession(File mapFile) {
        this.optionFile = new File("src/main/resources/options/options.kutdopt");
        this.mapFile = mapFile;
        this.map = new Map();

        InitializeSession();
    }

    public GameSession(File mapFile, File optionFile) {
        this.optionFile = optionFile;
        this.mapFile = mapFile;
        this.map = new Map();

        InitializeSession();
    }

    public void InitializeSession() {
        try {
            map.loadFromFile(mapFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        extractOptionValues();
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
}

