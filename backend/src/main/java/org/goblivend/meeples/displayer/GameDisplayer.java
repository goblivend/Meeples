package org.goblivend.meeples.displayer;

import org.goblivend.meeples.game.Cell;
import org.goblivend.meeples.game.Game;
import org.goblivend.meeples.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Optional.empty;
import static org.goblivend.meeples.game.Direction.*;

class GameDisplayer extends JPanel {
    private static final Integer DEFAULT_CELL_SIZE = 48;
    private Game game;
    private Integer cellSize;
    private Integer betweenSize;
    private Integer wallSize;
    private Integer usableSize;
    private Point gridDelta;
    private final Consumer<String> updateLog;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Integer> selectedMeeple = empty();
    private final Integer selectedPlayer;
    private final java.util.List<Integer> playerIds;

    public GameDisplayer(Game game, Consumer<String> updateLog, Integer selectedPlayer) {
        this.game = game;
        this.selectedPlayer = selectedPlayer;
        this.cellSize = DEFAULT_CELL_SIZE;
        calculateSubSizes();
        this.updateLog = updateLog;
        this.playerIds = game.getPlayerIds();

        setOpaque(true);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(
                (game.getDim().width + 2) * (cellSize),
                (game.getDim().height + 2) * (cellSize)));
        createSizeUpdateListener();
    }

    public void setCellSize(Integer cellSize) {
        this.cellSize = cellSize;
        calculateSubSizes();
    }

    public void setGame(Game game) {
        this.game = game;
    }

    private void calculateSubSizes() {
        this.betweenSize = cellSize * 3 / 100;
        this.wallSize = cellSize * 20 / 100;
        this.usableSize = (cellSize - wallSize);
        this.gridDelta = new Point(cellSize, cellSize);
    }

    public void updateSize() {
        var gridDim = game.getDim();
        System.out.printf("New Size of GameDisplayer: %s\n", getSize());
        if (getSize().height < gridDim.height * 8) {
            System.out.println("Size too small");
            return;
        }

        int maxGridDim = Math.max(gridDim.width, gridDim.height);
        int minDim = Math.min(getWidth(), getHeight());
        setCellSize(minDim / (maxGridDim + 2));
    }

    private void createSizeUpdateListener() {
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSize();
            }
        });
    }

    public Optional<Integer> getSelectedMeeple() {
        return selectedMeeple;
    }

    public void updateSelectedMeeple() {
        if (selectedMeeple.isPresent()) {
            var selected = selectedMeeple.get();
            selected++;
            selected %= game.getNbMeeples();
            selectedMeeple = Optional.of(selected);
        }
    }

    public void unSelectMeeple() {
        System.out.println("Unselecting Meeple");
        selectedMeeple = empty();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        paintGrid(g, game.getGrid());
    }

    private void paintGrid(Graphics g, Cell[][] grid) {
        super.paintComponent(g);
        paintBackGround(g);
        paintWalls(g, grid);

        paintTarget(g);
        paintBeams(g);
        paintSpawns(g);
        paintMeeples(g);

        paintSelection(g);
        paintSpecial(g);
        paintSuperSpeed(g);
    }

    private void paintBackGround(Graphics g) {
        final int width = game.getDim().width;
        final int height = game.getDim().height;

        g.setColor(Color.BLACK);
        g.fillRect(gridDelta.x, gridDelta.y, width * cellSize + betweenSize, height * cellSize + betweenSize);
        g.setColor(Color.WHITE);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if ((x == 0 && y == 0) || (x == width - 1 && y == height - 1))
                    continue;
                if (x % 2 == 1 && y % 2 == 1)
                    g.setColor(new Color(120, 120, 128));
                else if (x % 2 == 1 || y % 2 == 1)
                    g.setColor(new Color(140, 140, 145));
                else
                    g.setColor(new Color(172, 172, 172));

                g.fillRect(x * cellSize + gridDelta.x + betweenSize, y * cellSize + gridDelta.x + betweenSize, cellSize - betweenSize, cellSize - betweenSize);
            }
        }
    }

    private void paintWalls(Graphics g, Cell[][] grid) {
        final int width = game.getDim().width;
        final int height = game.getDim().height;

        g.setColor(Color.BLACK);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x].hasWall(DOWN))
                    g.fillRoundRect(
                            x * cellSize + gridDelta.x - wallSize / 2,
                            (y + 1) * cellSize + gridDelta.y - wallSize / 2,
                            cellSize + wallSize,
                            wallSize,
                            wallSize,
                            wallSize);
                if (grid[y][x].hasWall(RIGHT))
                    g.fillRoundRect(
                            (x + 1) * cellSize + gridDelta.x - wallSize / 2,
                            y * cellSize + gridDelta.y - wallSize / 2,
                            wallSize,
                            cellSize + wallSize,
                            wallSize,
                            wallSize);

                if (grid[y][x].hasWall(UP))
                    g.fillRoundRect(
                            x * cellSize + gridDelta.x - wallSize / 2,
                            y * cellSize + gridDelta.y - wallSize / 2,
                            cellSize + wallSize,
                            wallSize,
                            wallSize,
                            wallSize);
                if (grid[y][x].hasWall(LEFT))
                    g.fillRoundRect(
                            x * cellSize + gridDelta.x - wallSize / 2,
                            y * cellSize + gridDelta.y - wallSize / 2,
                            wallSize,
                            cellSize + wallSize,
                            wallSize,
                            wallSize);
            }
        }
    }

    private void paintTarget(Graphics g) {
        var p = game.getTarget();
        g.setColor(Color.RED);
        fillOvalAt(g, p, usableSize, usableSize);
        g.setColor(Color.WHITE);
        fillOvalAt(g, p, usableSize * 2 / 3, usableSize * 2 / 3);
        g.setColor(Color.RED);
        fillOvalAt(g, p, usableSize / 3, usableSize / 3);
    }

    private void fillOvalAt(Graphics g, Point p, int width, int height) {
        int x = p.x * cellSize + gridDelta.x + (cellSize - width) / 2;
        int y = p.y * cellSize + gridDelta.x + (cellSize - height) / 2;

        g.fillOval(x, y, width, height);

    }

    private void paintBeams(Graphics g) {
        g.setColor(new Color(0, 166, 255, 128));
        game.getBeams().forEach(p ->
                fillOvalAt(g, p, usableSize, usableSize));
    }

    private void paintSpawns(Graphics g) {
        g.setColor(new Color(100, 100, 100, 128));
        game.getSpawns().forEach(p -> fillOvalAt(g, p, usableSize, usableSize));
    }

    private void paintMeeples(Graphics g) {
        game.getMeeples(selectedPlayer)
                .forEach((key, value) -> {
                    g.setColor(key.color);
                    fillOvalAt(g, value, usableSize * 4 / 5, usableSize * 4 / 5);
                });
    }

    private void paintCursorAt(Graphics g, Point p) {
        var cursor = Utils.createCursor(usableSize);
        cursor.translate(
                p.x * cellSize + gridDelta.x + wallSize / 2,
                p.y * cellSize + gridDelta.y + wallSize / 2);
        g.fillPolygon(cursor);
    }

    private void paintSelection(Graphics g) {
        if (getSelectedMeeple().isEmpty())
            return;
        getSelectedMeeple()
                .map(i -> game.getMeeplePos(i))
                .ifPresent(pos -> {
                    g.setColor(Color.MAGENTA);
                    paintCursorAt(g, pos);
                });
    }

    private void paintSuperSpeed(Graphics g) {
        getSelectedMeeple()
                .stream()
                .flatMap(i -> game.superSpeed(playerIds.get(selectedPlayer), i).stream())
                .forEach(p -> {
                    g.setColor(Color.ORANGE);
                    paintCursorAt(g, p);
                });
    }

    private void paintSpecial(Graphics g) {
        getSelectedMeeple()
                .stream()
                .flatMap(i -> game.specialAbility(playerIds.get(selectedPlayer), i).stream())
                .forEach(p -> {
                    g.setColor(new Color(230, 255, 144));
                    paintCursorAt(g, p);
                });
    }

    public void updateWall(MouseEvent e) {
        final int width = game.getDim().width;
        final int height = game.getDim().height;

        int x = e.getX() - 8 - gridDelta.x - this.getX();
        int y = e.getY() - 32 - gridDelta.y - this.getY();

        int cellX = x / cellSize;
        int cellY = y / cellSize;

        int beforeX = cellX * cellSize;
        int beforeY = cellY * cellSize;

        int afterX = (cellX + 1) * cellSize;
        int afterY = (cellY + 1) * cellSize;

        int nearestX = x - beforeX < afterX - x ? beforeX : afterX;
        int nearestY = y - beforeY < afterY - y ? beforeY : afterY;

        if (Math.abs(x - nearestX) < Math.abs(y - nearestY)) {
            cellX = nearestX / cellSize;
            cellX--;

            if (cellX + 1 >= width || cellY >= height)
                return;

            Cell cell = game.getGrid()[cellY][cellX];
            Cell nextCell = game.getGrid()[cellY][cellX + 1];

            cell.setWall(RIGHT, !cell.hasWall(RIGHT));
            nextCell.setWall(LEFT, !nextCell.hasWall(LEFT));
        } else {
            cellY = nearestY / cellSize;
            cellY--;

            if (cellX >= width || cellY + 1 >= height)
                return;

            Cell cell = game.getGrid()[cellY][cellX];
            Cell nextCell = game.getGrid()[cellY + 1][cellX];

            cell.setWall(DOWN, !cell.hasWall(DOWN));
            nextCell.setWall(UP, !nextCell.hasWall(UP));
        }
    }

    private Point getCellFromClick(MouseEvent e) {
        int x = e.getX() - 8 - gridDelta.x - this.getX();
        int y = e.getY() - 32 - gridDelta.y - this.getY();

        return new Point(x / cellSize, y / cellSize);
    }

    public void recordMouseEvent(MouseEvent e) {
        Point p = getCellFromClick(e);
        if (selectedMeeple.isPresent() && game.getPossibleMoves(selectedMeeple.get(), selectedPlayer).contains(p))
            moveTo(p);
        else
            selectMeeple(p);
    }

    private void selectMeeple(Point p) {
        selectedMeeple = game.getMeepleAt(selectedPlayer, p);
    }

    private void moveTo(Point p) {
        if (selectedMeeple.isEmpty())
            return;

        var error = game.moveMeepleTo(selectedMeeple.get(), playerIds.get(selectedPlayer), p);
        if (error.isEmpty())
            return;

        updateLog.accept(error.get());
    }

    public void exportWalls() {
        game.exportWalls();
    }
}