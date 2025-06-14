package com.canja.kutowerdefence.domain;
import java.util.LinkedList;
import java.util.List;

public class Enemy {
    private float x,y;
    private float speed;
    private int hitpoint;
    private EnemyDescription description;
    private LinkedList<Point> path;
    private int currentPathIndex;
    private int goldReward;
    private boolean isSlowed = false;
    private float slowTimer = 0f;
    private float slowMultiplier = 1.0f;
    private boolean synergyActive = false;
    private static List<Enemy> sharedEnemies;


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

    public static void setSharedEnemies(List<Enemy> enemies) {
        sharedEnemies = enemies;
    }

    public void setSynergyActive(boolean active) {
        if (description.getName().equalsIgnoreCase("knight")) {
            this.synergyActive = active;
        }
    }


    public boolean isSynergyActive() { return synergyActive; }

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
        return speed*slowMultiplier;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isSlowed() { return isSlowed; }

    public void applySlow(float multiplier, float duration) {
        if (!isSlowed || slowMultiplier > multiplier) {
            isSlowed = true;
            slowMultiplier = multiplier;
            slowTimer = duration;
        } else {

            slowTimer = duration;
        }
    }

    public int getHitpoint() {
        return hitpoint;
    }


    public void update(float deltaTime) {
        if (currentPathIndex >= path.size()) {
            return;
        }

        // Handle slow effect
        if (isSlowed) {
            slowTimer -= deltaTime;
            if (slowTimer <= 0f) {
                isSlowed = false;
                slowMultiplier = 1.0f;
                System.out.println("[Effect] Slow effect ended for " + description.getName());
            }
        }

        // Combat synergy logic for knights
        if (description.getName().equalsIgnoreCase("knight")) {
            boolean nearGoblin = false;

            for (Enemy other : sharedEnemies) {
                if (other != this && other.getDescription().getName().equalsIgnoreCase("goblin")) {
                    float dx = other.getX() - x;
                    float dy = other.getY() - y;
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);
                    if (dist < 1.0f) {
                        nearGoblin = true;
                        break;
                    }
                }
            }

            if (nearGoblin) {
                float boostedSpeed = (EnemyFactory.GOBLIN.getSpeed() + EnemyFactory.KNIGHT.getSpeed()) / 2f;
                if (this.speed != boostedSpeed) {
                    System.out.println("[Synergy] Knight boosted by goblin nearby.");
                }
                this.speed = boostedSpeed;
            } else {
                float defaultSpeed = EnemyFactory.KNIGHT.getSpeed();
                if (this.speed != defaultSpeed) {
                    System.out.println("[Synergy] Knight lost synergy, reverting speed.");
                }
                this.speed = defaultSpeed;
            }

            setSynergyActive(nearGoblin);
        }

        // Move enemy
        Point target = path.get(currentPathIndex);
        float targetX = target.getX();
        float targetY = target.getY();

        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        float effectiveSpeed = getSpeed();
        if (distance < effectiveSpeed * deltaTime) {
            x = targetX;
            y = targetY;
            currentPathIndex++;
        } else {
            x += (dx / distance) * effectiveSpeed * deltaTime;
            y += (dy / distance) * effectiveSpeed * deltaTime;
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

    public void teleportToStart() {
        if (!path.isEmpty()) {
            Point start = path.get(0);
            this.x = start.getX();
            this.y = start.getY();
            this.currentPathIndex = 0;
        }
    }
}