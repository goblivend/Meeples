package org.goblivend.meeples.utils;

import org.goblivend.meeples.game.Direction;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

public class Utils {
    public static Point move(Point p, Direction dir) {
        HashMap<Direction, Point> dirs = new HashMap<>();
        dirs.put(Direction.UP, new Point(0, -1));
        dirs.put(Direction.DOWN, new Point(0, 1));
        dirs.put(Direction.LEFT, new Point(-1, 0));
        dirs.put(Direction.RIGHT, new Point(1, 0));

        var curr = dirs.get(dir);
        return new Point(p.x + curr.x, p.y + curr.y);
    }

    public static Polygon createCursor(int size) {
        return new Polygon(
                new int[] {
                        size/2,
                        size*6/10,
                        size,
                        size*6/10,
                        size/2,
                        size*4/10,
                        0,
                        size*4/10
                },
                new int[] {
                        0,
                        size*4/10,
                        size/2,
                        size*6/10,
                        size,
                        size*6/10,
                        size/2,
                        size*4/10
                },
                8);
    }

    public static boolean outOfBounds(Point p, Dimension dim) {
        return p.x < 0 || p.y < 0 || p.x >= dim.width || p.y >= dim.height;
    }

    public static boolean isExtremeCorner(Point p, Dimension dim) {
        return (p.x == 0 && p.y == 0) || (p.x == dim.width-1 && p.y == dim.height-1);
    }

    public static boolean reachingWallFromDir(Direction dir, Point p, Dimension dim) {
        var next = move(p, dir);

        return outOfBounds(next, dim)
                || next.equals(new Point(0, 0))
                || next.equals(new Point(dim.width-1, dim.height-1));
    }

    public static Stream<Direction> orthogonalOf(Direction d) {
        return switch (d) {
            case UP, DOWN -> Arrays.stream(new Direction[]{Direction.LEFT, Direction.RIGHT});
            case LEFT, RIGHT -> Arrays.stream(new Direction[]{Direction.UP, Direction.DOWN});
        };
    }
}
