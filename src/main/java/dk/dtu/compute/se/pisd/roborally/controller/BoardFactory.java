package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.Objects;

/**
 * A factory for creating boards. The factory itself is implemented as a singleton.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
// XXX A3: might be used for creating a first slightly more interesting board.
public class BoardFactory {

    /**
     * The single instance of this class, which is lazily instantiated on demand.
     */
    static private BoardFactory instance = null;

    /**
     * Constructor for BoardFactory. It is private in order to make the factory a singleton.
     */
    private BoardFactory() {
    }

    /**
     * Returns the single instance of this factory. The instance is lazily
     * instantiated when requested for the first time.
     *
     * @return the single instance of the BoardFactory
     */
    public static BoardFactory getInstance() {
        if (instance == null) {
            instance = new BoardFactory();
        }
        return instance;
    }

    /**
     * Creates a new board of given name of a board, which indicates
     * which type of board should be created. For now the name is ignored.
     *
     * @param name the given name board
     * @return the new board corresponding to that name
     */
    public Board createBoard(String name) {
        Board board;
        if (Objects.equals(name, "advanced")) {
                board = createAdvancedBoard();
        } else {
            board = createBasicBoard();
        }

        // add some walls, actions and checkpoints to some spaces
        Space space = board.getSpace(0,0);
        space.getWalls().add(Heading.SOUTH);
        ConveyorBelt action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(1,0);
        space.getWalls().add(Heading.NORTH);
        action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(1,1);
        space.getWalls().add(Heading.WEST);
        action  = new ConveyorBelt();
        action.setHeading(Heading.NORTH);
        space.getActions().add(action);

        space = board.getSpace(5,5);
        space.getWalls().add(Heading.SOUTH);
        action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(6,5);
        action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        //Checkpoints
        space = board.getSpace(5, 1);
        Checkpoint checkpoint1 = new Checkpoint(1);
        space.getActions().add(checkpoint1);

        space = board.getSpace(7, 3);
        Checkpoint checkpoint2 = new Checkpoint(2);
        space.getActions().add(checkpoint2);

        //allows for a checkpoint "out of bounds" when using the basic board
            space = board.getSpace(11, 6);
            if (space != null) { // Ensure the space exists before using it
                Checkpoint checkpoint3 = new Checkpoint(3);
                space.getActions().add(checkpoint3);
            }
        return board;
    }

    /**
     * The following two methods creates two types of boards of different sizes.
     * @return returns the board types.
     */
    private Board createAdvancedBoard() {
        return new Board(15,8, "<advanced>");
    }

    private Board createBasicBoard() {
        return new Board(8,8, "basic");
    }
}
