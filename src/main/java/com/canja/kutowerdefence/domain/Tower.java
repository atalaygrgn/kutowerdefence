package com.canja.kutowerdefence.domain;

import java.util.List;

public abstract class Tower extends MapObject {


    public abstract Enemy[] targetEnemies(List<Enemy> allEnemies);
    public abstract void attackEnemy(Enemy target);
    protected int range;
    protected GameSession session;
    protected long lastAttackTime = 0;
    protected long attackCooldownMillis = 800;

    public Tower(MapObjectType type, Point position) {
        super(type, position);
    }

    public Tower(MapObjectType type, Point position, GameSession session) {
        super(type, position);
        this.session = session;
        this.range = session.getOptionValues()[16];

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


}
