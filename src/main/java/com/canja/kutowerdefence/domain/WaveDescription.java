package com.canja.kutowerdefence.domain;

import java.util.Arrays;
import java.util.List;

public class WaveDescription {
    private int enemyGroupNumber;
    private int enemyGroupSize;
    private int reward;
    private int delayBetweenGroups;
    private final List<EnemyDescription> enemyTypes;

    public WaveDescription(int enemyGroupNumber, int enemyGroupSize, int reward, int delayBetweenGroups) {
        this.enemyGroupNumber = enemyGroupNumber;
        this.enemyGroupSize = enemyGroupSize;
        this.reward = reward;
        this.delayBetweenGroups = delayBetweenGroups;

        enemyTypes = Arrays.asList(
            EnemyFactory.GOBLIN,
            EnemyFactory.KNIGHT
        );
    }

    public List<EnemyDescription> getEnemyTypes() {
        return enemyTypes;
    }

    public int getEnemyGroupNumber() {
        return enemyGroupNumber;
    }

    public int getEnemyGroupSize() {
        return enemyGroupSize;
    }

    public int getReward() {
        return reward;
    }

    public int getDelay() {
        return delayBetweenGroups;
    }
}
