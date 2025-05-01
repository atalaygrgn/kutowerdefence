package com.canja.kutowerdefence.domain;

public class ArcherTower extends Tower {

    public ArcherTower(Point position) {
        super(MapObjectType.TOWER_ARCHER, position);
    }

    @Override
    public Enemy[] targetEnemies() {
        return new Enemy[0];
    }

    @Override
    public void attackEnemy(Enemy target) {

    }
}
