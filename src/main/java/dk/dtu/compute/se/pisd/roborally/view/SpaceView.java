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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 40; // 60; // 75;
    final public static int SPACE_WIDTH = 40;  // 60; // 75;

    public final Space space;



    public SpaceView(@NotNull Space space) {
        this.space = space;


        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }





    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear();

            for (FieldAction action : space.getActions()) {
                if (action instanceof ConveyorBelt conveyorBelt) {
                    Polygon arrow = new Polygon(-15.0, 8.0, 15.0, 8.0, 0.0, -20.0);
                    arrow.setFill(Color.LIGHTGRAY);
                    arrow.setOpacity(1); // Slightly transparent

                    // Rotate based on conveyor direction
                    switch (conveyorBelt.getHeading()) {
                        case NORTH -> arrow.setRotate(0);
                        case EAST -> arrow.setRotate(90);
                        case SOUTH -> arrow.setRotate(180);
                        case WEST -> arrow.setRotate(270);
                    }
                    this.getChildren().add(arrow);
                }
            }

            if (space.getWalls().contains(Heading.NORTH)) {
                Rectangle northWall = new Rectangle(SPACE_WIDTH, 5, Color.RED);
                StackPane.setAlignment(northWall, Pos.TOP_CENTER);
                this.getChildren().add(northWall);
            }
            if (space.getWalls().contains(Heading.SOUTH)) {
                Rectangle southWall = new Rectangle(SPACE_WIDTH, 5, Color.RED);
                StackPane.setAlignment(southWall, Pos.BOTTOM_CENTER);
                this.getChildren().add(southWall);
            }
            if (space.getWalls().contains(Heading.WEST)) {
                Rectangle westWall = new Rectangle(5, SPACE_HEIGHT, Color.RED);
                StackPane.setAlignment(westWall, Pos.CENTER_LEFT);
                this.getChildren().add(westWall);
            }
            if (space.getWalls().contains(Heading.EAST)) {
                Rectangle eastWall = new Rectangle(5, SPACE_HEIGHT, Color.RED);
                StackPane.setAlignment(eastWall, Pos.CENTER_RIGHT);
                this.getChildren().add(eastWall);
            }

            Checkpoint checkpoint = space.getCheckpoint();
            if (checkpoint != null) {
                Circle checkpointCircle = new Circle(15, Color.YELLOW); // Yellow circle
                Label checkpointLabel = new Label(String.valueOf(checkpoint.getNumber())); // Checkpoint number
                checkpointLabel.setTextFill(Color.BLACK);
                checkpointLabel.setStyle("-fx-font-weight: bold;");

                StackPane checkpointPane = new StackPane(checkpointCircle, checkpointLabel);
                this.getChildren().add(checkpointPane);
            }



        }


            // XXX A3: drawing walls and action on the space (could be done
            //         here); it would be even better if fixed things on
            //         spaces  are only drawn once (and not on every update)

            updatePlayer();
        }
    }

