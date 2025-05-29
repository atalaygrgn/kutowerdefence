package com.canja.kutowerdefence.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.canja.kutowerdefence.domain.GameSession;
import com.canja.kutowerdefence.domain.Map;
import com.canja.kutowerdefence.domain.MapObjectType;
import com.canja.kutowerdefence.domain.Player;
import com.canja.kutowerdefence.domain.TileType;
import com.canja.kutowerdefence.domain.TowerFactory;

public class BuyingTowerTest {
    private GameSession gameSession;
    private File mapFile;
    private Player player;

    @BeforeEach
    void setUp() {
        mapFile = new File("src/main/resources/maps/CANJA.kutdmap");
        gameSession = new GameSession(mapFile);
        player = gameSession.getPlayer();
    }
    
    @Test
    public void testValidBuyRequest() {
        MapObjectType type = MapObjectType.TOWER_ARCHER;
        //Adjust player money for testing
        player.deductGold(player.getGoldAmount());
        player.gainGold(150);
        //Adjust tower price for testing
        TowerFactory.setCost(125, 250, 375);
        
        boolean success = gameSession.buyNewTower(2, 4, type);

        assertTrue(success, "Expected a successful purchase.");
        assertTrue(player.getGoldAmount() == 25, "Player must have 25 golds after the purchase");
        assertTrue(gameSession.getNewestTower() != null, "Bought tower should be added to active towers");
    }

    @Test
    public void testInsufficientAmountOfGold() {
        MapObjectType type = MapObjectType.TOWER_ARTILLERY;
        //Adjust player money for testing
        player.deductGold(player.getGoldAmount());
        player.gainGold(150);
        //Adjust tower price for testing
        TowerFactory.setCost(125, 250, 375);
        
        boolean success = gameSession.buyNewTower(2, 4, type);

        assertFalse(success, "Expected a failed purchase due to insufficient balance.");
        assertTrue(player.getGoldAmount() == 150, "Player gold should not have changed after a failed purchase");
        assertTrue(gameSession.getNewestTower() == null, "No tower should be bought thus added to active towers list");

    }

    @Test
    public void testInvalidCoordinates() {
        MapObjectType type = MapObjectType.TOWER_MAGE;
        //Adjust player money for testing
        player.deductGold(player.getGoldAmount());
        player.gainGold(150);
        //Adjust tower price for testing
        TowerFactory.setCost(125, 250, 375);
        
        boolean success = gameSession.buyNewTower(18, 4, type);

        assertFalse(success, "Expected a failed purchase due to invalid coordinates.");
        assertTrue(player.getGoldAmount() == 150, "Player gold should not have changed after a failed purchase");
        assertTrue(gameSession.getNewestTower() == null, "No tower should be bought thus added to active towers list");

    }
}
