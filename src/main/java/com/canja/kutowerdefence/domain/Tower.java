package com.canja.kutowerdefence.domain;

import java.util.List;

public abstract class Tower extends MapObject {
    // OVERVIEW: This class represents a general tower, which has coordinates on a map (inherited from MapObject),
    // range to check for nearby enemies to attack, damage it deals per attack, cooldown time between attacks,
    // and a reference to the current game session. Subclasses must define its tower type i.e. how the tower should
    // attack an enemy.

    // ABSTRACTION FUNCTION:
    // AF(t) = (t.position.x, t.position.y) + t.range + t.damage + t.session

    // REPRESENTATION INVARIANT:
    // position.x >= 0 AND position.x <= 15 AND
    // position.y >= 0 AND position.y <= 11 AND
    // range >= 0 AND
    // damage >= 0 AND
    // session != null

    public abstract Enemy[] targetEnemies(List<Enemy> allEnemies);
    public abstract void attackEnemy(Enemy target);
    protected int cost;
    protected int range;
    protected int damage;
    protected GameSession session;
    protected long lastAttackTime = 0;
    protected static long attackCooldownMillis = 800;

    public Tower(MapObjectType type, Point position, GameSession gameSession, int range, int damage, int cost) {
        super(type, position);
        this.session = gameSession;
        this.range = range;
        this.damage = damage;
        this.cost = cost;
    }

    public boolean isInRange(Enemy enemy) {
        Point enemyPos = new Point(Math.round(enemy.getX()), Math.round(enemy.getY()));
        return position.distanceTo(enemyPos) <= range;
    }

    public final void tryAttack(List<Enemy> enemies) {
        long now = System.currentTimeMillis();

        if (now - lastAttackTime < attackCooldownMillis) return;

        Enemy[] targets = targetEnemies(enemies);
        if (targets.length > 0) {
            attackEnemy(targets[0]);
            lastAttackTime = now;
        }
    }

    protected Enemy getFurthestInRange(List<Enemy> allEnemies) {
        Enemy best = null;
        int bestIndex = -1;

        for (Enemy e : allEnemies) {
            if (isInRange(e) && e.getCurrentPathIndex() > bestIndex) {
                best = e;
                bestIndex = e.getCurrentPathIndex();
            }
        }
        return best;
    }

    public static long getCooldown() {
        return attackCooldownMillis;
    }

    public static void setCooldown(long cooldown) {
        attackCooldownMillis = cooldown;
    }

    public static void setCooldownToDefault() {
        attackCooldownMillis = 800;
    }

    public boolean repOk() {
        // EFFECTS: Returns true if the representation invariant of the Tower class holds true, otherwise returns false.
        return  (position.getX() >= 0 && position.getX() <= 15) &&
                (position.getY() >= 0 && position.getY() <= 11) &&
                range >= 0 && damage >= 0 && session != null;
    }

}
