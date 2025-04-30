package com.canja.kutowerdefence.domain;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class Map {

    private Tile[][] map;
    private Point[] pathStartEnd;
    private LinkedList<Point> path = new LinkedList<>();

    public Map() {
        map =  new Tile[16][12];
        pathStartEnd = new Point[2];
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 12; y++) {
                map[x][y] = new Tile();
            }
        }
    }

    public Tile[][] getArray() {
        return map;
    }

    public Tile getTile(int x, int y) {
        return map[x][y];
    }

    public void editTile(int x, int y, TileType tileType) {
        map[x][y].setTileType(tileType);
    }

    public Tile[][] reset() {
        map = new Tile[16][12];
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 12; y++) {
                map[x][y] = new Tile();
            }
        }
        return map;
    }

    public Point[] getPathStartEnd() {
        return pathStartEnd;
    }

    public void setPathStartEnd(int x1, int y1, int x2, int y2) {
        pathStartEnd[0] = new Point(x1, y1);
        pathStartEnd[1] = new Point(x2, y2);
    }

    public LinkedList<Point> getPath() {
        return path;
    }

    public void setPath(LinkedList<Point> path) {
        this.path = path;
    }

    public void loadFromFile(File mapFile) throws IOException {
        String content = new String(Files.readAllBytes(mapFile.toPath()));
        Gson gson = new Gson();
        Object[] data = gson.fromJson(content, Object[].class);

        // Deserialize map
        int[][] serializedMap = gson.fromJson(gson.toJson(data[0]), int[][].class);
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 12; y++) {
                TileType type = TileType.values()[serializedMap[x][y]];
                editTile(x, y, type);
            }
        }

        // Deserialize path start/end points
        int[][] pathStartEndData = gson.fromJson(gson.toJson(data[1]), int[][].class);
        if (pathStartEndData.length == 2) {
            setPathStartEnd(pathStartEndData[0][0], pathStartEndData[0][1], pathStartEndData[1][0], pathStartEndData[1][1]);
        }

        // Deserialize path linked list
        int[][] pathData = gson.fromJson(gson.toJson(data[2]), int[][].class);
        LinkedList<Point> path = new LinkedList<>();
        for (int[] point : pathData) {
            path.add(new Point(point[0], point[1]));
        }
        setPath(path);
    }

    public boolean validatePath() {
        if (pathStartEnd[0] != null && pathStartEnd[1] != null) {
            Point start = pathStartEnd[0];
            Point end = pathStartEnd[1];

            System.out.println("Start Point: " + start.getX() + ", " + start.getY());
            System.out.println("End Point: " + end.getX() + ", " + end.getY());

            // Ensure start and end points are within bounds
            if (!isEdgeTile(start.getX(), start.getY()) || !isEdgeTile(end.getX(), end.getY())) {
                return false; // Start or end point is not on the edge
            }

            LinkedList<Point> queue = new LinkedList<>();
            boolean[][] visited = new boolean[16][12];
            LinkedList<Point> tempPath = new LinkedList<>();

            queue.add(start);
            visited[start.getX()][start.getY()] = true;

            while (!queue.isEmpty()) {
                Point current = queue.poll();
                tempPath.add(current); // Add current point to the temporary path
                if (current.getX() == end.getX() && current.getY() == end.getY()) {
                    path = new LinkedList<>(tempPath); // Set the found path to path
                    return true; // Path found
                }

                Tile currentTile = getTile(current.getX(), current.getY());
                System.out.println("Current Point: " + current.getX() + ", " + current.getY());
                System.out.println("Current Tile Accessibility: " + getPathAccessibilityOfTileType(currentTile.getTileType()));
                System.out.println("Current Tile Type: " + currentTile.getTileType());
                System.out.println("Visited: " + visited[current.getX()][current.getY()]);
                boolean[] accessibility = getPathAccessibilityOfTileType(currentTile.getTileType());

                // Check neighbors based on accessibility
                if (accessibility[0] && current.getY() > 0) { // Top
                    Point neighbor = new Point(current.getX(), current.getY() - 1);
                    if (!visited[neighbor.getX()][neighbor.getY()] &&
                        getPathAccessibilityOfTileType(getTile(neighbor.getX(), neighbor.getY()).getTileType())[2]) {
                        queue.add(neighbor);
                        visited[neighbor.getX()][neighbor.getY()] = true;
                    }
                }
                if (accessibility[1] && current.getX() < 15) { // Right
                    Point neighbor = new Point(current.getX() + 1, current.getY());
                    if (!visited[neighbor.getX()][neighbor.getY()] &&
                        getPathAccessibilityOfTileType(getTile(neighbor.getX(), neighbor.getY()).getTileType())[3]) {
                        queue.add(neighbor);
                        visited[neighbor.getX()][neighbor.getY()] = true;
                    }
                }
                if (accessibility[2] && current.getY() < 11) { // Bottom
                    Point neighbor = new Point(current.getX(), current.getY() + 1);
                    if (!visited[neighbor.getX()][neighbor.getY()] &&
                        getPathAccessibilityOfTileType(getTile(neighbor.getX(), neighbor.getY()).getTileType())[0]) {
                        queue.add(neighbor);
                        visited[neighbor.getX()][neighbor.getY()] = true;
                    }
                }
                if (accessibility[3] && current.getX() > 0) { // Left
                    Point neighbor = new Point(current.getX() - 1, current.getY());
                    if (!visited[neighbor.getX()][neighbor.getY()] &&
                        getPathAccessibilityOfTileType(getTile(neighbor.getX(), neighbor.getY()).getTileType())[1]) {
                        queue.add(neighbor);
                        visited[neighbor.getX()][neighbor.getY()] = true;
                    }
                }
            }
        }
        return false; // No valid path found
    }

    public static boolean isEdgeTile(int x, int y) {
        return x == 0 || y == 0 || x == 15 || y == 11;
    }

    private boolean[] getPathAccessibilityOfTileType(TileType tileType) {
        switch (tileType) { // Accessible from where? {top, right, bottom, left}
            case CIRCULAR_TOPLEFT -> {
                return new boolean[]{false, true, true, false};
            }
            case CIRCULAR_TOPCENTER -> {
                return new boolean[]{false, true, false, true};
            }
            case CIRCULAR_TOPRIGHT -> {
                return new boolean[]{false, false, true, true};
            }
            case CIRCULAR_CENTERLEFT -> {
                return new boolean[]{true, false, true, false};
            }
            case CIRCULAR_CENTERRIGHT -> {
                return new boolean[]{true, false, true, false};
            }
            case CIRCULAR_BOTTOMLEFT -> {
                return new boolean[]{true, true, false, false};
            }
            case CIRCULAR_BOTTOMCENTER -> {
                return new boolean[]{false, true, false, true};
            }
            case CIRCULAR_BOTTOMRIGHT -> {
                return new boolean[]{true, false, false, true};
            }
            case HORIZONTAL -> {
                return new boolean[]{false, true, false, true};
            }
            case VERTICAL -> {
                return new boolean[]{true, false, true, false};
            }
            case TOP -> {
                return new boolean[]{false, false, true, false};
            }
            case BOTTOM -> {
                return new boolean[]{true, false, false, false};
            }
            case LEFT -> {
                return new boolean[]{false, true, false, false};
            }
            case RIGHT -> {
                return new boolean[]{false, false, false, true};
            }
            default -> {
                return new boolean[]{false, false, false, false};
            }

        }
    }
}
