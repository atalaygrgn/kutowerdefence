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

    public Tile[][] getMap() {
        return map;
    }

    public void setMap(Tile[][] map) {
        this.map = map;
    }

    public Tile getTile(int x, int y) {
        return map[x][y];
    }

    public void setTile(int x, int y, Tile tile) {
        map[x][y] = tile;
    }

}
