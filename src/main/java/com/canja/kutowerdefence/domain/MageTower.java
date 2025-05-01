package com.canja.kutowerdefence.domain;

public class MageTower extends Tower {

    public MageTower(Point position) {
        super(MapObjectType.TOWER_MAGE, position);
    }

    @Override
    public Enemy[] targetEnemies() {
        return new Enemy[0];
    }

    @Override
    public void attackEnemy(Enemy target) {

    }
}
