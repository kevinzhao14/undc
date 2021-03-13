package dungeoncrawler;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;



public class RoomRenderer {
    public static Pane drawRoom(Scene scene, Room room, ImageView player) {
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

        main.setStyle("-fx-padding: 50px");

        if (room.getObstacles() != null) {
            for (Obstacle obstacle : room.getObstacles()) {
                if (obstacle == null) {
                    continue;
                }
                Rectangle r = new Rectangle(getPx(obstacle.getX()), getPx(room.getHeight() - obstacle.getY() - obstacle.getHeight()), getPx(obstacle.getWidth()), getPx(obstacle.getHeight()));
                root.getChildren().add(r);
            }
        }
        if (room.getTopDoor() != null) {
            Rectangle r = new Rectangle(getPx(room.getTopDoor().getX()), getPx(room.getHeight() - room.getTopDoor().getY() - room.getTopDoor().getHeight()), getPx(room.getTopDoor().getWidth()), getPx(room.getTopDoor().getHeight()));
            root.getChildren().add(r);
        }
        if (room.getRightDoor() != null) {
            Rectangle r = new Rectangle(getPx(room.getRightDoor().getX()), getPx(room.getHeight() - room.getRightDoor().getY() - room.getRightDoor().getHeight()), getPx(room.getRightDoor().getWidth()), getPx(room.getRightDoor().getHeight()));
            root.getChildren().add(r);
        }
        if (room.getBottomDoor() != null) {
            Rectangle r = new Rectangle(getPx(room.getBottomDoor().getX()), getPx(room.getHeight() -room.getBottomDoor().getY() - room.getBottomDoor().getHeight()), getPx(room.getBottomDoor().getWidth()), getPx(room.getBottomDoor().getHeight()));
            root.getChildren().add(r);
        }
        if (room.getLeftDoor() != null) {
            Rectangle r = new Rectangle(getPx(room.getLeftDoor().getX()), getPx(room.getHeight() - room.getLeftDoor().getY() - room.getLeftDoor().getHeight()), getPx(room.getLeftDoor().getWidth()), getPx(room.getLeftDoor().getHeight()));
            root.getChildren().add(r);
        }
        scene.getStylesheets().add(room.getType().name() + ".css");
        root.getChildren().add(player);

        return main;
    }

    public static double getPx(double coord) {
        return (coord * GameSettings.PPU);
    }
}
