package dungeoncrawler;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * Class that renders the GUI for each Room
 * @author Ishaan Guha
 * @version  1.0
 */
public class RoomRenderer {

    /**
     * draws the room, adding all the doors, obstacles, and treasures
     * @param scene The room's scene
     * @param room The room to draw
     * @param player the current player sprite
     * @return a pane with all the room's doors and obstacles and treasures
     */
    public static Pane drawRoom (Scene scene, Room room, ImageView player) {
        player.setX(getPx(room.getStartX()));
        player.setY(getPx(room.getHeight() - room.getStartY() - GameSettings.PLAYER_HEIGHT));
        Pane root = new Pane();
        Pane main = new Pane();
        main.getChildren().addAll(root);

        root.setMaxHeight(getPx(room.getHeight()));
        root.setPrefHeight(getPx(room.getHeight()));
        root.setMinHeight(getPx(room.getHeight()));
        root.setMaxWidth(getPx(room.getWidth()));
        root.setPrefWidth(getPx(room.getWidth()));
        root.setMinWidth(getPx(room.getWidth()));

        root.setStyle("-fx-border-color: blue");

        main.setMaxHeight(getPx(room.getHeight()) * 1.36882129);
        main.setPrefHeight(getPx(room.getHeight()) * 1.36882129);
        main.setMinHeight(getPx(room.getHeight()) * 1.36882129);
        main.setMaxWidth(getPx(room.getWidth()) * 1.11111111);
        main.setPrefWidth(getPx(room.getWidth()) * 1.11111111);
        main.setMinWidth(getPx(room.getWidth()) * 1.11111111);

        main.getStyleClass().add("rootPane");

        //shift room so that it's not touching the edge of the window
        root.setTranslateX(getPx(room.getWidth()) * 0.0572916667);
        root.setTranslateY(getPx(room.getHeight()) * 0.25095057);

        main.setStyle("-fx-padding: 50px");

        if (room.getObstacles() != null) {
            for (Obstacle obstacle : room.getObstacles()) {
                if (obstacle == null) {
                    continue;
                }
                if (obstacle.getType().name().equals("KEY")) {
                    Image image = null;
                    ImageView imageView;
                    imageView = new ImageView("key.png");
                    imageView.setX(getPx(obstacle.getX()));
                    imageView.setY(getPx(room.getHeight() - obstacle.getY() - obstacle.getHeight()));
                    imageView.setFitWidth(getPx(obstacle.getWidth()));
                    imageView.setFitHeight(getPx(obstacle.getHeight()));
                    root.getChildren().add(imageView);
                } else {
                    Rectangle r = new Rectangle(getPx(obstacle.getX()),
                            getPx(room.getHeight() - obstacle.getY() - obstacle.getHeight()),
                            getPx(obstacle.getWidth()), getPx(obstacle.getHeight()));
                    root.getChildren().add(r);
                }

            }
        }
        if (room.getTopDoor() != null) {
            Image image = null;
            ImageView imageView;
            imageView = new ImageView("dungeon1-topdoor.png");
            imageView.setX(getPx(room.getTopDoor().getX()));
            imageView.setY(getPx(room.getHeight() - room.getTopDoor().getY()
                    - room.getTopDoor().getHeight()));
            imageView.setFitWidth(getPx(room.getTopDoor().getWidth()));
            imageView.setFitHeight(getPx(room.getTopDoor().getHeight()));
            root.getChildren().add(imageView);
        }
        if (room.getRightDoor() != null) {
            Image image = null;
            ImageView imageView;
            imageView = new ImageView("dungeon1-rightdoor.png");
            imageView.setX(getPx(room.getRightDoor().getX()));
            imageView.setY(getPx(room.getHeight() - room.getRightDoor().getY()
                    - room.getRightDoor().getHeight()));
            imageView.setFitWidth(getPx(room.getRightDoor().getWidth()));
            imageView.setFitHeight(getPx(room.getRightDoor().getHeight()));
            root.getChildren().add(imageView);
        }
        if (room.getBottomDoor() != null) {
            Image image = null;
            ImageView imageView;
            imageView = new ImageView("dungeon1-bottomdoor.png");
            imageView.setX(getPx(room.getBottomDoor().getX()));
            imageView.setY(getPx(room.getHeight() - room.getBottomDoor().getY()
                    - room.getBottomDoor().getHeight()));
            imageView.setFitWidth(getPx(room.getBottomDoor().getWidth()));
            imageView.setFitHeight(getPx(room.getBottomDoor().getHeight()));
            root.getChildren().add(imageView);
        }
        if (room.getLeftDoor() != null) {
            Image image = null;
            ImageView imageView;
            imageView = new ImageView("dungeon1-leftdoor.png");
            imageView.setX(getPx(room.getLeftDoor().getX()));
            imageView.setY(getPx(room.getHeight() - room.getLeftDoor().getY()
                    - room.getLeftDoor().getHeight()));
            imageView.setFitWidth(getPx(room.getLeftDoor().getWidth()));
            imageView.setFitHeight(getPx(room.getLeftDoor().getHeight()));
            root.getChildren().add(imageView);
        }
        scene.getStylesheets().add(room.getType().name() + ".css");
        root.getChildren().add(player);

        return main;
    }

    /**
     * Converting coordinates from game units to pixels
     * @param coord the coordinate to convert
     * @return the converted coordinate
     */
    public static double getPx(double coord) {
        return (coord * GameSettings.PPU);
    }
}
