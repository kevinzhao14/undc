package dungeoncrawler;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;

public class RoomRenderer {
    public static Scene drawRoom(Room room) {
        Group root = new Group();
        Scene scene = new Scene(root, room.getRoomWidth(), room.getRoomHeight());
        for (Obstacle obstacle : room.getObstacles()) {
            Rectangle r = new Rectangle(obstacle.getXPos(), obstacle.getYPos(), obstacle.getWidth(), obstacle.getHeight());
            root.getChildren().add(r);
        }
        if (room.getTopDoor() != null) {
            Rectangle r = new Rectangle(room.getTopDoor().getXPos(), room.getTopDoor().getYPos(), room.getTopDoor().getWidth(), room.getTopDoor().getHeight());
            root.getChildren().add(r);
        }
        if (room.getRightDoor() != null) {
            Rectangle r = new Rectangle(room.getRightDoor().getXPos(), room.getRightDoor().getYPos(), room.getRightDoor().getWidth(), room.getRightDoor().getHeight());
            root.getChildren().add(r);
        }
        if (room.getBottomDoor() != null) {
            Rectangle r = new Rectangle(room.getBottomDoor().getXPos(), room.getBottomDoor().getYPos(), room.getBottomDoor().getWidth(), room.getBottomDoor().getHeight());
            root.getChildren().add(r);
        }
        if (room.getLeftDoor() != null) {
            Rectangle r = new Rectangle(room.getLeftDoor().getXPos(), room.getLeftDoor().getYPos(), room.getLeftDoor().getWidth(), room.getLeftDoor().getHeight());
            root.getChildren().add(r);
        }
        scene.getStylesheets().add(room.getStyleSheetPath());
        return scene;
    }
}
