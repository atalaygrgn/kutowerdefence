package com.canja.kutowerdefence.domain;

import com.canja.kutowerdefence.ui.TileView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class MapEditor {

    private final Map map;
    private TileType selectedTileType;

    public MapEditor() {
        this.map = new Map();
        this.selectedTileType = TileType.EMPTY;
    }

    public Map getMap() {
        return map;
    }

    public TileType getSelectedTileType() {
        return selectedTileType;
    }

    public void changeSelectedTile(TileType tileType) {
        this.selectedTileType = tileType;
    }

    public void penTool(int x, int y, TileView tileView) {
        editTile(x, y, selectedTileType);
        tileView.setTileType(selectedTileType);
    }

    private void editTile(int x, int y, TileType tileType) {
        map.editTile(x, y, tileType);
    }

    public static int[] getPaletteCoords(TileType tileType) {
        int index = tileType.ordinal();
        return new int[] {index / 4, index % 4};
    }

    public void resetMap() {
        map.reset();
    }

    public void saveMap(String filename) {
        try {
            // Convert the map to a serializable format
            int[][] serializedMap = new int[16][12];
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 12; y++) {
                    serializedMap[x][y] = map.getTile(x, y).getTileType().ordinal();
                }
            }

            // Serialize path start/end points
            Point[] pathStartEnd = map.getPathStartEnd();
            int[][] pathStartEndData = new int[2][2];
            if (pathStartEnd[0] != null && pathStartEnd[1] != null) {
                pathStartEndData[0][0] = pathStartEnd[0].getX();
                pathStartEndData[0][1] = pathStartEnd[0].getY();
                pathStartEndData[1][0] = pathStartEnd[1].getX();
                pathStartEndData[1][1] = pathStartEnd[1].getY();
            }

            // Serialize path linked list
            LinkedList<Point> path = map.getPath();
            int[][] pathData = new int[path.size()][2];
            for (int i = 0; i < path.size(); i++) {
                pathData[i][0] = path.get(i).getX();
                pathData[i][1] = path.get(i).getY();
            }

            // Create Gson instance
            Gson gson = new GsonBuilder().create();

            // Write to file
            try (FileWriter writer = new FileWriter(filename)) {
                gson.toJson(new Object[]{serializedMap, pathStartEndData, pathData}, writer);
            }

            System.out.println("Map saved successfully to " + filename);
        } catch (IOException e) {
            System.err.println("Failed to save map: " + e.getMessage());
        }
    }

    public void loadMap(String filename) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        Gson gson = new Gson();
        Object[] data = gson.fromJson(content, Object[].class);

        // Deserialize map
        int[][] serializedMap = gson.fromJson(gson.toJson(data[0]), int[][].class);
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 12; y++) {
                TileType type = TileType.values()[serializedMap[x][y]];
                map.editTile(x, y, type);
            }
        }

        // Deserialize path start/end points
        int[][] pathStartEndData = gson.fromJson(gson.toJson(data[1]), int[][].class);
        if (pathStartEndData.length == 2) {
            map.setPathStartEnd(pathStartEndData[0][0], pathStartEndData[0][1], pathStartEndData[1][0], pathStartEndData[1][1]);
        }

        // Deserialize path linked list
        int[][] pathData = gson.fromJson(gson.toJson(data[2]), int[][].class);
        LinkedList<Point> path = new LinkedList<>();
        for (int[] point : pathData) {
            path.add(new Point(point[0], point[1]));
        }
        map.setPath(path);
    }
}
