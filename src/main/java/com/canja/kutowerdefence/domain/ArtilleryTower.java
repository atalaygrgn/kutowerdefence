package com.canja.kutowerdefence.domain;

public class ArtilleryTower extends Tower {
    public ArtilleryTower(Point position) {
        super(MapObjectType.TOWER_ARTILLERY, position);
    }

    @Override
    public Enemy[] targetEnemies() {
        return new Enemy[0];
    }

    @Override
    public void attackEnemy(Enemy target) {

    }
}
