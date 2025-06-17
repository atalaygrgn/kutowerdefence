package com.canja.kutowerdefence.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import com.canja.kutowerdefence.domain.Enemy;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class EnemyView extends Group {
    private Enemy enemy;
    private ImageView imageView;
    private Rectangle healthBarBack;
    private Rectangle healthBarFront;
    private final int TILE_SIZE = 64;

    private final List<Image> animationFrames = new ArrayList<>();
    private int currentFrame = 0;
    private Timeline animationTimeline;
    private ImageView slowIcon;
    private ImageView thunderIcon;

    public EnemyView(Enemy enemy) {
        this.enemy = enemy;

        String folderPath = getImageFolderPath(enemy);
        for (int i = 0; i < 6; i++) {
            Image frame = new Image(getClass().getResourceAsStream(folderPath + i + ".png"));
            animationFrames.add(frame);
        }

        imageView = new ImageView(animationFrames.get(0));
        imageView.setFitWidth(TILE_SIZE);
        imageView.setFitHeight(TILE_SIZE);

        // health bar background
        healthBarBack = new Rectangle(TILE_SIZE, 6);
        healthBarBack.setFill(Color.DARKRED);

        // health bar front (actual HP)
        healthBarFront = new Rectangle(TILE_SIZE, 6);
        healthBarFront.setFill(Color.LIMEGREEN);

        this.getChildren().addAll(imageView, healthBarBack, healthBarFront);


        Image snowflakeImage = new Image("file:src/main/resources/assets/effects/snowflake.png");
        slowIcon = new ImageView(snowflakeImage);
        slowIcon.setFitWidth(16);
        slowIcon.setFitHeight(16);
        slowIcon.setVisible(false);
        slowIcon.setMouseTransparent(true);
        slowIcon.setTranslateX(TILE_SIZE / 2f - 16);
        slowIcon.setTranslateY(-20);
        this.getChildren().add(slowIcon);

        Image thunderImage = new Image("file:src/main/resources/assets/effects/thunder.png");
        thunderIcon = new ImageView(thunderImage);
        thunderIcon.setFitWidth(16);
        thunderIcon.setFitHeight(16);
        thunderIcon.setVisible(false);
        thunderIcon.setMouseTransparent(true);
        thunderIcon.setTranslateX(TILE_SIZE / 2f);
        thunderIcon.setTranslateY(-20);
        this.getChildren().add(thunderIcon);


        animationTimeline = new Timeline(
                new KeyFrame(Duration.millis(100), e -> {
                    currentFrame = (currentFrame + 1) % animationFrames.size();
                    imageView.setImage(animationFrames.get(currentFrame));
                })
        );
        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.play();

        update(); // Initial update
    }

    private String getImageFolderPath(Enemy enemy) {
        String name = enemy.getDescription().getName().toLowerCase();
        if (name.equals("goblin")) {
            return "/assets/enemies/goblin/";
        } else if (name.equals("knight")) {
            return "/assets/enemies/knight/";
        } else {
            return "/assets/enemies/goblin/";
        }
    }

    public void update() {
        float tileX = enemy.getX();
        float tileY = enemy.getY();
        float pixelX = tileX * TILE_SIZE;
        float pixelY = tileY * TILE_SIZE;

        this.setTranslateX(pixelX + TILE_SIZE / 2f - imageView.getFitWidth() / 2f);
        this.setTranslateY(pixelY + TILE_SIZE / 2f - imageView.getFitHeight() / 2f);

        int currentHP = enemy.getHitpoint();
        int maxHP = enemy.getDescription().getHitpoints();
        float ratio = Math.max(0, (float) currentHP / maxHP);
        healthBarFront.setWidth(TILE_SIZE * ratio);

        // Show or hide the snowflake overlay
        slowIcon.setVisible(enemy.getIsSlowed());

        // Show or hide the thunder icon for combat synergy
        thunderIcon.setVisible(enemy.getHasCombatSynergy());
    }

    public boolean isDead() {
        return enemy.getHitpoint() <= 0;
    }

    public Enemy getEnemy() {
        return enemy;
    }
}
