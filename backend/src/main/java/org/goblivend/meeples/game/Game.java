package org.goblivend.meeples.game;

import lombok.Getter;
import org.goblivend.meeples.utils.Tuple;
import org.goblivend.meeples.utils.Utils;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.goblivend.meeples.game.Direction.*;

/*
 * TODO: Create WebService
 */


public class Game {
    private final String name;
    @Getter
    private final Dimension dim;
    @Getter
    private final List<Point> beams = new ArrayList<>(); // Possibility to add and remove Beams => need to update players
    @Getter
    private final List<Point> spawns = new ArrayList<>(); // Possibility to add and remove Spawns
    private Map<MeepleType, Point> meeples;
    private final Map<Integer, Player> players;
    @Getter
    private final Cell[][] grid; // TODO: need to update players' grid when changing walls
    @Getter
    private Point target;
    private Instant timerStart;
    private Integer timerDuration = 60;
    @Getter
    private boolean timerRunning = false;

    private HashMap<Integer, Tuple<Integer, Map<MeepleType, Integer>>> selectedSolutions = new HashMap<>();

    public Game(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.dim = new Dimension((Integer) map.get("width"), (Integer) map.get("height"));
        this.grid = new Cell[dim.height][dim.width];
        for (int y = 0; y < dim.height; y++) {
            for (int x = 0; x < dim.width; x++) {
                grid[y][x] = new Cell(new Point(x, y));
            }
        }
        setupBoard(map);

        this.meeples = IntStream.range(0, spawns.size())
                .boxed()
                .collect(toMap(i -> MeepleType.values()[i], spawns::get));

        //        generateTarget();
        target = new Point(2, 0); // TODO: Change back to random


        this.players = new HashMap<>();
    }


    private void setupBoard(Map<String, Object> map) {
        for (int y = 0; y < dim.height; y++) {
            grid[y][0].setWall(LEFT, true);
            grid[y][dim.width - 1].setWall(RIGHT, true);
        }

        for (int x = 0; x < dim.width; x++) {
            grid[0][x].setWall(UP, true);
            grid[dim.height - 1][x].setWall(DOWN, true);
        }

        @SuppressWarnings("unchecked")
        Map<String, ArrayList<Map<String, Integer>>> walls = (Map<String, ArrayList<Map<String, Integer>>>) map.get("walls");

        ArrayList<Map<String, Integer>> verticalWalls = walls.get("vertical");
        ArrayList<Map<String, Integer>> horizontalWalls = walls.get("horizontal");

        for (Map<String, Integer> wall : horizontalWalls) {
            Integer x = wall.get("x");
            Integer y = wall.get("y");

            grid[y][x].setWall(DOWN, true);
            grid[y + 1][x].setWall(UP, true);
        }

        for (Map<String, Integer> wall : verticalWalls) {
            Integer x = wall.get("x");
            Integer y = wall.get("y");

            grid[y][x].setWall(RIGHT, true);
            grid[y][x + 1].setWall(LEFT, true);
        }

        @SuppressWarnings("unchecked")
        ArrayList<Map<String, Integer>> beams = (ArrayList<Map<String, Integer>>) map.get("beams");
        for (Map<String, Integer> beam : beams)
            this.beams.add(new Point(beam.get("x"), beam.get("y")));

        @SuppressWarnings("unchecked")
        ArrayList<Map<String, Integer>> spawns = (ArrayList<Map<String, Integer>>) map.get("spawns");
        for (Map<String, Integer> spawn : spawns)
            this.spawns.add(new Point(spawn.get("x"), spawn.get("y")));
    }

    public void printGrid() {
        for (int y = 0; y < dim.height; y++) {
            Cell[] line = grid[y];
            for (Cell cell : line) {
                System.out.print(cell.hasWall(UP) ? " - " : "   ");
            }
            System.out.println();
            for (int x = 0; x < dim.width; x++) {
                Cell cell = line[x];
                System.out.print(cell.hasWall(LEFT) ? "|" : " ");
                if (beams.contains(new Point(x, y)))
                    System.out.print("x");
                else
                    System.out.print(" ");
                System.out.print(cell.hasWall(RIGHT) ? "|" : " ");
            }
            System.out.println();
            for (Cell cell : line) {
                System.out.print(cell.hasWall(DOWN) ? " - " : "   ");
            }
            System.out.println();

        }
    }

