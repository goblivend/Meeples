package org.goblivend.meeples.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.goblivend.Main;
import org.goblivend.meeples.converter.BoardConverter;
import org.goblivend.meeples.game.Game;
import org.goblivend.meeples.game.MeepleType;
import org.goblivend.meeples.ws.response.*;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.io.InputStream;
import java.util.*;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class GameService {
    private final Map<String, Object> defaultMap;
    private final Map<String, Game> games = new HashMap<>();

    public GameService() {
        Yaml yaml = new Yaml();
        InputStream inputStream = Main.class
                .getClassLoader()
                .getResourceAsStream("map-game-1.yml");
        defaultMap = yaml.load(inputStream);
    }

    public List<String> getGames() {
        return new ArrayList<>(games.keySet());
    }

    public boolean gameExists(String gameId) {
        return games.containsKey(gameId);
    }

    public String createGame(String gameId) {
        Game game = new Game(defaultMap);
        games.put(gameId, game);
        return gameId;
    }

    public Integer joinGame(String gameId, String userName) {
        Game game = games.get(gameId);
        return game.addPlayer(userName);
    }

    public BoardResponse getGame(String gameId) {
        Game game = games.get(gameId);
        return BoardConverter.boardToResponse(game); // TODO: Change Converters should not have access to games
    }

    public MoveResponse getMovesFromMeeple(String gameId, Integer userId, Integer posX, Integer posY) {
        Game game = games.get(gameId);

        return game.getMeepleAt(userId, new Point(posX, posY))
                .map(meeple -> new MoveResponse(
                        game.superSpeed(userId, meeple),
                        game.specialAbility(userId, meeple)))
                .orElse(new MoveResponse(new ArrayList<>(), new ArrayList<>()));
    }

    public void moveMeeple(String gameId, Integer userId, Integer srcX, Integer srcY, Integer dstX, Integer dstY) {
        Game game = games.get(gameId);
        Integer meepleId = game.getMeepleAt(userId, new Point(srcX, srcY)).orElseThrow();


        game.moveMeepleTo(meepleId, userId, new Point(dstX, dstY));
    }

    public void resetCurrentMoves(String gameId, Integer userId) {
        Game game = games.get(gameId);
        game.resetCurrentMove(userId);
    }

    public List<PlayerResponse> getPlayersStats(String gameId) {
        Game game = games.get(gameId);
        return game.getPlayerIds().stream()
                .map(game::getPlayer)
                .map(player -> new PlayerResponse(
                        player.getName(),
                        Arrays.stream(MeepleType.values())
                                .map(meepleType -> new PlayerResponse.MeepleUse(
                                                meepleType.name,
                                                player.getTimesPlaying(meepleType), // TODO: Should times used in validated Solution
                                                player.isInSpecialTeam(meepleType)
                                        )
                                ).toList()))
                .toList();
    }

    public TimerResponse getTimer(String gameId) {
        Game game = games.get(gameId);

        return new TimerResponse(game.getCurrentTimer(), game.isTimerRunning());
    }

    public void selectSolution(String gameId, Integer userId) {
        Game game = games.get(gameId);
        game.selectSolution(userId);
    }

    public List<MeepleResponse> getMeeples(String gameId, Integer userId) {
        Game game = games.get(gameId);
        return game.getMeeples(userId).entrySet()
                .stream()
                .map(meeple -> new MeepleResponse(
                        meeple.getKey().name,
                        meeple.getValue()))
                .toList();
    }
}
