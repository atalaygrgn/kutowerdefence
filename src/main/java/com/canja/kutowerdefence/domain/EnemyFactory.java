package com.canja.kutowerdefence.domain;

public class EnemyFactory {
    public static final EnemyDescription GOBLIN = new EnemyDescription(
            "Goblin",
            100,
            40,
            10,
            10f,
            1
    );

    public static final EnemyDescription KNIGHT = new EnemyDescription(
            "Knight",
            100,
            10,
            40,
            8f,
            1
    );
}