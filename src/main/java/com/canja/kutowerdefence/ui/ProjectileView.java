package com.canja.kutowerdefence.ui;

import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.QuadCurveTo;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ProjectileView extends Group {

    private static final int TILE_SIZE = 64;

    private final ImageView imageView = new ImageView();
    private final List<Image> frames = new ArrayList<>();
    private Timeline animationTimeline=null;
    private static int animationDuration = 50;
    private double endX, endY;
    private final Runnable onHit;
    private static float rate=1;

    public static float getRate() {
        return rate;
    }
    public static void setRate(float num) {
        rate = num;
    }
    public ProjectileView(double startX, double startY, double endX, double endY,
        String spritePath, int frameW, int frameH, int frameCount, Runnable onHit, boolean useBezier){

        this.endX = endX;
        this.endY = endY;
        this.onHit = onHit;

        Image fullImage = new Image(getClass().getResourceAsStream(spritePath));

        if (frameCount == 1) { // arrow case
            imageView.setImage(fullImage);
        } else {
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

            animationTimeline = new Timeline(
                    new KeyFrame(Duration.millis(animationDuration), e -> {
                        int next = (frames.indexOf(imageView.getImage()) + 1) % frames.size();
                        imageView.setImage(frames.get(next));
                    })
            );
            animationTimeline.setCycleCount(Animation.INDEFINITE);
            animationTimeline.setRate(rate);
            animationTimeline.play();
        }

        imageView.setFitWidth(frameW);
        imageView.setFitHeight(frameH);
        imageView.setTranslateX(-frameW / 2.0);
        imageView.setTranslateY(-frameH / 2.0);
        getChildren().add(imageView);

        setTranslateX(startX * TILE_SIZE);
        setTranslateY(startY * TILE_SIZE);

        if (useBezier) {
            startBezierAnimation();
        } else {
            TranslateTransition flight = new TranslateTransition(Duration.millis(400), this);
            flight.setRate(rate);
            flight.setToX(endX * TILE_SIZE);
            flight.setToY(endY * TILE_SIZE);
            flight.setOnFinished(e -> handleHit());
            flight.play();
        }
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
    private void handleHit() {
        if (animationTimeline != null) {
            animationTimeline.stop();
        }
        onHit.run();
        this.setVisible(false);
        this.getChildren().clear();
        if (getParent() instanceof Pane parent) {
            parent.getChildren().remove(this);
        }
    }

    public void startBezierAnimation() {
        double rawSx = getTranslateX();
        double rawSy = getTranslateY();
        double rawEx = endX * TILE_SIZE;
        double rawEy = endY * TILE_SIZE;

        // centered values
        double sx = rawSx + TILE_SIZE / 2.0;
        double sy = rawSy + TILE_SIZE / 2.0;
        double ex = rawEx + TILE_SIZE / 2.0;
        double ey = rawEy + TILE_SIZE / 2.0;

        double dx = ex - sx;
        double dy = ey - sy;

        double distance = Math.sqrt(dx * dx + dy * dy);


        // perpendicular and always arcs up
        double px = dy / distance;
        double py = -dx / distance;

        // adjusting arc height
        double baseArc = Math.max(30, Math.min(40, distance * 0.5));
        double arcHeight = Math.min(60, baseArc + distance / 6);

        // arc more for diagonal shots
        double angle = Math.abs(Math.atan2(dy, dx));
        double arcMultiplier = 0.7 + 0.6 * Math.abs(Math.sin(angle));
        double adjustedArc = arcHeight * arcMultiplier;

        // midpoint
        double midX = (sx + ex) / 2;
        double midY = (sy + ey) / 2;

        double controlX = midX + px * adjustedArc;
        double controlY = midY + py * adjustedArc;

        Path path = new Path();
        path.getElements().add(new MoveTo(sx, sy));
        path.getElements().add(new QuadCurveTo(controlX, controlY, ex, ey));

        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(400));
        pathTransition.setRate(rate);
        pathTransition.setNode(this);
        pathTransition.setPath(path);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setOnFinished(e -> handleHit());
        pathTransition.play();
    }
}
