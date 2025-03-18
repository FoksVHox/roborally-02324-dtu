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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Objects;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    static public Board board = null;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        if (space.getPlayer() != null) return;

        space.setPlayer(board.getCurrentPlayer());
        board.incrementMoves();

        board.setCurrentPlayer(board.getNextPlayer());
    }

    /**
     * Starts the programming phase of the game.
     * This method sets the game phase to PROGRAMMING, assigns the first player as the current player,
     * and resets the programming fields for all players. It also assigns new random command cards.
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    /**
     * Generates a random command card from the available command set.
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * Ends the programming phase and transitions the game to the activation phase.
     * This method makes all program fields invisible, ensures the first player's program fields are visible,
     * and sets the game phase to ACTIVATION. It also resets the current player and step to the initial state.
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    /**
     * Checks if the game phase should be finished based on the board and player position.
     *
     * If the board is "<advanced>", the game finishes when a player reaches space (11,6) and has passed the last checkpoint (3).
     * If the board is "basic", the game finishes when a player reaches space (7,3) and has passed the last checkpoint (2).
     * Once the game is finished, it updates the phase to FINISHED and displays the winning message.
     */
    public void finnishGamePhase() {
        if (Objects.equals(board.boardName, "<advanced>")) {
            if (board.getSpace(11, 6).getPlayer() != null && Player.getLastCheckpoint() == 3) {
                board.setPhase(Phase.FINISHED);
                ShowWinningBoardAdvanced();
            }

        } else if (Objects.equals(board.boardName, "basic")) {
            if (board.getSpace(7, 3).getPlayer() != null && Player.getLastCheckpoint() == 2) {
                board.setPhase(Phase.FINISHED);
                ShowWinningBoardBasic();
            }
        }

    }

    /**
     * Displays a popup message declaring the winner, for each of the boards
     */
    public static void ShowWinningBoardBasic() {
        // Show popup instantly when the game is won
        JOptionPane.showMessageDialog(null, "Congratulations! " + board.getSpace(7, 3).getPlayer().getName() + " won the game!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }
    public static void ShowWinningBoardAdvanced() {
        // Show popup instantly when the game is won
        JOptionPane.showMessageDialog(null, "Congratulations! " + board.getSpace(11, 6).getPlayer().getName() + " won the game!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }




    /**
     * Makes the program field at the specified register visible for all players.
     */
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    /**
     * Hides all program fields for all players.
     */
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * Executes all player programs in sequence.
     *
     * This method disables step mode, runs the programs, and checks if the game should finish.
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
        finnishGamePhase();
    }

    /**
     * Executes the next step in the game while keeping step mode enabled.
     */
    public void executeStep() {
        board.setStepMode(true);
        executeNextStep(false);
    }

    /**
     * Continues executing programs while the game is in the ACTIVATION phase
     * and step mode is not enabled.
     *
     * If `waitingForLoR` is false, execution stops immediately (temporary fix).
     */
    private void continuePrograms() {
        do {
            executeNextStep(false);
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * Executes the next step in the player's program.
     *
     * Retrieves the current player's command card and executes it.
     * If player input is required, execution stops. Otherwise, it moves to the next player
     * or advances to the next step. When all steps are completed, conveyor belts and
     * checkpoints activate, and a new programming phase begins.
     */
    public void executeNextStep(boolean interactiveExecuted) {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    //prevents LoR from entering a permanent loop
                    if (card.command == Command.LoR) {
                        if (!interactiveExecuted) {
                            board.setPhase(Phase.PLAYER_INTERACTION);
                            return;
                        }
                    } else {
                        executeCommand(currentPlayer, command);
                    }
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        activateConveyorBelts();
                        activateCheckPoints();
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }
    /**
     * Activates all conveyor belts on the board.
     *
     * This method iterates over all spaces on the board and triggers conveyor belt actions, if present.
     */
    private void activateConveyorBelts() {
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                for (FieldAction action : space.getActions()) {
                    if (action instanceof ConveyorBelt conveyorBelt) {
                        conveyorBelt.doAction(this, space);
                    }
                }
            }
        }
    }
    /**
     * Activates all checkpoints on the board.
     *
     * This method iterates over all spaces on the board and triggers checkpoint actions, if present.
     */
    private void activateCheckPoints() {
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                for (FieldAction action : space.getActions()) {
                    if (action instanceof Checkpoint checkpoint) {
                        checkpoint.doAction(this, space);
                    }
                }
            }
        }
    }


    /**
     * Executes the given command for a specific player.
     *
     * Depending on the command type, this method performs different actions such as movement, turning, or interaction.
     * If the command requires player input (e.g., Left or Right), the game enters the PLAYER_INTERACTION phase.
     *
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                //the commands for u_turn and backward movement
                case U_TURN:
                    this.uTurn(player);
                    break;
                case BACKWARD:
                    this.moveBackward(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    /**
     *
     * @param player player movement makes forward in the direction the player is facing possible
     */
    public void moveForward(@NotNull Player player) {
        Space currentSpace = player.getSpace();
        Heading heading = player.getHeading();

        if (pushRobots(currentSpace, heading)) {
            Space nextSpace = board.getNeighbour(currentSpace, heading);
            if (nextSpace != null) {
                nextSpace.setPlayer(player);
                currentSpace.setPlayer(null);
            }
        }
    }

    /**
     *
     * @param player player movement makes moving backwards one space possible
     */
    public void moveBackward(@NotNull Player player) {
        Space currentSpace = player.getSpace();
        Heading heading = player.getHeading().opposite(); // Move in the opposite direction of current heading

        if (pushRobots(currentSpace, heading)) {
            Space nextSpace = board.getNeighbour(currentSpace, heading);
            if (nextSpace != null) {
                nextSpace.setPlayer(player);
                currentSpace.setPlayer(null);
            }
        }
    }


    /**
     *
     * @param player player movement makes moving two spaces at a time possible
     */
    public void fastForward(@NotNull Player player) {
        //move two spaces
        moveForward(player);
        moveForward(player);

    }

    /**
     *
     * @param player player movement makes turning right possible
     */
    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().next());

    }

    /**
     *
     * @param player player movement makes turning left possible
     */
    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());

    }

    /**
     *
     * @param player player movement, interactive card
     * @param direction the direction the player is heading towards
     */
    public void lor(@NotNull Player player, String direction) {
        if (direction.equals("left")) {
            turnLeft(player);
        } else if (direction.equals("right")) {
            turnRight(player);
        }

        board.setPhase(Phase.ACTIVATION);
        executeNextStep(true);
        if (board.getPhase() == Phase.ACTIVATION) {
            continuePrograms();
        }

    }

    /**
     *
     * @param player controls player movement, makes 180 degree turns possible
     */
    public void uTurn(Player player) {
        player.setHeading(player.getHeading().next().next()); // Rotate 180 degrees
    }

    /**
     *
     * @param space the spaces of the board
     * @param direction the direction that the player is heading towards
     * @return should return false if movement is blocked in some form.
     */
    private boolean pushRobots(Space space, Heading direction) {
        Space nextSpace = board.getNeighbour(space, direction);
        if (nextSpace == null || nextSpace.getWalls().contains(direction)) {
            return false; // Movement blocked by a wall
        }

        Player nextPlayer = nextSpace.getPlayer();
        if (nextPlayer != null) {
            boolean pushed = pushRobots(nextSpace, direction);
            if (!pushed) {
                return false; // Chain movement blocked
            }
        }

        nextSpace.setPlayer(space.getPlayer());
        space.setPlayer(null);
        return true;
    }



    /**
     * A method called when no corresponding controller operation is implemented yet.
     * This should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
