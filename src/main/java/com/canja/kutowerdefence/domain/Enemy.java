package com.canja.kutowerdefence.domain;
import java.util.LinkedList;

public class Enemy {
    private float x,y;
    private float speed;
    private int hitpoint;
    private EnemyDescription description;
    private LinkedList<Point> path;
    private int currentPathIndex;
    private static int goldReward;


    public Enemy(EnemyDescription description, LinkedList<Point> path) {
        this.description = description;
        this.path = path;
        speed= description.getSpeed();
        this.hitpoint = description.getHitpoints();
        goldReward = description.getGold();

        Point start = path.get(0);
        this.x= start.getX();
        this.y= start.getY();

        System.out.println("PATH: " + path.get(0).getX() + "," + path.get(0).getY() + " → " + path.get(1).getX() + "," + path.get(1).getY());
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
        System.out.println("x: " + x + " y: " + y + "deltaTime: " + deltaTime+"currentPathIndex: " + currentPathIndex);
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

    public void toggleSpeed(boolean willBeFaster) {
        if (willBeFaster) speed *= 2;
        else speed /= 2;
    }
}