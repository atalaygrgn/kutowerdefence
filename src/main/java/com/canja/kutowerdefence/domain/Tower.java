package com.canja.kutowerdefence.domain;

import com.canja.kutowerdefence.domain.MapObject;

public abstract class Tower extends MapObject {

    public Tower(MapObjectType type, Point position) {
        super(type, position);
    }

    public abstract Enemy[] targetEnemies();
    public abstract void attackEnemy(Enemy target);
}
