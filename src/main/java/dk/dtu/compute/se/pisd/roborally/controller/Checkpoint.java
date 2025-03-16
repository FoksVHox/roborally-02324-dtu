package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;


/**
 * This class represents a checkpoint on a space.
 *
 * @author Signe Nielsen (github tag: Stardust_Blooms),
 * @param
 */


public class Checkpoint extends FieldAction {
    private final int number;

    public Checkpoint(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }



    @Override
    public boolean doAction(GameController gameController, @NotNull Space space) {
        Player player = space.getPlayer();
        if (player != null) {
            player.passCheckpoint(number); // checks if
        }
        return false;
    }
}
