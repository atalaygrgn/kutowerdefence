package com.canja.kutowerdefence.domain;

import java.util.List;

public abstract class Tower extends MapObject {

    public abstract Enemy[] targetEnemies(List<Enemy> allEnemies);
    public abstract void attackEnemy(Enemy target);

    protected int range;
    protected int damage;
    protected int cost;
    protected GameSession session;
    protected long lastAttackTime = 0;
    protected int level = 1;

    protected static long cooldownMillis = 800;

    public Tower(MapObjectType type, Point position, GameSession gameSession, int range, int damage, int cost) {
        super(type, position);
        this.session = gameSession;
        this.range = range;
        this.damage = damage;
        this.cost = cost;
    }

    public int getLevel() {
        return level;
    }

    public boolean canUpgrade() {
        return level == 1;
    }

    public abstract int getUpgradeCost();
    public abstract void upgrade();

    public boolean tryUpgrade(Player player) {
        if (level >= 2) return false;
        int cost = getUpgradeCost();
        if (player.getGoldAmount() < cost) return false;

        player.deductGold(cost);
        upgrade();
        return true;
    }




    public boolean isInRange(Enemy enemy) {
        Point enemyPos = new Point(Math.round(enemy.getX()), Math.round(enemy.getY()));
        return position.distanceTo(enemyPos) <= range;
    }

    public final void tryAttack(List<Enemy> enemies) {
        long now = System.currentTimeMillis();
        if (now - lastAttackTime < cooldownMillis) return;

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
        return cooldownMillis;
    }

    public static void setCooldown(long cooldown) {
        cooldownMillis = cooldown;
    }

    public static void setCooldownToDefault() {
        cooldownMillis = 800;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }
}
