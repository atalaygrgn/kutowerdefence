package com.canja.kutowerdefence.domain;

public final class TowerFactory {

    public static Tower createTower(MapObjectType towerType, Point location) {
        return switch (towerType) {
            case TOWER_ARCHER -> new ArcherTower(location);
            case TOWER_ARTILLERY -> new ArtilleryTower(location);
            case TOWER_MAGE -> new MageTower(location);
            default -> null;
        };
    }
}
