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
    public void applyAction(Player player) {
        System.out.println("Player reached checkpoint " + number);
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        return false;
    }
}
