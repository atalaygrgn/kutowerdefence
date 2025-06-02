package com.canja.kutowerdefence.ui;

import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ProjectileView extends Group {

    private static final int TILE_SIZE = 64;

    private final ImageView imageView = new ImageView();
    private final List<Image> frames = new ArrayList<>();
    private final Timeline animationTimeline;
    private static int animationDuration = 50;

    public ProjectileView(double startX, double startY, double endX, double endY,
        String spritePath, int frameW, int frameH, int frameCount, Runnable onHit){

        Image fullImage = new Image(getClass().getResourceAsStream(spritePath));
        int imageWidth = (int) fullImage.getWidth();

        for (int i = 0; i < frameCount; i++) {
            int x = i * frameW;
            if (x + frameW > imageWidth) break;

            frames.add(new WritableImage(
                    fullImage.getPixelReader(),
                    x, 0,
                    frameW, frameH
            ));
        }
        imageView.setImage(frames.get(0));
        imageView.setFitWidth(frameW);
        imageView.setFitHeight(frameH);
        getChildren().add(imageView);

        animationTimeline = new Timeline(
                new KeyFrame(Duration.millis(animationDuration), e -> {
                    int next = (frames.indexOf(imageView.getImage()) + 1) % frames.size();
                    imageView.setImage(frames.get(next));
                })
        );
        animationTimeline.setCycleCount(Animation.INDEFINITE);
        animationTimeline.play();

        setTranslateX(startX * TILE_SIZE);
        setTranslateY(startY * TILE_SIZE);

        TranslateTransition flight = new TranslateTransition(Duration.millis(400), this);
        flight.setToX(endX * TILE_SIZE);
        flight.setToY(endY * TILE_SIZE);
        flight.setOnFinished(e -> {
            animationTimeline.stop();
            onHit.run();
            this.setVisible(false);
            this.getChildren().clear();
            ((Pane)getParent()).getChildren().remove(this);
        });
        flight.play();
    }

    public static int getAnimationDuration() {
        return animationDuration;
    }

    public static void setAnimationDuration(int duration) {
        animationDuration = duration;
    }

    public static void setAnimationDurationToDefault() {
        animationDuration = 50;
    }
}