    public void exportWalls() {
        StringBuilder sb = new StringBuilder();
        sb.append("  vertical:\n");
        for (int y = 0; y < dim.height; y++) {
            for (int x = 0; x < dim.width - 1; x++) {
                if (!grid[y][x].hasWall(RIGHT))
                    continue;
                sb.append(format("    - x: %s\n", x));
                sb.append(format("      y: %s\n", y));
            }
        }

        sb.append("  horizontal:\n");
        for (int y = 0; y < dim.height - 1; y++) {
            for (int x = 0; x < dim.width; x++) {
                if (!grid[y][x].hasWall(DOWN))
                    continue;
                sb.append(format("    - x: %s\n", x));
                sb.append(format("      y: %s\n", y));
            }
        }
        System.out.println(sb);
    }

    private void generateTarget() {
        Random rd = new Random();
        target = new Point(rd.nextInt(dim.width), rd.nextInt(dim.height));

        if (Utils.isExtremeCorner(target, dim) || !meeples.containsValue(target)) {
            generateTarget();
        }

        players.values().forEach(p -> p.setTarget(target));
    }

    public Map<MeepleType, Point> getMeeples(Integer playerId) {
        return players.get(playerId).getMeeples();
    }

    public Integer getNbMeeples() {
        return meeples.size();
    }

    public Point getMeeplePos(Integer meepleId) {
        return meeples.get(MeepleType.fromId(meepleId));
    }

    public List<Integer> getPlayerIds() {
        return players.keySet().stream().toList();
    }

    public Player getPlayer(Integer playerId) {
        return players.get(playerId);
    }
    public Integer addPlayer(String userName) {
        Integer playerId = players.size();
        players.put(playerId, new Player(playerId, userName, dim, grid, beams, target, new HashMap<>(meeples)));
        return playerId;
    }

    public Integer getNbPlayers() {
        return players.size();
    }

    public List<Point> superSpeed(Integer player, Integer meeple) {
        return players.get(player).superSpeed(MeepleType.fromId(meeple));
    }

    public List<Point> specialAbility(Integer player, Integer meeple) {
        return players.get(player).specialAbility(MeepleType.fromId(meeple));
    }

    public List<Point> getPossibleMoves(Integer meepleId, Integer playerId) {
        return Stream.concat(
                        superSpeed(playerId, meepleId).stream(),
                        specialAbility(playerId, meepleId).stream()
                ).collect(toSet())
                .stream()
                .toList();
    }

    public void resetCurrentMove(Integer playerID) {
        players.get(playerID).resetPlaying(new HashMap<>(meeples));
    }

    public Optional<Integer> getMeepleAt(Integer playerId, Point p) {
        return players.get(playerId).getMeepleAt(p).map(MeepleType::getId);
    }

    public Optional<String> moveMeepleTo(Integer meepleId, Integer playerId, Point p) {
        var res = players.get(playerId).moveMeepleTo(MeepleType.fromId(meepleId), p);

        // TODO: Detect when player wins =>
        // start Timer

        return res;
    }

    public Integer getCurrentTimer() {
        if (!timerRunning)
            return timerDuration;
        var timeLeft = timerDuration - (int) (Instant.now().getEpochSecond() - timerStart.getEpochSecond());
        if (timeLeft <= 0) {
            timerRunning = false;
            selectedSolutions = new HashMap<>();
            generateTarget();
            // TODO: Get best solution and save it as current turn
            players.values().forEach(player -> player.resetPlaying(new HashMap<>(meeples)));
            return 0;
        } // TODO: Add possibility to showcase the best solution

        return timeLeft;
    }

    public void selectSolution(Integer playerId) {
        Player player = players.get(playerId);
        if (!player.isOnTarget())
            return;

        var solution = player.getPlaying();
        var nbMoves = solution.size();
        selectedSolutions.put(playerId, new Tuple<>(nbMoves, solution));
        timerRunning = true;
        timerStart = Instant.now();
    }
}
