package com.canja.kutowerdefence.domain;

import java.util.List;

public class MageTower extends Tower {

    public MageTower(Point position, GameSession gameSession, int range, int damage) {
        super(MapObjectType.TOWER_MAGE, position, gameSession, range, damage);
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
                    target.takeDamage(damage, "Spell");
                    //DBG ICN
                    System.out.println("Mage Tower attacked: " + target.getDescription().getName() + " HP=" + target.getHitpoint());
                }
        );
    }
}
