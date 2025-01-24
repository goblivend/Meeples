package org.goblivend.meeples.game;

import java.awt.*;

public enum MeepleType {
    GREEN("FORREST JUMP", Color.GREEN),
    GRAY("OZZY MOSIS", Color.GRAY),
    BLUE("BLUE BEAMER", Color.BLUE),
    BROWN("SHORTSTOP", new Color(77, 58, 4)),
    RED("SIDESTEP", Color.RED),
    WHITE("SKEWT", Color.WHITE),
    YELLOW("MC EDGE", Color.YELLOW),
    BLACK("CARBON", Color.BLACK);

    public final String name;
    public final Color color;
    public final int id = this.ordinal();

    MeepleType(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public static MeepleType fromId(int id) {
        for (MeepleType type : MeepleType.values()) {
            if (type.id == id)
                return type;
        }
        return null;
    }

    public int getId() {
        return id;
    }
}
