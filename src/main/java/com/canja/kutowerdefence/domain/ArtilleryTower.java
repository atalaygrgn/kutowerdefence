package com.canja.kutowerdefence.domain;

import java.util.List;

public class ArtilleryTower extends Tower {
    public ArtilleryTower(Point position, GameSession session) {
        super(MapObjectType.TOWER_ARTILLERY, position, session);
    }

    @Override
    public Enemy[] targetEnemies(List<Enemy> allEnemies) {
        Enemy best = getFurthestInRange(allEnemies);
        return best != null ? new Enemy[]{best} : new Enemy[0];
    }

    @Override
    public void attackEnemy(Enemy target) {
        int damage = session.getOptionValues()[14];
        int aoeRadius = session.getOptionValues()[17];

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
                            System.out.println("Artillery AOE hit " + e.getDescription().getName() +
                                    " (dist, " + dist + ") HP, " + e.getHitpoint());
                        }
                    }
                }
        );
    }


}
