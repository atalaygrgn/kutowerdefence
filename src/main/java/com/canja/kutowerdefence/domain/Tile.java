package com.canja.kutowerdefence.domain;

import javafx.scene.image.Image;

import java.io.File;

public class Tile {

    private TileType tileType;

    public Tile(TileType tileType) {
        this.tileType = tileType;
    }

    public Tile() {
        this.tileType = TileType.CIRCULAR_CENTER;
    }

    public TileType getTileType() {
        return tileType;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }

    public Image getTileImage() {
        String tilePath = switch (tileType) {
            case HORIZONTAL -> "assets/tile64/tile64_13.png";
            case VERTICAL -> "assets/tile64/tile64_07.png";
            case CIRCULAR_TOPLEFT -> "assets/tile64/tile64_00.png";
            case CIRCULAR_TOPCENTER -> "assets/tile64/tile64_01.png";
            case CIRCULAR_TOPRIGHT -> "assets/tile64/tile64_02.png";
            case CIRCULAR_CENTERLEFT -> "assets/tile64/tile64_04.png";
            case CIRCULAR_CENTER -> "assets/tile64/tile64_05.png";
            case CIRCULAR_CENTERRIGHT -> "assets/tile64/tile64_06.png";
            case CIRCULAR_BOTTOMLEFT -> "assets/tile64/tile64_08.png";
            case CIRCULAR_BOTTOMCENTER ->"assets/tile64/tile64_09.png";
            case CIRCULAR_BOTTOMRIGHT -> "assets/tile64/tile64_10.png";
            default -> null;
        };
        return new Image(new File("src/main/resources/" + tilePath).toURI().toString());
    }

}

