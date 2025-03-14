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

import java.util.Objects;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        if(space.getPlayer() != null) return;

        space.setPlayer(board.getCurrentPlayer());
        board.incrementMoves();

        board.setCurrentPlayer(board.getNextPlayer());
    }

    // XXX V2
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

    // XXX V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX V2
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    public void finnishGamePhase() {
        if (Objects.equals(board.boardName, "advanced")) {
            if (board.getSpace(11, 6).getPlayer() != null) {
                board.setPhase(Phase.FINISHED);

                System.out.println(board.getSpace(11, 6).getPlayer().getName());


            }
        }
        else if (Objects.equals(board.boardName, "basic")) {
            if (board.getSpace(7, 3).getPlayer() != null) {
                board.setPhase(Phase.FINISHED);
                System.out.println(board.getSpace(7, 3).getPlayer().getName());
            }
        }



    }
    public static void ShowWinningBoard() {



    }

    // XXX V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
        finnishGamePhase();
    }

    // XXX V2
    public void executeStep() {
        board.setStepMode(true);
        executeNextStep();
    }

    // XXX V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX V2
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
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


    // XXX V2
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
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    // TODO V2
    public void moveForward(@NotNull Player player) {
        player = board.getCurrentPlayer();
        Space currentSpace = player.getSpace();
        Heading heading = player.getHeading();

        Space nextSpace = board.getNeighbour(currentSpace, heading);

        // Check if the next space is valid and not blocked by a wall
        if (nextSpace != null && nextSpace.getPlayer() == null) {
            // Move the player
            nextSpace.setPlayer(player);
            currentSpace.setPlayer(null);
        }
    }

    public void moveBackward(@NotNull Player player) {
        player = board.getCurrentPlayer(); // Ensure we are working with the current player
        Space currentSpace = player.getSpace();
        Heading heading = player.getHeading().opposite(); // Move in the opposite direction of current heading

        Space nextSpace = board.getNeighbour(currentSpace, heading);

        // Check if the next space is valid and empty (no other player there)
        if (nextSpace != null && nextSpace.getPlayer() == null) {
            // Move the player
            nextSpace.setPlayer(player);
            currentSpace.setPlayer(null);
        }
    }



    // TODO V2
    public void fastForward(@NotNull Player player) {
        //move two spaces
        moveForward(player);
        moveForward(player);

    }

    // TODO V2
    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().next());

    }

    // TODO V2
    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());

    }
    public void uTurn(Player player) {
        player.setHeading(player.getHeading().next().next()); // Rotate 180 degrees
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
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
