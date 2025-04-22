package com.canja.kutowerdefence.domain;

import com.canja.kutowerdefence.ui.TileView;

import java.util.Arrays;

public class MapEditor {

    private Map map;
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
        int index = Arrays.asList(TileType.values()).indexOf(tileType);
        return new int[] {index / 4, index % 4};
    }
}
