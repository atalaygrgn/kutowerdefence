package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.domain.Tile;
import com.canja.kutowerdefence.domain.TileType;
import javafx.scene.image.ImageView;

public class TileView extends ImageView {

    private Tile tile;

    public TileView(Tile tile) {
        setTile(tile);
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
        setImage(tile.getTileImage());
    }

    public void setTileType(TileType tileType) {
        this.tile = new Tile(tileType);
        setImage(this.tile.getTileImage());
    }
}
