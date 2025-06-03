package com.canja.kutowerdefence.domain;

public class Wave {
    private final WaveDescription description;
    private EnemyGroup activeGroup;
    private int delayBetweenGroups;
    private int enemyGroupNumber;
    private int currentGroupIndex;
    private int reward;

    public Wave(WaveDescription description) {
        this.description = description;
        delayBetweenGroups = description.getDelay();
        enemyGroupNumber = description.getEnemyGroupNumber();
        currentGroupIndex = 0;
        reward = description.getReward();
    }

    public boolean hasEnemies() {
        return currentGroupIndex < enemyGroupNumber;
    }

    public void proceedNextGroup() {
        currentGroupIndex++;
        activeGroup = EnemyGroupFactory.generateRandomGroup(description);
    }

    public EnemyGroup getActiveGroup() {
        return activeGroup;
    }

    public int getDelay() {
        return delayBetweenGroups;
    }

    public int getReward() {
        return reward;
    }

    public int getEnemyGroupNumber() {
        return enemyGroupNumber;
    }

    public int getCurrentGroup() {
        return currentGroupIndex;
    }
}
