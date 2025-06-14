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
        System.out.printf("Arrow fired at enemy pos: (%.2f, %.2f)%n", target.getX(), target.getY());
        session.getView().launchProjectile(
                position.getX(), position.getY(),
                target.getX(), target.getY(),
                "/assets/effects/Arrow.png",
                32, 32, 1,
                () -> {
                    target.takeDamage(damage, "Arrow");
                    //DEBUG ICIN TEMP
                    //System.out.println("Archer Tower attacked: " + target.getDescription().getName() + " HP=" + target.getHitpoint());
                }, true
        );
    }
}
