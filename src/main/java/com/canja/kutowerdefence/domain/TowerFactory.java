package com.canja.kutowerdefence.domain;

public final class TowerFactory {
    private static final int ARCHER_INDEX = 0; 
    private static final int ARTILLERY_INDEX = 1;
    private static final int MAGE_INDEX = 2;
    private static int[] range = new int[3];
    private static int[] damage = new int[3];
    private static int[] cost = new int[3];
    private static int aoeRadius;

    public static void setRange(int val1, int val2, int val3) {
        range[ARCHER_INDEX] = val1;
        range[ARTILLERY_INDEX] = val2;
        range[MAGE_INDEX] = val3;
    }

    public static void setDamage(int val1, int val2, int val3) {
        damage[ARCHER_INDEX] = val1;
        damage[ARTILLERY_INDEX] = val2;
        damage[MAGE_INDEX] = val3;
    }

    public static void setCost(int val1, int val2, int val3) {
        cost[ARCHER_INDEX] = val1;
        cost[ARTILLERY_INDEX] = val2;
        cost[MAGE_INDEX] = val3;
    }

    public static void setAoeRadius(int val) {
        aoeRadius = val;
    }

    public static Tower createTower(MapObjectType towerType, Point location, GameSession gameSession) {
        return switch (towerType) {
            case TOWER_ARCHER -> new ArcherTower(location, gameSession, range[ARCHER_INDEX], damage[ARCHER_INDEX], cost[ARCHER_INDEX]);
            case TOWER_ARTILLERY -> new ArtilleryTower(location, gameSession, range[ARTILLERY_INDEX], damage[ARTILLERY_INDEX], cost[ARTILLERY_INDEX], aoeRadius);
            case TOWER_MAGE -> new MageTower(location, gameSession, range[MAGE_INDEX], damage[MAGE_INDEX], cost[MAGE_INDEX]);
            default -> null;
        };
    }

    public static boolean upgradeTower(Tower tower, Player player) {
        if (!tower.canUpgrade()) return false;

        int upgradeCost = tower.getUpgradeCost();
        if (player.getGoldAmount() < upgradeCost) return false;

        player.deductGold(upgradeCost);
        tower.upgrade();
        return true;
    }


}
