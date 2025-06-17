package com.canja.kutowerdefence.domain;

import java.util.List;

public class MageTower extends Tower {

    public MageTower(Point position, GameSession gameSession, int range, int damage, int cost) {
        super(MapObjectType.TOWER_MAGE, position, gameSession, range, damage, cost);
    }

    @Override
    public Enemy[] targetEnemies(List<Enemy> allEnemies) {
        Enemy best = getFurthestInRange(allEnemies);
        return best != null ? new Enemy[]{best} : new Enemy[0];
    }

    @Override
    public void attackEnemy(Enemy target) {
        if (level == TowerLevel.LEVEL2) {

            session.getView().launchProjectile(
                    position.getX(), position.getY(),
                    target.getX(), target.getY(),
                    "/assets/effects/Ice.png",
                    128, 128, 7,
                    () -> {
                        target.takeDamage(damage, "Spell");
                        target.applySlow(0.8, 4000);
                        if (Math.random() < 0.03) { // 3% chance to teleport
                            target.teleportToStart();
                        }
                        System.out.println("Mage L2 hit: " + target.getDescription().getName() + " HP=" + target.getHitpoint());
                    },
                    false
            );
        } else {

            session.getView().launchProjectile(
                    position.getX(), position.getY(),
                    target.getX(), target.getY(),
                    "/assets/effects/Fire.png",
                    128, 128, 7,
                    () -> {
                        target.takeDamage(damage, "Spell");
                        if (Math.random() < 0.03) { // 3% chance to teleport
                            target.teleportToStart();
                        }
                        System.out.println("Mage hit: " + target.getDescription().getName() + " HP=" + target.getHitpoint());
                    },
                    false
            );
        }
    }

    @Override
    public int getUpgradeCost() {
        // Return 0 for level 2 towers to indicate they are maxed out
        if (level == TowerLevel.LEVEL2) {
            return 0;
        }
        return cost + (cost * 3 / 5);
    }

    @Override
    protected void applyLevel2Stats() {
        this.range = (int) Math.round(this.range * 1.3);
        this.damage = (int) Math.round(this.damage * 1.5);
        this.setPersonalCooldown(600);
    }
}
