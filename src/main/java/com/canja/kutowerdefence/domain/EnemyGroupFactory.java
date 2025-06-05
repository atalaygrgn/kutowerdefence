package com.canja.kutowerdefence.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyGroupFactory {
    public static float delayBetweenEnemies = 1;

    public static EnemyGroup generateRandomGroup(WaveDescription description) {
        List<EnemyDescription> result = new ArrayList<>();
        List<EnemyDescription> pool = description.getEnemyTypes();
        int enemyCount = description.getEnemyGroupSize();

        if (pool == null || pool.isEmpty()) {
            throw new IllegalArgumentException("ERROR: Pool is empty.");
        }

        Random random = new Random();

        for (int i = 0; i < enemyCount; i++) {
            int index = random.nextInt(pool.size());
            EnemyDescription selected = pool.get(index);
            result.add(selected);
        }
        
        EnemyGroup group = new EnemyGroup(result, delayBetweenEnemies);

        return group;
    }

    public static float getDelay() {
        return delayBetweenEnemies;
    }

    public static void setDelay(float delay) {
        delayBetweenEnemies = delay;
    }
}
