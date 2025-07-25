package com.canja.kutowerdefence.domain;
import java.util.LinkedList;
import java.util.List;

public class Enemy {
    private float x, y;
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
    private boolean isSlowed = false;
    private long slowEndTime = 0;
    private double slowFactor = 1.0;
    private boolean hasCombatSynergy = false;
    private float originalSpeed;

    public float getOriginalSpeed() {
        return originalSpeed;
    }

    public void applySlow(double factor, long durationMillis) {
        this.slowFactor = factor;
        this.isSlowed = true;
        this.slowEndTime = System.currentTimeMillis() + durationMillis;
    }

    public Enemy(EnemyDescription description, LinkedList<Point> path) {
        this.description = description;
        this.path = path;
        this.speed = description.getSpeed();
        this.originalSpeed = description.getSpeed();
        this.hitpoint = description.getHitpoints();
        this.goldReward = description.getGold();

        Point start = path.get(0);
        this.x = start.getX();
        this.y = start.getY();

        this.noiseOffsetX = (float) (Math.random() * 2 * Math.PI);
        this.noiseOffsetY = (float) (Math.random() * 2 * Math.PI);
        this.noiseTime = 0;
    }

    public boolean getHasCombatSynergy() {
        return hasCombatSynergy;
    }

    public void setHasCombatSynergy(boolean hasSynergy) {
        this.hasCombatSynergy = hasSynergy;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean getIsSlowed() {
        return isSlowed;
    }

    public EnemyDescription getDescription() {
        return description;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public float getSpeed() {
        float currentSpeed = speed;

        if (isSlowed && System.currentTimeMillis() < slowEndTime) {
            currentSpeed = (float)(currentSpeed * slowFactor);
        } else if (isSlowed) {
            isSlowed = false;
            slowFactor = 1.0;
        }

        return currentSpeed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getHitpoint() {
        return hitpoint;
    }

    private float lastDx = 0;
    private float lastDy = 0;

    public void update(float deltaTime) {
        if (currentPathIndex >= path.size()) {
            return;
        }

        Point target = path.get(currentPathIndex);
        float targetX = target.getX();
        float targetY = target.getY();

        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        float currentSpeed = getSpeed();

        // Update noise
        noiseTime += deltaTime;
        float noiseX = (float) (Math.sin(noiseTime * NOISE_FREQUENCY + noiseOffsetX) * NOISE_AMPLITUDE);
        float noiseY = (float) (Math.cos(noiseTime * NOISE_FREQUENCY + noiseOffsetY) * NOISE_AMPLITUDE);

        boolean directionChanged = false;
        if (lastDx != 0 || lastDy != 0) {
            float dotProduct = (dx * lastDx + dy * lastDy) / 
                              (distance * (float)Math.sqrt(lastDx * lastDx + lastDy * lastDy));
            directionChanged = dotProduct < 0.9;
        }

        if (distance < currentSpeed * deltaTime) {
            x = targetX;
            y = targetY;
            currentPathIndex++;
            lastDx = 0;
            lastDy = 0;
        } else {
            float moveX = (dx / distance) * currentSpeed * deltaTime;
            float moveY = (dy / distance) * currentSpeed * deltaTime;

            // Store current direction for next update
            lastDx = dx;
            lastDy = dy;

            // Add noise only when not too close to target and not changing direction
            if (distance > 0.5f && !directionChanged) {
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

    // damage in arrow: knight < goblin
    // damage in spell: knight > goblin

    public int getCurrentPathIndex() {
        return currentPathIndex;
    }

    public boolean reachedEnd() {
        return currentPathIndex >= path.size();
    }

    public void teleportToStart() {
        if (path != null && !path.isEmpty()) {
            Point start = path.get(0);
            this.x = start.getX();
            this.y = start.getY();
            this.currentPathIndex = 1;
        }
    }
}
