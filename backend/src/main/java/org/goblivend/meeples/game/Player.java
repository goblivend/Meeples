package org.goblivend.meeples.game;

import lombok.Getter;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.goblivend.meeples.utils.Tuple;
import org.goblivend.meeples.utils.Tuple3;
import org.goblivend.meeples.utils.Utils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toSet;

public class Player {
    // TODO: Store the meeples placement in the player object to allow mutltiplayer
    // Need the base turn meeples in the Game as well
    // Might try with a boolean[][] for the free board, walls don't need to be player specific
    // Edit: boolean[][] seems overkill, a simple map <Meeple.Type, Point> should be enough, will need to put meeple movements in the player object
    // Also need a way to retrace a player's moves in order to show the solution found to the other players

    @Getter
    private final Integer id; // Instance Dependant
    @Getter
    private final String name; // Instance Dependant
    private Dimension dim; // Game Dependant
    private Cell[][] grid; // Game Dependant
    private List<Point> beams; // Game Dependant
    private Point target; // Turn Dependant
    private Map<MeepleType, Point> meeples; // Turn Dependant
    private final Map<MeepleType, Function<Point, List<Point>>> abitilyDispatchers = new HashMap<>(); // Instance Dependant
    private List<MeepleType> played; // Game Dependant
    private Map<MeepleType, Integer> playing; // Turn Dependant
    private List<MeepleType> specialPlaying; // Turn Dependant

    public Player(Integer id, String name, Dimension dim, Cell[][] grid, List<Point> beams, Point target, Map<MeepleType, Point> meeples) {
        this.id = id;
        this.name = name;
        this.dim = dim;
        this.grid = grid;
        this.beams = beams;
        this.target = target;
        this.meeples = meeples;
        this.abitilyDispatchers.put(MeepleType.YELLOW, this::specialYellow);
        this.abitilyDispatchers.put(MeepleType.BLUE, this::specialBlue);
        this.abitilyDispatchers.put(MeepleType.BROWN, this::specialBrown);
        this.abitilyDispatchers.put(MeepleType.RED, this::specialRed);
        this.abitilyDispatchers.put(MeepleType.GRAY, this::specialGray);
        this.abitilyDispatchers.put(MeepleType.GREEN, this::specialGreen);
        this.abitilyDispatchers.put(MeepleType.WHITE, this::specialWhite);
        this.abitilyDispatchers.put(MeepleType.BLACK, this::specialBlack);
        this.played = new ArrayList<>();
        this.playing = new HashMap<>();
        this.specialPlaying = new ArrayList<>();
    }

    /**************************
     ******** Getters *********
     **************************/

    public Integer nbMoves() {
        return playing.keySet()
                .stream()
                .map(k -> playing.get(k))
                .reduce(Integer::sum)
                .orElse(0);
    }

    public Map<MeepleType, Point> getMeeples() {
        return new HashMap<>(meeples);
    }

    public boolean isOnTarget() {
        return meeples.containsValue(target);
    }

    public Map<MeepleType, Integer> getPlaying() {
        return new HashMap<>(playing);
    }

    /**************************
     ******** Setters *********
     **************************/

    public void setGrid(Cell[][] grid) {
        this.grid = grid;
    }

    public void setBeams(List<Point> beams) {
        this.beams = beams;
    }

    public void setTarget(Point target) {
        this.target = target;
    }

    /************************************
     * Utility functions for the player *
     ************************************/

    public void use(MeepleType type) {
        played.add(type);
    }

    public boolean isInSpecialTeam(MeepleType type) {
        return played.contains(type);
    }

    public boolean canPlaySpecial(MeepleType type) {
        return !specialPlaying.contains(type);
    }

    public void resetPlaying(Map<MeepleType, Point> meeples) {
        this.playing = new HashMap<>();
        this.specialPlaying = new ArrayList<>();
        this.meeples = meeples;
    }

    public void play(MeepleType type) {
        playing.put(type, playing.getOrDefault(type, 0) + 1);
    }

    public void playSpecial(MeepleType type) {
        playing.put(type, playing.getOrDefault(type, 0) + 1);
        specialPlaying.add(type);
    }

    public Integer nbPlaying() {
        return playing.size();
    }

