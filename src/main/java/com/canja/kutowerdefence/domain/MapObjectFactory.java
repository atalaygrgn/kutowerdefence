package com.canja.kutowerdefence.domain;

public final class MapObjectFactory {

    public static MapObject createMapObject(MapObjectType mapObjectType, int x, int y) {
        return new MapObject(mapObjectType, x, y);

    }
}
