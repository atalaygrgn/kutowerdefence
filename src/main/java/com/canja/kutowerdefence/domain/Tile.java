package com.canja.kutowerdefence.domain;

public class Tile {

    private TileType tileType;

    public Tile(TileType tileType) {
        this.tileType = tileType;
    }

    public Tile(int index) {
        this.tileType = TileType.values()[index];
    }

    public Tile(int x, int y) {
        this.tileType = TileType.values()[4*x + y];
    }

    public Tile() {
        this.tileType = TileType.EMPTY;
    }

    public TileType getTileType() {
        return tileType;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }

    public static TileType getTileType(int x, int y) {
        return TileType.values()[4*x + y];
    }

}

