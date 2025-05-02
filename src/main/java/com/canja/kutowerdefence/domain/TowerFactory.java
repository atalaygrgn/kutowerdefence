package com.canja.kutowerdefence.domain;

public final class TowerFactory {

    public static Tower createTower(MapObjectType towerType, Point location, GameSession session) {
        return switch (towerType) {
            case TOWER_ARCHER -> new ArcherTower(location, session);
            case TOWER_ARTILLERY -> new ArtilleryTower(location, session);
            case TOWER_MAGE -> new MageTower(location, session);
            default -> null;
        };
    }
}
