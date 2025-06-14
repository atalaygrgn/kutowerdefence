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

    private static final float NOISE_AMPLITUDE = 0.15f; // Maximum deviation from path
    private static final float NOISE_FREQUENCY = 1.5f; // How fast the noise changes
    private float noiseOffsetX;
    private float noiseOffsetY;
    private float noiseTime;


    public Enemy(EnemyDescription description, LinkedList<Point> path) {
        this.description = description;
        this.path = path;
        this.speed = description.getSpeed();
        this.hitpoint = description.getHitpoints();
        this.goldReward = description.getGold();

        Point start = path.get(0);
        this.x = start.getX();
        this.y = start.getY();

        this.noiseOffsetX = (float) (Math.random() * 2 * Math.PI);
        this.noiseOffsetY = (float) (Math.random() * 2 * Math.PI);
        this.noiseTime = 0;
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

        // Update noise
        noiseTime += deltaTime;
        float noiseX = (float) (Math.sin(noiseTime * NOISE_FREQUENCY + noiseOffsetX) * NOISE_AMPLITUDE);
        float noiseY = (float) (Math.cos(noiseTime * NOISE_FREQUENCY + noiseOffsetY) * NOISE_AMPLITUDE);

        if(distance < speed * deltaTime){
            x= targetX;
            y= targetY;
            currentPathIndex++;
        } else {
            float moveX = (dx/distance) * speed * deltaTime;
            float moveY = (dy/distance) * speed * deltaTime;

            // Add noise only when not too close to target
            if (distance > 0.5f) {
                x += moveX + noiseX * deltaTime;
                y += moveY + noiseY * deltaTime;
            } else {
                x += moveX;
                y += moveY;
            }
        }
    }

    /*
     * REQUIRES:
     * - The damage must be a positive integer (damage >= 0).
     * - The attackType must be one of the following strings (case-sensitive): "Arrow", "Spell", or any other string (treated as unknown type).
     * - EnemyDescription must be non-null and initialized.
     *
     * MODIFIES:
     * - this.hitpoint
     *
     * EFFECTS:
     * - Computes effective damage based on resistance:
     *   - For "Arrow", reduces damage using arrowResistance.
     *   - For "Spell", reduces damage using spellResistance.
     *   - For unknown types, applies full damage.
     * - Deducts the resulting actual damage from this.hitpoint .
     * - The hitpoint may become less than or equal to 0 (in case the enemy can "die").
     */

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