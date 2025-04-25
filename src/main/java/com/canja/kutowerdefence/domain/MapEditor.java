package com.canja.kutowerdefence.domain;

import com.canja.kutowerdefence.ui.TileView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MapEditor {

    private final Map map;
    private TileType selectedTileType;

    public MapEditor() {
        this.map = new Map();
        this.selectedTileType = TileType.EMPTY;
    }

    public Map getMap() {
        return map;
    }

    public TileType getSelectedTileType() {
        return selectedTileType;
    }

    public void changeSelectedTile(TileType tileType) {
        this.selectedTileType = tileType;
    }

    public void penTool(int x, int y, TileView tileView) {
        editTile(x, y, selectedTileType);
        tileView.setTileType(selectedTileType);
    }

    private void editTile(int x, int y, TileType tileType) {
        map.editTile(x, y, tileType);
    }

    public static int[] getPaletteCoords(TileType tileType) {
        int index = tileType.ordinal();
        return new int[] {index / 4, index % 4};
    }

    public void resetMap() {
        map.reset();
    }

    public void saveMap(String filename) {
        try {
            // Convert the map to a serializable format
            int[][] serializedMap = new int[16][12];
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 12; y++) {
                    serializedMap[x][y] = map.getTile(x, y).getTileType().ordinal();
                }
            }

            // Create Gson instance
            Gson gson = new GsonBuilder().create();

            // Write to file
            try (FileWriter writer = new FileWriter(filename)) {
                gson.toJson(serializedMap, writer);
            }

            System.out.println("Map saved successfully to " + filename);
        } catch (IOException e) {
            System.err.println("Failed to save map: " + e.getMessage());
        }
    }

    public void loadMap(String filename) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        Gson gson = new Gson();
        int[][] serializedMap = gson.fromJson(content, int[][].class);

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 12; y++) {
                TileType type = TileType.values()[serializedMap[x][y]];
                map.editTile(x, y, type);
            }
        }
    }
}
