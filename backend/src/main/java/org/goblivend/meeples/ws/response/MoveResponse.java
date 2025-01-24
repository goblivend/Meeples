package org.goblivend.meeples.ws.response;

import lombok.AllArgsConstructor;

import java.awt.*;
import java.util.List;

@AllArgsConstructor
public class MoveResponse {
    public List<Point> basicMoves;
    public List<Point> specialMoves;
}
