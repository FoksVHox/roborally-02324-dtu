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
    @Test
    void testFastForwardMultipleMoves() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Space currentSpace = current.getSpace();

        // Fast-forward  player
        gameController.fastForward(current);

        // Check if the player has moved two spaces forward (assuming no walls or collisions)
        assertNotEquals(currentSpace, current.getSpace(), "Player should have moved two spaces forward.");
    }

    @Test
    void testPlayerPushAnotherPlayer() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        player1.setSpace(board.getSpace(1, 1)); // Player 1 at (1,1)
        player2.setSpace(board.getSpace(1, 2)); // Player 2 at (1,2)

        gameController.moveForward(player1); // Push Player 1 into Player 2

        assertEquals(player1, board.getSpace(1, 2).getPlayer(), "Player 1 should not have moved into Player 2.");
    }


}

class GameController2 {
    private GameController gameController;
    private Board basicBoard;
    private Board advancedBoard;
    private Player player;

    @BeforeEach
    void setUp() {
        basicBoard = new Board(8, 8);
        basicBoard.boardName = "basic";

        advancedBoard = new Board(15, 8);
        advancedBoard.boardName = "advanced";
    }

    @AfterEach
    void tearDown() {
        gameController = null;
        player = null;
    }



    @Test
    void testBasicBoardCheckpointProgression() {
        gameController = new GameController(basicBoard);
        Board board = gameController.board;

        player = new Player(board, null, "TestPlayer");
        board.addPlayer(player);
        player.setSpace(board.getSpace(2, 3)); // Player starts near first checkpoint

        // Simulate player passing checkpoints
        player.passCheckpoint(1);
        assertEquals(1, player.getCollectedCheckpoints(), "Player should have passed checkpoint 1");

        player.passCheckpoint(2);
        assertEquals(2, player.getCollectedCheckpoints(), "Player should have passed checkpoint 2");

        // Attempt to skip a checkpoint
        player.passCheckpoint(4);
        assertEquals(2, player.getCollectedCheckpoints(), "Player should NOT be able to skip checkpoint 3");

        // Pass checkpoint 3 and then checkpoint 4
        player.passCheckpoint(3);
        assertEquals(3, player.getCollectedCheckpoints(), "Player should have passed checkpoint 3");

        player.passCheckpoint(4);
        assertEquals(4, player.getCollectedCheckpoints(), "Player should have passed checkpoint 4");
    }



    @Test
    void testAdvancedBoardWinningCondition() {
        gameController = new GameController(advancedBoard);
        Board board = gameController.board;

        player = new Player(board, null, "TestPlayer");
        board.addPlayer(player);
        player.setSpace(board.getSpace(11, 6)); // Assume this is near a checkpoint

        // Simulate passing checkpoints in order
        player.passCheckpoint(1);
        player.passCheckpoint(2);
        player.passCheckpoint(3);

        assertEquals(3, player.getCollectedCheckpoints(), "Player should have collected 3 checkpoints");

        // Winning condition: Player reaches the final checkpoint
        player.passCheckpoint(4);
        assertEquals(4, player.getCollectedCheckpoints(), "Player should have passed the last checkpoint");

        gameController.finnishGamePhase();

        assertEquals(Phase.INITIALISATION, board.getPhase(), "Game should be finished when the last checkpoint is passed");
    }

}