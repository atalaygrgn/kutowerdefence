package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.domain.Tile;
import com.canja.kutowerdefence.domain.TileType;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;

public class TileView extends ImageView {

    private Tile tile;
    private static final Glow glow = new Glow(0.5);

    public TileView(Tile tile) {
        setTile(tile);
    }

    public Tile getTile() {
        return tile;
    }

    public TileType getTileType() {
        return tile.getTileType();
    }

    public void setTile(Tile tile) {
        this.tile = tile;
        setImage(tile.getTileImage64());
    }

    public void setTileType(TileType tileType) {
        this.tile = new Tile(tileType);
        setImage(this.tile.getTileImage64());
    }

    public void highlight() {
        setEffect(glow);
    }

    public void unhighlight() {
        setEffect(null);
    }
}
