package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;



    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null, "Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    /**
     * Test for Assignment V1 (can be deleted later once V1 was shown to the teacher)
     */
    @Test
    void testV1() {
        Board board = gameController.board;

        Player player = board.getCurrentPlayer();
        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        assertEquals(player, board.getSpace(0, 4).getPlayer(), "Player " + player.getName() + " should be on Space (0,4)!");
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    public void testFastForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Space currentSpace = current.getSpace();
        gameController.fastForward(current);
        assertNotEquals(currentSpace, current.getSpace(), "Player should have moved two spaces forward.");
    }

    @Test
    void testMoveBackward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        gameController.board.getSpace(3, 3).setPlayer(current);
        current.setHeading(Heading.NORTH);

        gameController.moveBackward(current);

        assertEquals(current, gameController.board.getSpace(3, 4).getPlayer());
        Assertions.assertNull(gameController.board.getSpace(3, 3).getPlayer());
    }

    @Test
    void moveForwardNorth() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        board.getSpace(0, 1).setPlayer(current);
        current.setHeading(Heading.NORTH);

        gameController.moveForward(current);

        assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player " + current.getName() + " should be at (0,0)!");
        assertEquals(Heading.NORTH, current.getHeading(), "Player should still be facing NORTH!");
        Assertions.assertNull(board.getSpace(0, 1).getPlayer(), "Space (0,1) should be empty!");
    }

    @Test
    void turnRight() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        board.getSpace(0, 1).setPlayer(current);
        current.setHeading(Heading.NORTH);

        gameController.turnRight(current);

        assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should be at (0,1)!");
        assertEquals(Heading.EAST, current.getHeading(), "Player should still be facing EAST!");
    }
    @Test
    void turnLeft() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        board.getSpace(0, 1).setPlayer(current);
        current.setHeading(Heading.NORTH);

        gameController.turnLeft(current);

        assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should be at (0,1)!");
        assertEquals(Heading.WEST, current.getHeading(), "Player should still be facing WEST!");
    }

    @Test
    void testUTurn() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        board.getSpace(0, 1).setPlayer(current);
        current.setHeading(Heading.NORTH);

        gameController.uTurn(current);

        assertEquals(Heading.SOUTH, current.getHeading());
    }

    @Test
    public void testLor() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Heading originalHeading = current.getHeading();
        gameController.lor(current, "left");
        assertEquals(originalHeading.prev(), current.getHeading(), "Player should have turned left during LoR.");
    }

    @Test
    void turnRightMoveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        board.getSpace(0, 1).setPlayer(current);
        current.setHeading(Heading.EAST);

        gameController.turnRight(current);
        gameController.moveForward(current);

        assertEquals(current, board.getSpace(0, 2).getPlayer(), "Player " + current.getName() + " should be at (0,2)!");
        assertEquals(Heading.SOUTH, current.getHeading(), "Player should still be facing SOUTH!");
        Assertions.assertNull(board.getSpace(0, 1).getPlayer(), "Space (0,1) should be empty!");

    }



    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
    }

    @Test
    void testStartProgrammingPhase() {
        Board board = gameController.board;
        gameController.startProgrammingPhase();

        assertEquals(Phase.PROGRAMMING, board.getPhase(), "Game phase should be PROGRAMMING!");
        assertEquals(board.getPlayer(0), board.getCurrentPlayer(), "First player should be current player!");
    }

    @Test
    void testFinishProgrammingPhase() {
        Board board = gameController.board;
        gameController.finishProgrammingPhase();

        assertEquals(Phase.ACTIVATION, board.getPhase(), "Game phase should be ACTIVATION!");
        assertEquals(board.getPlayer(0), board.getCurrentPlayer(), "First player should be current player!");
    }


}