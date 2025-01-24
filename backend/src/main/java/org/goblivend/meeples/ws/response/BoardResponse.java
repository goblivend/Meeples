package org.goblivend.meeples.ws.response;


import lombok.AllArgsConstructor;

import java.awt.*;
import java.util.List;

@AllArgsConstructor
public class BoardResponse {

    public Integer width;
    public Integer height;
    public List<List<Cell>> board;
    public List<Point> spawns;
    public List<Point> beams;
    public Point target;
    // TODO: Add meeples to board details
    @AllArgsConstructor
    public static class Cell {
        public boolean wallUp;
        public boolean wallDown;
        public boolean wallLeft;
        public boolean wallRight;
    }
}
