package com.canja.kutowerdefence.domain;

import javafx.scene.image.Image;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;

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

    public Image getTileImage64() {
        String index_f = new DecimalFormat("00").format(Arrays.asList(TileType.values()).indexOf(tileType));
        return new Image(new File("src/main/resources/assets/tile64/tile64_" + index_f + ".png").toURI().toString());
    }

    public static TileType getTileType(int x, int y) {
        return TileType.values()[4*x + y];
    }

}

