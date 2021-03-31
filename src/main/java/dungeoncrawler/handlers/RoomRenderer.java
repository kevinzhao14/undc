package dungeoncrawler.handlers;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.Obstacle;
import dungeoncrawler.objects.Player;
import dungeoncrawler.objects.Room;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
     * @param canvas Canvas to draw on
     * @return a pane with all the room's doors and obstacles and treasures
     */
    public static Pane drawRoom(Scene scene, Room room, Canvas canvas) {
        Pane root = new Pane();
        Pane main = new Pane();
        Pane bgPane = new Pane();
        main.getChildren().addAll(root);

        double rootHeight = getPx(room.getHeight());
        double rootWidth = getPx(room.getWidth());
        root.setMaxHeight(rootHeight);
        root.setPrefHeight(rootHeight);
        root.setMinHeight(rootHeight);
        root.setMaxWidth(rootWidth);
        root.setPrefWidth(rootWidth);
        root.setMinWidth(rootWidth);

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

        //add canvas to root
        canvas.setHeight(rootHeight + GameSettings.CANVAS_PADDING * 2);
        canvas.setWidth(rootWidth + GameSettings.CANVAS_PADDING * 2);
        canvas.setTranslateX(-GameSettings.CANVAS_PADDING);
        canvas.setTranslateY(-GameSettings.CANVAS_PADDING);

        main.setStyle("-fx-padding: 50px");

        if (room.getTopDoor() != null) {
            ImageView imageView = new ImageView("textures/dungeon1-topdoor.png");
            imageView.setX(getPx(room.getTopDoor().getX()));
            imageView.setY(getPx(room.getHeight() - room.getTopDoor().getY()
                    - room.getTopDoor().getHeight()) + 2);
            imageView.setFitWidth(getPx(room.getTopDoor().getWidth()));
            imageView.setFitHeight(getPx(room.getTopDoor().getHeight()));
            root.getChildren().add(imageView);
        }
        if (room.getRightDoor() != null) {
            ImageView imageView = new ImageView("textures/dungeon1-rightdoor.png");
            imageView.setX(getPx(room.getRightDoor().getX()));
            imageView.setY(getPx(room.getHeight() - room.getRightDoor().getY()
                    - room.getRightDoor().getHeight() * 2));
            imageView.setFitWidth(getPx(room.getRightDoor().getWidth()));
            imageView.setFitHeight(getPx(room.getRightDoor().getHeight()) * 2);
            root.getChildren().add(imageView);
        }
        if (room.getBottomDoor() != null) {
            ImageView imageView = new ImageView("textures/dungeon1-bottomdoor.png");
            imageView.setX(getPx(room.getBottomDoor().getX()));
            imageView.setY(getPx(room.getHeight() - room.getBottomDoor().getY()
                    - room.getBottomDoor().getHeight()) + 2);
            imageView.setFitWidth(getPx(room.getBottomDoor().getWidth()));
            imageView.setFitHeight(getPx(room.getBottomDoor().getHeight()));
            root.getChildren().add(imageView);
        }
        if (room.getLeftDoor() != null) {
            ImageView imageView = new ImageView("textures/dungeon1-leftdoor.png");
            imageView.setX(getPx(room.getLeftDoor().getX()));
            imageView.setY(getPx(room.getHeight() - room.getLeftDoor().getY()
                    - room.getLeftDoor().getHeight() * 2));
            imageView.setFitWidth(getPx(room.getLeftDoor().getWidth()));
            imageView.setFitHeight(getPx(room.getLeftDoor().getHeight()) * 2);
            root.getChildren().add(imageView);
        }
        scene.getStylesheets().add("styles/" + room.getType().name() + ".css");
        root.getChildren().add(canvas);

        return main;
    }

    public static void drawFrame(Canvas c, Room room, Player player) {
        //clear canvas
        GraphicsContext gc = c.getGraphicsContext2D();

        //draw player
        double x = getPx(player.getPosX());
        double y = getPx(room.getHeight() - player.getPosY() - player.getHeight() * 2);
        double h = getPx(player.getHeight() * 2);
        double w = getPx(player.getWidth());
        Image img = player.getImage();
        gc.clearRect(0, 0, c.getWidth(), c.getHeight());
        drawImg(c, img, h, w, x, y);

        x = 0;
        y = 0;
        h = 10;
        w = 10;
        img = null;
        //draw obstacles
        if (room.getObstacles() != null) {
            for (Obstacle obstacle : room.getObstacles()) {
                if (obstacle == null) {
                    continue;
                }
                x = getPx(obstacle.getX());
                y = getPx(room.getHeight() - obstacle.getY() - obstacle.getHeight());
                w = getPx(obstacle.getWidth());
                h = getPx(obstacle.getHeight());
                if (obstacle.getType().name().equals("KEY")) {
                    img = new Image("items/key.png");
                } else {
                    img = new Image("items/" + obstacle.getType().toString().toLowerCase() + ".png");
                }
                drawImg(c, img, h, w, x, y);

            }
        }
        if (room.getMonsters() != null) {
            for (Monster monster : room.getMonsters()) {
                if (monster != null && monster.getHealth() > 0) {
//                    node.setOpacity(monster.getDeathProgress());
                    h = getPx(monster.getHeight() / monster.getSpriteHeight());
                    w = getPx(monster.getWidth() / monster.getSpriteWidth());
                    x = getPx(monster.getPosX());
                    y = getPx(room.getHeight() - monster.getPosY() - monster.getHeight());
                    drawImg(c, monster.getImage(), h, w, x, y);
                }
            }
        }
    }

    private static void drawImg(Canvas c, Image img, double h, double w, double x, double y) {
        GraphicsContext gc = c.getGraphicsContext2D();
        gc.drawImage(img, x + GameSettings.CANVAS_PADDING, y + GameSettings.CANVAS_PADDING, w, h);
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
