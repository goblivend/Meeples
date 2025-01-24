package org.goblivend.meeples.converter;

import org.goblivend.meeples.game.Cell;
import org.goblivend.meeples.game.Game;
import org.goblivend.meeples.ws.response.BoardResponse;

import java.util.Arrays;
import java.util.List;

import static org.goblivend.meeples.game.Direction.*;

public class BoardConverter {

    public static List<List<BoardResponse.Cell>> gridConverter(Cell[][] grid) {
        return Arrays.stream(grid)
                .map(line -> Arrays.stream(line)
                        .map(e -> new BoardResponse.Cell(
                                        e.hasWall(UP),
                                        e.hasWall(DOWN),
                                        e.hasWall(LEFT),
                                        e.hasWall(RIGHT)
                                )
                        ).toList()
                ).toList();
    }

    public static BoardResponse boardToResponse(Game game) {
        return new BoardResponse(
                game.getDim().width,
                game.getDim().height,
                gridConverter(game.getGrid()),
                game.getSpawns(),
                game.getBeams(),
                game.getTarget()
        );
    }
}
