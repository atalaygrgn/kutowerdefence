package com.canja.kutowerdefence.domain;
import java.util.LinkedList;

public class Enemy {
    private float x,y;
    private float speed;
    private int hitpoint;
    private EnemyDescription description;
    private LinkedList<Point> path;
    private int currentPathIndex;
    private int goldReward;


    public Enemy(EnemyDescription description, LinkedList<Point> path) {
        this.description = description;
        this.path = path;
        this.speed = description.getSpeed();
        this.hitpoint = description.getHitpoints();
        this.goldReward = description.getGold();

        Point start = path.get(0);
        this.x = start.getX();
        this.y = start.getY();
    }
    
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public EnemyDescription getDescription() {
        return description;
    }

    public int getGoldReward() {
        return goldReward;
    }
    
    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getHitpoint() {
        return hitpoint;
    }
    public void update(float deltaTime){
        if(currentPathIndex >= path.size()){
            return;
        }
        Point target = path.get(currentPathIndex);
        float targetX = target.getX();
        float targetY = target.getY();

        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float)Math.sqrt(dx*dx+dy*dy);

        if(distance< speed * deltaTime){
            x= targetX;
            y= targetY;
            currentPathIndex++;
        } else {
            x += (dx/distance) * speed * deltaTime;
            y += (dy/distance) * speed * deltaTime;
        }
    }

    public void takeDamage(int damage, String attackType) {
        float multiplier = 1.0f;

        if (attackType.equals("Arrow")) {
            int resistance = description.getArrowResistance();
            multiplier = 1.0f - resistance / 100f;
        } else if (attackType.equals("Spell")) {
            int resistance = description.getSpellResistance();
            multiplier = 1.0f - resistance / 100f;
        }

        int actualDamage = Math.round(damage * multiplier);
        hitpoint -= actualDamage;
    }
    //damage in arrow: knight<goblin
    //damage in spell: knight>goblin

    public int getCurrentPathIndex() {
        return currentPathIndex;
    }

    public boolean reachedEnd() {
        return currentPathIndex >= path.size();
    }
}