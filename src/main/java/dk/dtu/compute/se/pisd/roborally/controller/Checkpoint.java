package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;


/**
 * This class represents a checkpoint on a space.
 * Players have to reach the checkpoints in s specific order
 *
 * @author Signe Nielsen (github tag: Stardust_Blooms), s245413@student.dtu.dk
 */


public class Checkpoint extends FieldAction {
    private final int number;


    /**
     * Creates a checkpoint with the assigned number.
     * @param number is the checkpoint number
     */

    public Checkpoint(int number) {
        this.number = number;
    }

    /**
     *
     * @return the checkpoint number for the checkpoint.
     */

    public int getNumber() {
        return number;
    }

    /**
     *
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return false as is does not
     */

    @Override
    public boolean doAction(GameController gameController, @NotNull Space space) {
        Player player = space.getPlayer();
        if (player != null) {
            player.passCheckpoint(number); // checks if
        }
        return false;
    }
}
