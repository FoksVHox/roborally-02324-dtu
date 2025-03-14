package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Checkpoint extends FieldAction {
    private final int number;

    public Checkpoint(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    //temp

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Player player = space.getPlayer();
        System.out.println("Player reached checkpoint " + number);
        return false;
    }
}
