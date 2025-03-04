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
        System.out.println("Creating board " + name);
        Board board;
        if (Objects.equals(name, "advanced")) {
                board = createAdvancedBoard();
        } else {
            board = createBasicBoard();
        }
        System.out.println(board.width);
        System.out.println(board.height);

        // add some walls, actions and checkpoints to some spaces
        Space space = board.getSpace(0, 0);
        space.getWalls().add(Heading.SOUTH);
        ConveyorBelt action = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(1, 0);
        space.getWalls().add(Heading.NORTH);
        action = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(1, 1);
        space.getWalls().add(Heading.WEST);
        action = new ConveyorBelt();
        action.setHeading(Heading.NORTH);
        space.getActions().add(action);

        space = board.getSpace(5, 5);
        space.getWalls().add(Heading.SOUTH);
        action = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(6, 5);
        action = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        //Walls
        space = board.getSpace(7, 5);
        //action  = new ;
        space.getActions().add(action);

        return board;
    }

    private Board createAdvancedBoard() {
        return new Board(15,8, "<advanced>");
    }

    private Board createBasicBoard() {
        return new Board(8,8, "basic");
    }
}
