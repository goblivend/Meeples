package org.goblivend.meeples.game;

import java.awt.*;
import java.util.HashMap;

public class Cell {

    /**
     * Each Direction set to true if there is a wall
     */
    protected final HashMap<Direction, Boolean> wallMap;
    protected final Point pos;

    public Cell(Point pos) {
        this.pos = pos;
        wallMap = new HashMap<>();
        wallMap.put(Direction.UP, false);
        wallMap.put(Direction.DOWN, false);
        wallMap.put(Direction.LEFT, false);
        wallMap.put(Direction.RIGHT, false);
    }

    public void setWall(Direction dir, Boolean wall) {
        wallMap.put(dir, wall);
    }

    public Boolean hasWall(Direction dir)  {
        return wallMap.get(dir);
    }
}
