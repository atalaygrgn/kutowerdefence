package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.domain.Tile;
import com.canja.kutowerdefence.domain.TileType;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;

public class TileView extends ImageView {

    private Tile tile;
    private static final Glow glow = new Glow(0.5);

    public TileView(Tile tile) {
        setTile(tile);
    }

    public TileType getTileType() {
        return tile.getTileType();
    }

    public void setTile(Tile tile) {
        this.tile = tile;
        setImage(getTileImage64());
    }

    public void setTileType(TileType tileType) {
        this.tile = new Tile(tileType);
        setImage(getTileImage64());
    }

    public void highlight() {
        setEffect(glow);
    }

    public void unhighlight() {
        setEffect(null);
    }

    public Image getTileImage64() {
        String index_f = new DecimalFormat("00").format(Arrays.asList(TileType.values()).indexOf(tile.getTileType()));
        return new Image(new File("src/main/resources/assets/tile64/tile64_" + index_f + ".png").toURI().toString());
    }
}
