package com.canja.kutowerdefence.domain;

import java.io.File;


public class GameSession {
    private final File mapFile;

    public GameSession(File mapFile) {
        this.mapFile = mapFile;
    }

    public void start() {
        System.out.println("Starting new game session with map: " + mapFile.getName());
        // TODO: Display the loaded map, spawn the enemies etc...
    }

}

