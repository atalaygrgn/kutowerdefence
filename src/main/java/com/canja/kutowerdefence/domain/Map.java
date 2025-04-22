package com.canja.kutowerdefence.domain;

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

}
