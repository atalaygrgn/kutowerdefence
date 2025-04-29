package com.canja.kutowerdefence.domain;

import com.canja.kutowerdefence.Routing;

import java.io.File;
import java.io.IOException;

public class GameSession {
    private final File mapFile;
    private final Map map; // Assuming a Map object is part of GameSession

    public GameSession(File mapFile) {
        this.mapFile = mapFile;
        this.map = new Map();
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
}

