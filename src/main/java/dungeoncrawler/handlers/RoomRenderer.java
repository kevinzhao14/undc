package dungeoncrawler.handlers;

import dungeoncrawler.objects.Door;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.Obstacle;
import dungeoncrawler.objects.Room;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;



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
    public static Pane drawRoom(Scene scene, Room room, ImageView player) {
        Pane root = new Pane();
        Pane main = new Pane();
        Pane bgPane = new Pane();
        main.getChildren().addAll(root);

        root.setMaxHeight(getPx(room.getHeight()));
        root.setPrefHeight(getPx(room.getHeight()));
        root.setMinHeight(getPx(room.getHeight()));
        root.setMaxWidth(getPx(room.getWidth()));
        root.setPrefWidth(getPx(room.getWidth()));
        root.setMinWidth(getPx(room.getWidth()));

        //holds the dungeon image
        double roomHeight = Math.round(getPx(room.getHeight()) * 1.36363636);
        double roomWidth = Math.round(getPx(room.getWidth()) * 1.11111111);
        main.setMaxHeight(roomHeight);
        main.setPrefHeight(roomHeight);
        main.setMinHeight(roomHeight);
        main.setMaxWidth(roomWidth);
        main.setPrefWidth(roomWidth);
        main.setMinWidth(roomWidth);

        main.getStyleClass().add("rootPane");

        //shift game rectangle so that it's aligned with the background image
        root.setTranslateX(Math.round(getPx(room.getWidth()) * 0.0555555556));
        root.setTranslateY(Math.round(getPx(room.getHeight()) * 0.23863636363));

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
                    imageView.setY(getPx(room.getHeight() - obstacle.getY()
                            - obstacle.getHeight()));
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
        if (room.getMonsters() != null) {
            for (Monster monster : room.getMonsters()) {
               if (monster != null) {
                   Rectangle m = new Rectangle(getPx(monster.getPosX()), getPx(room.getHeight() - monster.getPosY() - monster.getHeight()), getPx(monster.getWidth()), getPx(monster.getHeight()));
               }
            }
        }
        if (room.getTopDoor() != null) {
            showDoor(room, root, room.getTopDoor(), "dungeon1-topdoor.png");
        }
        if (room.getRightDoor() != null) {
            showDoor(room, root, room.getRightDoor(), "dungeon1-rightdoor.png");
        }
        if (room.getBottomDoor() != null) {
            showDoor(room, root, room.getBottomDoor(), "dungeon1-bottomdoor.png");
        }
        if (room.getLeftDoor() != null) {
            showDoor(room, root, room.getLeftDoor(), "dungeon1-leftdoor.png");
        }
        scene.getStylesheets().add(room.getType().name() + ".css");
        root.getChildren().add(player);

        return main;
    }

    public static void showDoor(Room room, Pane root, Door door, String doorPNG) {
        ImageView imageView = new ImageView(doorPNG);
        imageView.setX(getPx(door.getX()) - 1);
        imageView.setY(getPx(room.getHeight() - door.getY()
                - door.getHeight()));
        imageView.setFitWidth(getPx(door.getWidth()));
        imageView.setFitHeight(getPx(door.getHeight()));
        root.getChildren().add(imageView);
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
