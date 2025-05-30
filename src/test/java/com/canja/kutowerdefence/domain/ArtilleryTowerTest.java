package com.canja.kutowerdefence.domain;

import com.canja.kutowerdefence.ui.GamePlayView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class ArtilleryTowerTest {

    private GameSession session;
    private EnemyDescription dummyDesc;

    @BeforeEach
    void setUp() {
        File realMapFile = new File("src/main/resources/maps/MAP2.kutdmap");
        session = new GameSession(realMapFile);
        session.setGamePlayView(new HeadlessGamePlayView());
        session.start();
        TowerFactory.setRange(0, 5, 0);
        TowerFactory.setDamage(0, 20, 0);
        TowerFactory.setAoeRadius(3);
        Tower.setCooldown(0);
        dummyDesc = new EnemyDescription("TestDummy", 100, 0, 0, 1.0f, 10);
    }

    // TEST 1
    @Test
    void furthestInRange() throws Exception {
        Enemy nearEnemy = new Enemy(dummyDesc, session.getMap().getPath());
        Enemy farEnemy = new Enemy(dummyDesc, session.getMap().getPath());
        setEnemyPosition(nearEnemy, 2, 2);
        setEnemyPosition(farEnemy, 4, 2);
        session.addEnemy(nearEnemy);
        session.addEnemy(farEnemy);

        Tower artillery = TowerFactory.createTower(MapObjectType.TOWER_ARTILLERY, new Point(1, 1), session);
        session.addTower(artillery);

        session.tick(1.0f);

        assertTrue(farEnemy.getHitpoint() < 100, "Furthest enemy should be hit");
        assertTrue(nearEnemy.getHitpoint() < 100, "Near enemy also within AOE should be hit");
    }

    // TEST 2
    @Test
    void onlyWithinAOE() throws Exception {
        Enemy centerEnemy = new Enemy(dummyDesc, session.getMap().getPath());
        Enemy closeEnemy = new Enemy(dummyDesc, session.getMap().getPath());
        Enemy outsideEnemy = new Enemy(dummyDesc, session.getMap().getPath());

        setEnemyPosition(centerEnemy, 2, 2);
        setEnemyPosition(closeEnemy, 6, 2);
        setEnemyPosition(outsideEnemy, 10, 10);

        session.addEnemy(centerEnemy);
        session.addEnemy(closeEnemy);
        session.addEnemy(outsideEnemy);

        Tower artillery = TowerFactory.createTower(MapObjectType.TOWER_ARTILLERY, new Point(1, 1), session);
        session.addTower(artillery);

        session.tick(1.0f);

        assertTrue(centerEnemy.getHitpoint() < 100, "Center enemy should be damaged");
        assertEquals(100, closeEnemy.getHitpoint(), "Close enemy outside AOE should not be damaged");
        assertEquals(100, outsideEnemy.getHitpoint(), "Far enemy outside AOE should not be damaged");
    }
    //
    // TEST 3
    @Test
    void cooldownCheck() throws Exception {
        Enemy enemy = new Enemy(dummyDesc, session.getMap().getPath());
        setEnemyPosition(enemy, 2, 2);
        session.addEnemy(enemy);

        Tower.setCooldown(100000);
        Tower artillery = TowerFactory.createTower(MapObjectType.TOWER_ARTILLERY, new Point(1, 1), session);
        session.addTower(artillery);

        session.tick(1.0f);
        int hpAfterFirstTick = enemy.getHitpoint();

        session.tick(1.0f);
        int hpAfterSecondTick = enemy.getHitpoint();

        assertEquals(hpAfterFirstTick, hpAfterSecondTick, "Tower should not attack again due to cooldown");
    }

    private void setEnemyPosition(Enemy enemy, float x, float y) throws Exception {
        Field fieldX = Enemy.class.getDeclaredField("x");
        Field fieldY = Enemy.class.getDeclaredField("y");
        fieldX.setAccessible(true);
        fieldY.setAccessible(true);
        fieldX.set(enemy, x);
        fieldY.set(enemy, y);
    }

    static class HeadlessGamePlayView extends GamePlayView {
        @Override
        public void launchProjectile(int fromX, int fromY, float toX, float toY,
                                     String spritePath, int frameW, int frameH, int frameCount,
                                     Runnable onHit) {
            onHit.run();
        }
    }
}


