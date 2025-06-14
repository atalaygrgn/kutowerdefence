package com.canja.kutowerdefence.domain;

import java.util.List;

public class ArcherTower extends Tower {

    public ArcherTower(Point position, GameSession gameSession, int range, int damage, int cost) {
        super(MapObjectType.TOWER_ARCHER, position, gameSession, range, damage, cost);
    }

    @Override
    public Enemy[] targetEnemies(List<Enemy> allEnemies) {
        Enemy best = getFurthestInRange(allEnemies);
        return best != null ? new Enemy[]{best} : new Enemy[0];
    }

    @Override
    public void attackEnemy(Enemy target) {
        session.getView().launchProjectile(
                position.getX(), position.getY(),
                target.getX(), target.getY(),
                "/assets/effects/Fire.png",
                128, 128, 7,
                () -> {
                    target.takeDamage(damage, "Arrow");
                    //DEBUG ICIN TEMP
                    System.out.println("Archer Tower attacked: " + target.getDescription().getName() + " HP=" + target.getHitpoint());
                }
        );
    }

    @Override
    public int getUpgradeCost() {
        return (int)(cost * 1.5);
    }

    @Override
    public void upgrade() {
        if (level == 1) {
            this.level = 2;
            this.range = (int)(this.range * 1.5);
            cooldownMillis /= 2;
        }
    }


}
