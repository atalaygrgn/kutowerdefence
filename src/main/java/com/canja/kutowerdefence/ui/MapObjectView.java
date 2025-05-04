package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.domain.MapObject;
import com.canja.kutowerdefence.domain.MapObjectType;
import com.canja.kutowerdefence.domain.TileType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;

public class MapObjectView extends ImageView {

    private final MapObject mapObject;

    public MapObjectView(MapObject mapObject) {
        this.mapObject = mapObject;
        if (mapObject.getType().equals(MapObjectType.CASTLE)) {
            setPreserveRatio(true);
            setFitWidth(128);
        }
        setImage(getObjectImage());
    }

    public MapObject getMapObject() {
        return mapObject;
    }

    public Image getObjectImage() {
        if (mapObject.getType().equals(MapObjectType.CASTLE)) {
            return new Image("file:src/main/resources/assets/tile64/castle64.png");
        } else if (mapObject.getType().equals(MapObjectType.KU_TOWER)) {
            return new Image("file:src/main/resources/assets/tile64/tile64_37.png");
        }
        String index_f = new DecimalFormat("00").format(mapObject.getType().ordinal() + 16);
        return new Image(new File("src/main/resources/assets/tile64/tile64_" + index_f + ".png").toURI().toString());
    }
}
