package com.canja.kutowerdefence.domain;

import java.util.List;

public class EnemyGroup {
    private List<EnemyDescription> enemies;
    private int delayBetweenEnemies;

    public EnemyGroup(List<EnemyDescription> enemies, int delay) {
        this.enemies = enemies;
        this.delayBetweenEnemies = delay;
    }

    public List<EnemyDescription> getEnemies() {
        return enemies;
    }

    public int getDelay() {
        return delayBetweenEnemies;
    }
}
