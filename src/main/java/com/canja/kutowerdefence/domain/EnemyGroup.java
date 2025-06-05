package com.canja.kutowerdefence.domain;

import java.util.List;

public class EnemyGroup {
    private List<EnemyDescription> enemies;
    private float delayBetweenEnemies;

    public EnemyGroup(List<EnemyDescription> enemies, float delay) {
        this.enemies = enemies;
        this.delayBetweenEnemies = delay;
    }

    public List<EnemyDescription> getEnemies() {
        return enemies;
    }

    public float getDelay() {
        return delayBetweenEnemies;
    }

    public void setDelay(float delay) {
        this.delayBetweenEnemies = delay;
    }
}
