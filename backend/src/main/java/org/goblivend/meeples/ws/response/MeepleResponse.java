package org.goblivend.meeples.ws.response;

import java.awt.*;

public class MeepleResponse {
    public String name;
    public Point pos;

    public MeepleResponse(String name, Point pos) {
        this.name = name;
        this.pos = pos;
    }
}
