package com.canja.kutowerdefence.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class MapTest {

    private Map map;

    @BeforeEach
    void setUp() {
        map = new Map(16, 12);
    }


    @Test
    public void testValidHorizontalPath() {
        map.setPathStartEnd(0, 0, 15, 0);

        for (int x = 0; x <= 15; x++) {
            map.editTile(x, 0, TileType.HORIZONTAL);
        }

        assertTrue(map.validatePath(), "Expected a valid horizontal path on top row.");
    }

    @Test
    public void testBrokenPath() {
        map.setPathStartEnd(0, 0, 15, 0);

        for (int x = 0; x <= 15; x++) {
            if (x != 7) {
                map.editTile(x, 0, TileType.HORIZONTAL);
            }
            // tile (7, 0) is default (non-walkable)
        }

        assertFalse(map.validatePath(), "Expected no valid path due to missing tile at (7, 0).");
    }

    @Test
    public void testValidVerticalPath() {
        map.setPathStartEnd(0, 0, 0, 11);

        for (int y = 0; y <= 11; y++) {
            map.editTile(0, y, TileType.VERTICAL);
        }

        assertTrue(map.validatePath(), "Expected a valid vertical path on left column.");
    }

    @Test
    public void testValidZigZagPathWithCorners() {
        // Start on top-left, end on right edge
        map.setPathStartEnd(0, 0, 15, 5);

        // Vertical path from (0,0) to (0,5)
        for (int y = 0; y <= 5; y++) {
            map.editTile(0, y, TileType.VERTICAL);
        }

        // Turn right at (0,5) to (1,5) using a bottom-left corner
        map.editTile(0, 5, TileType.CIRCULAR_BOTTOMLEFT);
        map.editTile(1, 5, TileType.HORIZONTAL);

        // Continue right to (15,5)
        for (int x = 2; x <= 15; x++) {
            map.editTile(x, 5, TileType.HORIZONTAL);
        }

        // Validate path
        boolean result = map.validatePath();
        assertTrue(result, "Expected valid zig-zag path from (0,0) to (15,5).");

        // Extra Test: Validate specific path points
        LinkedList<Point> path = map.getPath();
        assertNotNull(path);
        assertEquals(new Point(0, 0), path.getFirst());
        assertEquals(new Point(15, 5), path.getLast());
        assertTrue(path.contains(new Point(0, 3)));
        assertTrue(path.contains(new Point(1, 5)));
        assertTrue(path.contains(new Point(10, 5)));
    }


}