    public boolean isPlaying(MeepleType type) {
        return playing.containsKey(type);
    }
    public Integer getTimesPlaying(MeepleType type) {
        return playing.getOrDefault(type, 0);
    }

    public boolean playingSpecial(MeepleType type) {
        return specialPlaying.contains(type);
    }

    public Optional<MeepleType> getMeepleAt(Point p) {
        return meeples.entrySet()
                .stream()
                .filter(e -> e.getValue().equals(p))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private boolean canPlayMeeple(MeepleType meeple) {
        return !(isInSpecialTeam(meeple) || nbMoves() >= 24 || getTimesPlaying(meeple) >= 10
                || (!isPlaying(meeple) && nbPlaying() == 3));
    }

    /****************************************************
     ******** Methods to move meeples on the board ******
     ****************************************************/

    public List<Point> superSpeed(MeepleType meeple) {
        if (!canPlayMeeple(meeple))
            return new ArrayList<>();

        return Arrays.stream(Direction.values())
                .map(d -> moveWhilePossible(meeples.get(meeple), d))
                .filter(p -> !p.equals(meeples.get(meeple))).toList();
    }

    public List<Point> specialAbility(MeepleType meeple) {
        if (!canPlayMeeple(meeple) || !canPlaySpecial(meeple))
            return new ArrayList<>();

        var superSpeed = superSpeed(meeple);

        return abitilyDispatchers
                .get(meeple)
                .apply(meeples.get(meeple))
                .stream()
                .filter(p -> !superSpeed.contains(p))
                .toList();
    }

    public Optional<String> moveMeepleTo(MeepleType meeple, Point p) {
        var movesSimple = superSpeed(meeple);
        var movesSpecial = specialAbility(meeple);

        if (isInSpecialTeam(meeple)) {
            System.err.printf("Player:%d can't reuse meeple: %s\n", this.id, meeple.name);
            return of(format("Player:%d can't reuse meeple: %s\n", this.id, meeple.name));
        }

        if (!movesSimple.contains(p) && !movesSpecial.contains(p)) {
            System.err.printf("Invalid move for meeple: %s, for player: %d, dst: %s\n", meeple.name, this.id, p);
            return of(format("Invalid move for meeple: %s, for player: %d, dst: %s\n", meeple.name, this.id, p));
        }

        if (nbPlaying() == 3 && !isPlaying(meeple)) {
            System.err.printf("Player:%d using too many meeples\n", this.id);
            return of(format("Player:%d using too many meeples\n", this.id));
        }

        if (nbMoves() >= 24) {
            System.err.printf("Player:%d using too many moves: %d\n", this.id, nbMoves());
            return of(format("Player:%d using too many moves: %d\n", this.id, nbMoves()));
        }

        if (!movesSimple.contains(p) && playingSpecial(meeple)) {
            System.err.printf("Player:%d already used special ability of :%s\n", this.id, meeple.name);
            return of(format("Player:%d already used special ability of :%s\n", this.id, meeple.name));
        }

        System.out.printf("Player: %d moving meeple:%s to: %s\n", this.id, meeple.name, p);

        if (movesSimple.contains(p))
            play(meeple);
        else
            playSpecial(meeple);

        meeples.put(meeple, p);

        if (p.equals(target)) {
            use(meeple);
        }

        return empty();
    }

    private List<Point> specialYellow(Point p) {
        return Arrays.stream(Direction.values())
                .map(d -> new Tuple<>(d,
                        moveWhilePossible(p, d)
                ))
                .filter(t -> Utils.reachingWallFromDir(t.t1(), t.t2(), dim))
                .map(t -> new Tuple<>(t.t1(), oppositeSide(t.t2(), t.t1())))
                .filter(t -> isFree(t.t2()))
                .map(t -> moveWhilePossible(t.t2(), t.t1()))
                .filter(this::isFree)
                .toList();
    }

    private List<Point> specialBlue(Point p) {
        return beams.stream()
                .filter(this::isFree)
                .toList();
    }

    private List<Point> specialBrown(Point p) {
        return Arrays.stream(Direction.values())
                .map(d -> {
                    var cell = grid[p.y][p.x];
                    var prevCell = cell;

                    while (!cell.hasWall(d)) {
                        var nextPos = Utils.move(cell.pos, d);

                        if (!isFree(nextPos))
                            break;

                        prevCell = cell;
                        cell = grid[nextPos.y][nextPos.x];
                    }
                    return prevCell.pos;
                })
                .filter(this::isFree)
                .toList();
    }

    private List<Point> specialRed(Point p) {
        return Arrays.stream(Direction.values())
                .map(d -> {
                    var cell = grid[p.y][p.x];

                    if (!cell.hasWall(d)) {
                        var nextPos = Utils.move(cell.pos, d);

                        if (isFree(nextPos))
                            return nextPos;
                    }
                    return cell.pos;
                })
                .filter(this::isFree)
                .toList();
    }

    private List<Point> specialGray(Point p) {
        return Arrays.stream(Direction.values())
                .map(d -> new Tuple<>(d,
                        moveWhilePossible(p, d)
                ))
                .filter(t -> !Utils.reachingWallFromDir(t.t1(), t.t2(), dim))
                .map(t -> new Tuple<>(t.t1(), Utils.move(t.t2(), t.t1())))
                .map(t -> moveWhilePossible(t.t2(), t.t1()))
                .filter(this::isFree)
                .toList();
    }


    private List<Point> specialGreen(Point p) {
        return Arrays.stream(Direction.values())
                .map(d -> {
                    var cell = grid[p.y][p.x];
                    for (int i = 0; i < 3; i++) {
                        var nextPos = Utils.move(cell.pos, d);
                        if (Utils.outOfBounds(nextPos, dim))
                            return new Tuple<>(false, nextPos);
                        cell = grid[nextPos.y][nextPos.x];
                    }
                    return new Tuple<>(isFree(cell.pos), cell.pos);
                })
                .filter(Tuple::t1)
                .map(Tuple::t2)
                .filter(this::isFree)
                .toList();
    }

    private List<Point> specialWhite(Point p) {
        BiFunction<Point, Direction, Tuple3<Boolean, Point, Direction>> moveOne = (point, d) -> {
            var cell = grid[point.y][point.x];

            if (cell.hasWall(d))
                return new Tuple3<>(false, cell.pos, d);

            var nextPos = Utils.move(cell.pos, d);
            cell = grid[nextPos.y][nextPos.x];
            return new Tuple3<>(true, cell.pos, d);
        };

        return Arrays.stream(Direction.values())
                .map(d -> moveOne.apply(p, d))
                .filter(Tuple3::t1)
                .flatMap(t3 -> Utils.orthogonalOf(t3.t3()).map(d -> moveOne.apply(t3.t2(), d)))
                .filter(Tuple3::t1)
                .map(Tuple3::t2)
                .filter(this::isFree)
                .collect(toSet())
                .stream()
                .toList();
    }

    private List<Point> specialBlack(Point p) {
        return Arrays.stream(MeepleType.values())
                .filter(type -> type != MeepleType.BLACK) // Avoid Recursive call
                .filter(this::canPlaySpecial)
                .map(abitilyDispatchers::get)
                .flatMap(ability -> ability.apply(p).stream())
                .collect(toSet())
                .stream()
                .toList();
    }


    /******************************************************
     ******** Utility functions for meeple movements ******
     ******************************************************/

    private boolean isFree(Point p) {
        return !meeples.containsValue(p);
    }

    private Point moveWhilePossible(Point point, Direction d) {
        var cell = grid[point.y][point.x];

        while (!cell.hasWall(d)) {
            var nextPos = Utils.move(cell.pos, d);

            if (!isFree(nextPos))
                break;
            cell = grid[nextPos.y][nextPos.x];
        }
        return cell.pos;
    }

    private Point oppositeSide(Point p, Direction d) {
        var res = switch (d) {
            case DOWN -> new Point(p.x, 0);
            case UP -> new Point(p.x, dim.height - 1);
            case LEFT -> new Point(dim.width - 1, p.y);
            case RIGHT -> new Point(0, p.y);
        };

        if (res.equals(new Point(0, 0)) || res.equals(new Point(dim.width - 1, dim.height - 1)))
            return Utils.move(res, d);
        return res;
    }
}
