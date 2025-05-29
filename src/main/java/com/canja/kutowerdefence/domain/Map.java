package com.canja.kutowerdefence.domain;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedList;

public class Map {
    private static int dimX;
    private static int dimY; 
    private Tile[][] map;
    private Point[] pathStartEnd;
    private LinkedList<Point> path = new LinkedList<>();
    private ArrayList<MapObject> objects = new ArrayList<>();

    public Map(int m, int n) {
        dimX = m;
        dimY = n;
        map =  new Tile[dimX][dimY];
        pathStartEnd = new Point[2];
        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                map[x][y] = new Tile();
            }
        }
    }

    public static int getXDimention() {
        return dimX;
    }

    public static int getYDimention() {
        return dimY;
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

    public void addObject(MapObject object) {
        objects.add(object);
    }

    public ArrayList<MapObject> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<MapObject> objects) {
        this.objects = objects;
    }

    public Tile[][] reset() {
        pathStartEnd = new Point[2];
        path.clear();
        objects.clear();
        map = new Tile[dimX][dimY];
        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
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
        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
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

        // Deserialize map objects
        ArrayList<ArrayList<Double>> objectData = gson.fromJson(gson.toJson(data[3]), ArrayList.class);
        ArrayList<MapObject> objects = new ArrayList<>();
        for (ArrayList<Double> obj : objectData) {
            int typeOrdinal = obj.get(0).intValue();
            int x = obj.get(1).intValue();
            int y = obj.get(2).intValue();
            MapObjectType type = MapObjectType.values()[typeOrdinal];
            objects.add(new MapObject(type, new Point(x, y)));
        }
        setObjects(objects);
    }

    public boolean validatePath() {
        /*
         * REQUIRES:
         * - The map must be initialized i.e. map is not null.
         * - pathStartEnd must contain two non-null Point objects representing the start and end positions of the path.
         * - Each tile in the map must have a defined TileType.
         *
         * MODIFIES:
         * - this.path: updated to store the valid traversable path if found.
         *
         * EFFECTS:
         * - Returns true if a valid path exists between the start and end Points using tiles with valid accessibility.
         * - The path must start and end on edge tiles of the map.
         * - The search performs a BFS traversal, verifying that each step follows the access rules of TileType
         *   (see getPathAccessibilityOfTileType).
         * - If a valid path is found, it is stored in path.
         * - Returns false if:
         *   - Either start or end point is not on the map edge.
         *   - No path is found between the two points using valid tiles.
         */

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
            boolean[][] visited = new boolean[dimX][dimY];
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
                if (accessibility[1] && current.getX() < dimX - 1) { // Right
                    Point neighbor = new Point(current.getX() + 1, current.getY());
                    if (!visited[neighbor.getX()][neighbor.getY()] &&
                            getPathAccessibilityOfTileType(getTile(neighbor.getX(), neighbor.getY()).getTileType())[3]) {
                        queue.add(neighbor);
                        visited[neighbor.getX()][neighbor.getY()] = true;
                    }
                }
                if (accessibility[2] && current.getY() < dimY - 1) { // Bottom
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
        return x == 0 || y == 0 || x == dimX - 1 || y == dimY - 1;
    }

    private boolean[] getPathAccessibilityOfTileType(TileType tileType) {
        switch (tileType) { // Accessible from where? {top, right, bottom, left}
            case CIRCULAR_TOPLEFT -> {
                return new boolean[]{false, true, true, false};
            }
            case CIRCULAR_TOPCENTER, CIRCULAR_BOTTOMCENTER, HORIZONTAL -> {
                return new boolean[]{false, true, false, true};
            }
            case CIRCULAR_TOPRIGHT -> {
                return new boolean[]{false, false, true, true};
            }
            case CIRCULAR_CENTERLEFT, CIRCULAR_CENTERRIGHT, VERTICAL -> {
                return new boolean[]{true, false, true, false};
            }
            case CIRCULAR_BOTTOMLEFT -> {
                return new boolean[]{true, true, false, false};
            }
            case CIRCULAR_BOTTOMRIGHT -> {
                return new boolean[]{true, false, false, true};
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
    // Returns direction of the path: 1 for right, 2 for down, 3 for up, 4 for right
    public int getDirection() {
        Point point1 = path.get(0);
        Point point2 = path.get(1);
        int x1=point1.getX();
        int y1=point1.getY();
        int x2=point2.getX();
        int y2=point2.getY();
        System.out.println(""+point1+""+point2+""+x1+""+y1+""+x2+""+y2);

        int direction = 0;
        if(x1!=x2){
            if(x1+1==x2){ direction=1;}
            else if(x1-1==x2){ direction=4;}
        } else {
            if(y1+1==y2){ direction=2;}
            else if (y1-1==y2){ direction=3;}
        }
        return direction;
    }
}