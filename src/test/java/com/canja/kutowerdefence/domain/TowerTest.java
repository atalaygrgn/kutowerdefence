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
        assertEquals(3, tower.range);
        assertEquals(10, tower.damage);
        assertEquals(100, tower.cost);
        assertEquals(new Point(5, 5), tower.position);
        assertEquals(MapObjectType.TOWER_ARCHER, tower.getType());
    }

    @Test
    void testEnemyInRange() {
        LinkedList<Point> path1 = new LinkedList<>();
        path1.add(new Point(4, 4));
        Enemy enemy1 = new Enemy(EnemyFactory.GOBLIN, path1);
        assertTrue(tower.isInRange(enemy1), "Enemy should be in range");

        LinkedList<Point> path2 = new LinkedList<>();
        path2.add(new Point(9, 9));
        Enemy enemy2 = new Enemy(EnemyFactory.GOBLIN, path2);
        assertFalse(tower.isInRange(enemy2), "Enemy should be out of range");
    }

    @Test
    void testAttackRespectsCooldown() {
        LinkedList<Point> enemyPath = new LinkedList<>();
        enemyPath.add(new Point(4, 4));
        Enemy enemy = new Enemy(EnemyFactory.GOBLIN, enemyPath);
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(enemy);

        tower.tryAttack(enemies); // First attack
        Enemy firstTarget = tower.getLastTarget();
        assertNotNull(firstTarget, "Tower should have attacked");

        tower.tryAttack(enemies); // Second attack attempt (should be blocked by cooldown)
        assertEquals(firstTarget, tower.getLastTarget(),
                "Tower should not have attacked again due to cooldown");
    }

    @Test
    void testTargetFurthestEnemy() {
        LinkedList<Point> path1 = new LinkedList<>();
        path1.add(new Point(4, 4));
        path1.add(new Point(5, 5));
        Enemy enemy1 = new Enemy(EnemyFactory.GOBLIN, path1);

        LinkedList<Point> path2 = new LinkedList<>();
        path2.add(new Point(6, 6));
        path2.add(new Point(7, 7));
        path2.add(new Point(8, 8));
        Enemy enemy2 = new Enemy(EnemyFactory.GOBLIN, path2);
        // Update enemy2's position by simulating movement
        enemy2.update(1.0f); // Move to make it further along its path

        List<Enemy> enemies = new ArrayList<>();
        enemies.add(enemy1);
        enemies.add(enemy2);

        Enemy[] targets = tower.targetEnemies(enemies);
        assertEquals(1, targets.length, "Should target exactly one enemy");
        assertEquals(enemy2, targets[0], "Should target the enemy further along the path");
    }
}
