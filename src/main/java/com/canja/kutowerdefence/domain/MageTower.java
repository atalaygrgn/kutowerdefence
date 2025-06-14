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


        session.getView().launchProjectile(
                position.getX(), position.getY(),
                target.getX(), target.getY(),
                level == 1 ? "/assets/effects/Fire.png" : "/assets/effects/BlueFire.png", // 🔹 Different graphic
                128, 128, 7,
                () -> {
                    target.takeDamage(damage, "Spell");
                    if (Math.random() < 0.03) {
                        target.teleportToStart();
                    }

                    //DBG ICN
                    System.out.println("Mage Tower attacked: " + target.getDescription().getName() + " HP=" + target.getHitpoint());
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
        }
    }


}
