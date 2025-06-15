package com.canja.kutowerdefence.domain;

import java.util.List;

public abstract class Tower extends MapObject {

    protected int range;
    protected int damage;
    protected int cost;
    public GameSession session;

    protected long lastAttackTime = 0;


    protected static long attackCooldownMillis = 800;


    protected long personalCooldownMillis = -1;

    protected TowerLevel level = TowerLevel.LEVEL1;

    public Tower(MapObjectType type, Point position, GameSession gameSession, int range, int damage, int cost) {
        super(type, position);
        this.session = gameSession;
        this.range = range;
        this.damage = damage;
        this.cost = cost;
    }

    public final void tryAttack(List<Enemy> enemies) {
        long now = System.currentTimeMillis();

        long cooldownToUse = (personalCooldownMillis > 0) ? personalCooldownMillis : getCooldown();

        if (now - lastAttackTime < cooldownToUse) return;

        Enemy[] targets = targetEnemies(enemies);
        if (targets.length > 0) {
            attackEnemy(targets[0]);
            lastAttackTime = now;
        }
    }

    public boolean isInRange(Enemy enemy) {
        Point enemyPos = new Point(Math.round(enemy.getX()), Math.round(enemy.getY()));
        return position.distanceTo(enemyPos) <= range;
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

    // Existing cooldown methods - untouched
    public static long getCooldown() {
        return attackCooldownMillis;
    }

    public static void setCooldown(long cooldown) {
        attackCooldownMillis = cooldown;
    }

    public static void setCooldownToDefault() {
        attackCooldownMillis = 800;
    }


    public TowerLevel getLevel() {
        return level;
    }

    public boolean canUpgrade() {
        return level == TowerLevel.LEVEL1 && session.getPlayer().getGoldAmount() >= getUpgradeCost();
    }

    public void upgrade() {
        if (canUpgrade()) {
            session.getPlayer().deductGold(getUpgradeCost());
            level = TowerLevel.LEVEL2;
            applyLevel2Stats();
        }
    }


    public void setPersonalCooldown(long cooldown) {
        this.personalCooldownMillis = cooldown;
    }

    public long getPersonalCooldown() {
        return (personalCooldownMillis > 0) ? personalCooldownMillis : getCooldown();
    }

    public void resetPersonalCooldown() {
        this.personalCooldownMillis = -1;
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

    public abstract Enemy[] targetEnemies(List<Enemy> allEnemies);
    public abstract void attackEnemy(Enemy target);

    public abstract int getUpgradeCost();
    protected abstract void applyLevel2Stats();
}
