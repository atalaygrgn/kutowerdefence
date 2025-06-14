package com.canja.kutowerdefence.domain;

import java.util.List;

public class ArtilleryTower extends Tower {
    private int aoeRadius;

    public ArtilleryTower(Point position, GameSession gameSession, int range, int damage, int cost, int aoeRadius) {
        super(MapObjectType.TOWER_ARTILLERY, position, gameSession, range, damage, cost);
        this.aoeRadius = aoeRadius;
    }

    @Override
    public Enemy[] targetEnemies(List<Enemy> allEnemies) {
        Enemy best = getFurthestInRange(allEnemies);
        return best != null ? new Enemy[]{best} : new Enemy[0];
    }




/*
  REQUIRES: target must be an alive enemy currently present in the game session's enemy list.
  MODIFIES: the hitpoints of any enemies within the area-of-effect radius of the target location.
  EFFECTS: launches a projectile animation from this tower's position to the target's location.
           Once the projectile lands, it applies damage to all enemies within aoeRadius
            of the impact location.
 */

    @Override
    public void attackEnemy(Enemy target) {
        float impactX = target.getX();
        float impactY = target.getY();

        session.getView().launchProjectile(
                position.getX(), position.getY(),
                impactX, impactY,
                "/assets/effects/Explosions.png",
                192, 192, 10,
                () -> {
                    for (Enemy e : session.getEnemies()) {
                        float dx = e.getX() - impactX;
                        float dy = e.getY() - impactY;
                        double dist = Math.sqrt(dx * dx + dy * dy);

                        if (dist <= aoeRadius) {
                            e.takeDamage(damage, "Shell");
                            //DEBUG ICIN
                            //System.out.println("Artillery AOE hit " + e.getDescription().getName() +
                            //        " (dist, " + dist + ") HP, " + e.getHitpoint());
                        }
                    }
                } , false
        );
    }
}
