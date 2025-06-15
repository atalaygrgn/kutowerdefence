package com.canja.kutowerdefence.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class TowerTest {
    private static class TestTower extends Tower {
        private Enemy lastTarget;

        public TestTower(Point position, GameSession gameSession, int range, int damage, int cost) {
            super(MapObjectType.TOWER_ARCHER, position, gameSession, range, damage, cost);
        }

        @Override
        public Enemy[] targetEnemies(List<Enemy> allEnemies) {
            Enemy target = getFurthestInRange(allEnemies);
            return target != null ? new Enemy[]{target} : new Enemy[0];
        }

        @Override
        public void attackEnemy(Enemy target) {
            lastTarget = target;
            target.takeDamage(damage, "Test");
        }

        public Enemy getLastTarget() {
            return lastTarget;
        }
    }

    private TestTower tower;
    private GameSession gameSession;
    private LinkedList<Point> path;

    @BeforeEach
    void setUp() {
        path = new LinkedList<>();
        path.add(new Point(0, 0));
        path.add(new Point(10, 10));

        File mapFile = new File("src/main/resources/maps/CANJA.kutdmap");
        gameSession = new GameSession(mapFile);
        tower = new TestTower(new Point(5, 5), gameSession, 3, 10, 100);
    }

    @Test
    void testTowerInitialization() {
        // TODO
    }

    @Test
    void testEnemyInRange() {
        // TODO
    }

    @Test
    void testAttackRespectsCooldown() {
        // TODO
    }

    @Test
    void testTargetFurthestEnemy() {
        // TODO
    }
}
