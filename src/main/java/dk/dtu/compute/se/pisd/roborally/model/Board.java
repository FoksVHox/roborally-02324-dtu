/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * This represents a RoboRally game board. Which gives access to
 * all the information of current state of the games. Note that
 * the terms board and game are used almost interchangeably.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    private int moves = 0;

    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
    }
    /**
     * Constructs a Board with the specified width and height.
     * Defaults to using "defaultboard" as the board name.
     *
     * @param width  The width of the board.
     * @param height The height of the board.
     */
    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }
    /**
     * Gets the unique game ID.
     *
     */
    public Integer getGameId() {
        return gameId;
    }
    /**
     * Sets the game ID if it has not been assigned before.
     *
     */
    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }
    /**
     * Retrieves the space at the specified coordinates.
     */
    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }
    /**
     * Gets the number of players on the board.
     */
    public int getPlayersNumber() {
        return players.size();
    }
    /**
     * Adds a player to the board if they are not already present.
     *
     */
    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }
    /**
     * Retrieves a player by index.
     *
     */
    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * Return the next player. If the current player is the last player in the queue, we should take the first "player again
     * @return Player
     */
    public Player getNextPlayer() {
        int currentIndex = this.getPlayerNumber(this.getCurrentPlayer());
        int totalPlayers = this.getPlayersNumber();
        int nextIndex = (currentIndex + 1) % totalPlayers;
        return this.getPlayer(nextIndex);
    }


    /**
     * Gets the current player.
     */

    public Player getCurrentPlayer() {
        return current;
    }
    /**
     * Sets the current player if they are in the game.
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }
    /**
     * Gets the current game phase.
     *
     */
    public Phase getPhase() {
        return phase;
    }
    /**
     * Sets the game phase and notifies of changes.
     *
     */
    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }
    /**
     * Gets the total number of moves made.
     *
     */
    public int getMoves() { return moves; }

    public void setMoves(int moves) {
        this.moves = moves;
        notifyChange();
    }
    /**
     * Increments the move count by one and notifies of changes.
     */
    public void incrementMoves() {
        moves++;
        notifyChange();
    }

    public void decrementMoves() {
        moves--;
        notifyChange();
    }
    /**
     * Gets the current step number.
     *
     */
    public int getStep() {
        return step;
    }
    /**
     * Sets the current step and notifies of changes.
     *
     */
    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }
    /**
     * Checks if the game is in step mode.
     *
     */
    public boolean isStepMode() {
        return stepMode;
    }
    /**
     * Enables or disables step mode and notifies of changes.
     *
     */
    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }
    /**
     * Gets the index of a given player in the game.
     *
     */
    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        // TODO A3: This implementation needs to be adjusted so that walls on
        //          spaces (and maybe other obstacles) are taken into account
        //          (see above JavaDoc comment for this method).
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }

    public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        // TODO V2: changed the status so that it shows the phase, the current player, and the current register
        return "Player = " + getCurrentPlayer().getName() +". Moves = " + getMoves();
    }

    public String getCheckpointsMessage() {
        int checkpoints = getCurrentPlayer().getCollectedCheckpoints();  // Get the current player's collected checkpoints
        return "Player = " + getCurrentPlayer().getName() + " Checkpoints = " + checkpoints;
    }



}
