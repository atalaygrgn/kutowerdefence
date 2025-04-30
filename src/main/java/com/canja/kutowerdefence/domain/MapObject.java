package com.canja.kutowerdefence.domain;

public class MapObject {

    private Point position;
    private MapObjectType type;

    public MapObject(MapObjectType type, Point position) {
        this.type = type;
        this.position = position;
    }

    public MapObject(MapObjectType type, int x, int y) {
        this.type = type;
        this.position = new Point(x, y);
    }

    public static MapObjectType getObjectType(int x, int y) {
        return MapObjectType.values()[4*x + y - 16];
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public MapObjectType getType() {
        return type;
    }

    public void setType(MapObjectType type) {
        this.type = type;
    }
}
