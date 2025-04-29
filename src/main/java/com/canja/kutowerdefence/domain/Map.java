package com.canja.kutowerdefence.domain;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Map {

    private Tile[][] map;

    public Map() {
        map =  new Tile[16][12];
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 12; y++) {
                map[x][y] = new Tile();
            }
        }
    }

    public Tile[][] getArray() {
        return map;
    }

    public Tile getTile(int x, int y) {
        return map[x][y];
    }

    public void editTile(int x, int y, TileType tileType) {
        map[x][y].setTileType(tileType);
    }

    public Tile[][] reset() {
        map = new Tile[16][12];
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 12; y++) {
                map[x][y] = new Tile();
            }
        }
        return map;
    }

    public void loadFromFile(File mapFile) throws IOException {
        String content = new String(Files.readAllBytes(mapFile.toPath()));
        Gson gson = new Gson();
        int[][] serializedMap = gson.fromJson(content, int[][].class);

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 12; y++) {
                TileType type = TileType.values()[serializedMap[x][y]];
                editTile(x, y, type);
            }
        }
    }
}
