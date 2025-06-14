package com.canja.kutowerdefence.ui;

import com.canja.kutowerdefence.domain.MapObject;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class KuTowerView extends Group {

    private final ImageView towerImage;
    private final Rectangle healthBarBack;
    private final Rectangle healthBarFront;

    private final int TILE_SIZE = 64;
    private final int TOWER_WIDTH = 64;
    private final int TOWER_HEIGHT = 128;

    private int maxHealth;
    private int currentHealth = 100;

    public KuTowerView(MapObject mapObject, int maxHealth) {
        this.maxHealth = maxHealth;
        // Tower image
        towerImage = new ImageView(new Image("file:src/main/resources/assets/tile64/tile64_37.png"));
        towerImage.setFitWidth(TOWER_WIDTH);
        towerImage.setFitHeight(TOWER_HEIGHT);
        towerImage.setTranslateY(-64); // Place bottom-aligned to tile

        // Health bar background
        healthBarBack = new Rectangle(TILE_SIZE, 6);
        healthBarBack.setFill(Color.DARKRED);
        healthBarBack.setTranslateY(-70);

        // Health bar front (actual health)
        healthBarFront = new Rectangle(TILE_SIZE, 6);
        healthBarFront.setFill(Color.LIMEGREEN);
        healthBarFront.setTranslateY(-70);

        getChildren().addAll(towerImage, healthBarBack, healthBarFront);
    }

    public KuTowerView(MapObject mapObject) {
        this(mapObject, 100); // dummy default
    }

    public void setHealth(int health) {
        this.currentHealth = Math.max(0, Math.min(health, maxHealth));
        updateHealthBar();
    }


    private void updateHealthBar() {
        float ratio = (float) currentHealth / maxHealth;
        healthBarFront.setWidth(TILE_SIZE * ratio);
    }

}
