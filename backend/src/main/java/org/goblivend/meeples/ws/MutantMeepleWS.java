package org.goblivend.meeples.ws;

import jakarta.inject.Inject;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.goblivend.meeples.service.GameService;
import org.goblivend.meeples.ws.response.*;

import jakarta.ws.rs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Path("/mutant-meeple")
@Tag(name = "MutantMeeple", description = "Mutant Meeple Game API")
public class MutantMeepleWS {
    // TODO: Check if the player exists in every endpoints

    @Inject
    GameService gameService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MutantMeepleWS.class);

    @GET
    @Path("/hello")
    @Operation(summary = "Hello World", description = "Simple Hello World endpoint")
    public String hello() {
        return "Hello World!";
    }

    @GET
    @Path("/games")
    public List<String> getGames() {
        LOGGER.info("Getting games");
        return gameService.getGames();
    }

    @POST
    @Path("/game/{gameId}")
    public String createGame(@PathParam("gameId") String gameId) {
        LOGGER.info("Creating game {}", gameId);
        return gameService.createGame(gameId);
    }

    @POST
    @Path("/game/{gameId}/join")
    public Integer joinGame(@PathParam("gameId") String gameId,
                            @QueryParam("name") String userName) {
        if (!gameService.gameExists(gameId)) {
            throw new NotFoundException("Game " + gameId + " not found");
        }

        LOGGER.info("Joining game {} with user {}", gameId, userName);
        var uuid = gameService.joinGame(gameId, userName);
        LOGGER.info("User {} joined game {} with id {}", userName, gameId, uuid);
        return uuid;
    }

    @GET
    @Path("/game/{gameId}/board")
    public BoardResponse getGame(@PathParam("gameId") String gameId) {
        if (!gameService.gameExists(gameId)) {
            throw new NotFoundException("Game " + gameId + " not found");
        }

        LOGGER.info("Getting game {}", gameId);
        return gameService.getGame(gameId);
    }

    @GET
    @Path("/game/{gameId}/moves")
    public MoveResponse getMovesFromMeepleAt(@PathParam("gameId") String gameId,
                                             @QueryParam("userId") Integer user,
                                             @QueryParam("x") Integer x,
                                             @QueryParam("y") Integer y) {
        if (!gameService.gameExists(gameId)) {
            throw new NotFoundException("Game " + gameId + " not found");
        }

        LOGGER.info("Getting moves for user {} at position ({}, {}) in game {}", user, x, y, gameId);
        return gameService.getMovesFromMeeple(gameId, user, x, y);
    }

    @POST
    @Path("/game/{gameId}/move")
    public void moveMeeple(@PathParam("gameId") String gameId,
                           @QueryParam("userId") Integer userId,
                           @QueryParam("srcX") Integer srcX,
                           @QueryParam("srcY") Integer srcY,
                           @QueryParam("dstX") Integer dstX,
                           @QueryParam("dstY") Integer dstY) {
        if (!gameService.gameExists(gameId)) {
            throw new NotFoundException("Game " + gameId + " not found");
        }

        LOGGER.info("Moving meeple for user {} from ({}, {}) to ({}, {}) in game {}", userId, srcX, srcY, dstX, dstY, gameId);
        gameService.moveMeeple(gameId, userId, srcX, srcY, dstX, dstY);
    }

    @DELETE
    @Path("/game/{gameId}/move")
    public BoardResponse resetMoves(@PathParam("gameId") String gameId,
                                    @QueryParam("userId") Integer userId) {
        if (!gameService.gameExists(gameId)) {
            throw new NotFoundException("Game " + gameId + " not found");
        }

        LOGGER.info("Resetting moves for user {} in game {}", userId, gameId);
        gameService.resetCurrentMoves(gameId, userId);
        return gameService.getGame(gameId);
    }

    @GET
    @Path("/game/{gameId}/timer")
    public TimerResponse getTimer(@PathParam("gameId") String gameId) {
        if (!gameService.gameExists(gameId)) {
            throw new NotFoundException("Game " + gameId + " not found");
        }

//        LOGGER.info("Getting timer for game {}", gameId);
        var response = gameService.getTimer(gameId); // TODO: Implement Timer Start
//        LOGGER.info("Timer for game {} is {}", gameId, response);
        return response;
    }

    @PUT
    @Path("/game/{gameId}/move/select")
    public void selectSolution(@PathParam("gameId") String gameId,
                               @QueryParam("userId") Integer userId) {
        if (!gameService.gameExists(gameId)) {
            throw new NotFoundException("Game " + gameId + " not found");
        }

        LOGGER.info("Selecting solution for user {} in game {}", userId, gameId);
        gameService.selectSolution(gameId, userId);
    }

    @GET
    @Path("/game/{gameId}/players")
    public List<PlayerResponse> getPlayersStats(@PathParam("gameId") String gameId) {
        if (!gameService.gameExists(gameId)) {
            throw new NotFoundException("Game " + gameId + " not found");
        }

        LOGGER.info("Getting player stats for game {}", gameId);
        return gameService.getPlayersStats(gameId); // TODO: Implement Player Stats
    }

    @GET
    @Path("/game/{gameId}/meeples")
    public List<MeepleResponse> getMeeples(@PathParam("gameId") String gameId,
                                           @QueryParam("userId") Integer userId) {
        if (!gameService.gameExists(gameId)) {
            throw new NotFoundException("Game " + gameId + " not found");
        }

        LOGGER.info("Getting meeples for game {}", gameId);
        return gameService.getMeeples(gameId, userId);
    }
}
