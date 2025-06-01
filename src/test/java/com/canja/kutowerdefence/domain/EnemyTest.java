

package com.canja.kutowerdefence.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class EnemyTest {

    private Enemy enemy;

    @BeforeEach
    void setUp() {
        EnemyDescription desc = new EnemyDescription("Goblin", 100, 10, 20, 1.0f, 5);
        LinkedList<Point> path = new LinkedList<>();
        path.add(new Point(0, 0));
        path.add(new Point(1, 0));

        enemy = new Enemy(desc, path);
    }

    @Test
    void testDamageArrow() {
        enemy.takeDamage(50, "Arrow"); // 20% resistance -> 40 damage
        assertEquals(60, enemy.getHitpoint(), "Arrow resistance not applied correctly");
    }

    @Test
    void testDamageSpell() {
        enemy.takeDamage(50, "Spell"); // 10% resistance -> 45 damage
        assertEquals(55, enemy.getHitpoint(), "Spell resistance not applied correctly");
    }

    @Test
    void testDamageNoResistance() {
        EnemyDescription desc = new EnemyDescription("Goblin", 80, 0, 0, 1.0f, 5);
        LinkedList<Point> apath = new LinkedList<>();
        apath.add(new Point(0, 0));
        apath.add(new Point(1, 0));
        Enemy noResistEnemy = new Enemy(desc, apath);
        noResistEnemy.takeDamage(30, "Arrow");
        assertEquals(50, noResistEnemy.getHitpoint());
    }

    @Test
    void testDamageOverkill() {
        enemy.takeDamage(999, "Spell"); // more than hitpoint
        assertTrue(enemy.getHitpoint() <= 0, "Enemy should be dead");
    }

    @Test
    void testDamageUnknownType() {
        int before = enemy.getHitpoint();
        enemy.takeDamage(10, "Unknown"); // unknown type = full damage
        assertEquals(before - 10, enemy.getHitpoint(), "Unknown type should deal full damage");
    }
}

