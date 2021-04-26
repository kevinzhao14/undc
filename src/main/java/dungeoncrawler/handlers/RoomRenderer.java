package dungeoncrawler.handlers;
import dungeoncrawler.objects.ChallengeRoom;
import dungeoncrawler.objects.DroppedItem;
import dungeoncrawler.objects.ExitDoor;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.Obstacle;
import dungeoncrawler.objects.Player;
import dungeoncrawler.objects.Room;
import dungeoncrawler.objects.RoomType;
import dungeoncrawler.objects.ShotProjectile;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;


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
        double roomHeight = getPx(room.getHeight() * 1.36363636);
        double roomWidth = getPx(room.getWidth() * 1.11111111);
        if (room.getType() == RoomType.EXITROOM) {
            roomHeight = getPx(room.getHeight()) * 1.21621622;
            roomWidth = getPx(room.getWidth()) * 1.15384615;
        }

        main.setMaxHeight(roomHeight);
        main.setPrefHeight(roomHeight);
        main.setMinHeight(roomHeight);
        main.setMaxWidth(roomWidth);
        main.setPrefWidth(roomWidth);
        main.setMinWidth(roomWidth);

        main.getStyleClass().add("rootPane");

        //shift game rectangle so that it's aligned with the background image
        if (room.getType() == RoomType.EXITROOM) {
            root.setTranslateX(getPx(room.getWidth()) * 0.0769230769);
            root.setTranslateY(getPx(room.getHeight()) * 0.144144144);
        } else {
            root.setTranslateX(getPx(room.getWidth() * 0.0555555556));
            root.setTranslateY(getPx(room.getHeight() * 0.23863636363));
        }

        //add canvas to root
        canvas.setHeight(rootHeight + GameSettings.CANVAS_PADDING * 2);
        canvas.setWidth(rootWidth + GameSettings.CANVAS_PADDING * 2);
        canvas.setTranslateX(-GameSettings.CANVAS_PADDING);
        canvas.setTranslateY(-GameSettings.CANVAS_PADDING);

        main.setStyle("-fx-padding: 50px");



        if (room.getTopDoor() != null) {
            ImageView imageView = new ImageView("textures/dungeon1-topdoor.png");
            if (room instanceof ChallengeRoom && !((ChallengeRoom) room).isCompleted()) {
                imageView = new ImageView("textures/dungeon1-topdoor-blocked.png");
            }
            double y = getPx(room.getHeight() - room.getTopDoor().getY()
                    - room.getTopDoor().getHeight()) + 2;
            if (room.getTopDoor() instanceof ExitDoor) {
                imageView = new ImageView("textures/dungeon1-topdoor-exit.png");
                y--;
            }
            imageView.setX(getPx(room.getTopDoor().getX()));
            imageView.setY(y);
            imageView.setFitWidth(getPx(room.getTopDoor().getWidth()));
            imageView.setFitHeight(getPx(room.getTopDoor().getHeight()));
            root.getChildren().add(imageView);
        }
        if (room.getRightDoor() != null) {
            ImageView imageView = new ImageView("textures/dungeon1-rightdoor.png");
            if (room instanceof ChallengeRoom && !((ChallengeRoom) room).isCompleted()) {
                imageView = new ImageView("textures/dungeon1-rightdoor-blocked.png");
            }

            imageView.setX(getPx(room.getRightDoor().getX()));
            imageView.setY(getPx(room.getHeight() - room.getRightDoor().getY()
                    - room.getRightDoor().getHeight() * 2));
            imageView.setFitWidth(getPx(room.getRightDoor().getWidth()));
            imageView.setFitHeight(getPx(room.getRightDoor().getHeight()) * 2);
            root.getChildren().add(imageView);
        }
        if (room.getBottomDoor() != null) {
            ImageView imageView = new ImageView("textures/dungeon1-bottomdoor.png");
            if (room instanceof ChallengeRoom && !((ChallengeRoom) room).isCompleted()) {
                imageView = new ImageView("textures/dungeon1-bottomdoor-blocked.png");
            }

            imageView.setX(getPx(room.getBottomDoor().getX()));
            imageView.setY(getPx(room.getHeight() - room.getBottomDoor().getY()
                    - room.getBottomDoor().getHeight()) + 2);
            imageView.setFitWidth(getPx(room.getBottomDoor().getWidth()));
            imageView.setFitHeight(getPx(room.getBottomDoor().getHeight()));
            root.getChildren().add(imageView);
        }
        if (room.getLeftDoor() != null) {
            ImageView imageView = new ImageView("textures/dungeon1-leftdoor.png");
            if (room instanceof ChallengeRoom && !((ChallengeRoom) room).isCompleted()) {
                imageView = new ImageView("textures/dungeon1-leftdoor-blocked.png");
            }

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
        gc.clearRect(0, 0, c.getWidth(), c.getHeight());
        gc.setGlobalAlpha(1);

        double x;
        double y;
        double h;
        double w;
        Image img;

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
                img = obstacle.getSprite();
                drawImg(gc, img, h, w, x, y);

            }
        }
        if (room.getMonsters() != null) {
            for (Monster m : room.getMonsters()) {
                if (m != null && (m.getHealth() > 0 || m.getOpacity() > 0)) {
                    h = getPx(m.getHeight() / m.getSpriteHeight());
                    w = getPx(m.getWidth() / m.getSpriteWidth());
                    x = getPx(m.getX());
                    y = getPx(room.getHeight() - m.getY() - m.getHeight());
                    gc.setGlobalAlpha(m.getOpacity());
                    drawImg(gc, m.getImage(), h, w, x, y);
                    drawHealthbar(gc, GameSettings.MONSTER_HEALTHBAR_HEIGHT, w, x, y
                            - GameSettings.MONSTER_HEALTHBAR_HEIGHT - 10, m.getHealth()
                            / m.getMaxHealth());
                    if (m.getOpacity() < 1) {
                        m.setOpacity(m.getOpacity() - (1000.0 / (GameSettings.MONSTER_FADE_TIME
                                * GameSettings.FPS)));
                    }
                }
            }
        }
        gc.setGlobalAlpha(1);
        if (room.getDroppedItems() != null) {
            for (DroppedItem item : room.getDroppedItems()) {
                h = getPx(item.getHeight()) * GameSettings.DROP_ITEM_SPRITE_SCALE;
                w = getPx(item.getWidth()) * GameSettings.DROP_ITEM_SPRITE_SCALE;
                x = getPx(item.getX() + item.getWidth()
                        * (1 - GameSettings.DROP_ITEM_SPRITE_SCALE) / 2);
                y = getPx(room.getHeight() - item.getY() - item.getHeight() + item.getHeight()
                        * (1 - GameSettings.DROP_ITEM_SPRITE_SCALE) / 2);
                img = item.getItem().getSprite();
                drawImg(gc, img, h, w, x, y);
            }
        }
        //draw projectiles
        for (ShotProjectile p : room.getProjectiles()) {
            h = getPx(p.getHeight());
            w = getPx(p.getWidth());
            x = getPx(p.getX());
            y = getPx(room.getHeight() - p.getY() - p.getHeight());
            img = p.getSprite();
            drawImg(gc, img, h, w, x, y);
        }

        //draw player
        if (player.getHealth() > 0) {
            x = getPx(player.getX());
            y = getPx(room.getHeight() - player.getY() - player.getHeight() * 2);
            h = getPx(player.getHeight() * 2);
            w = getPx(player.getWidth());
            img = player.getImage();
            drawImg(gc, img, h, w, x, y);
        }
    }

    private static void drawImg(GraphicsContext gc, Image img, double h, double w, double x,
                                double y) {
        gc.drawImage(img, x + GameSettings.CANVAS_PADDING, y + GameSettings.CANVAS_PADDING, w, h);
    }

    private static void drawHealthbar(GraphicsContext gc, double h, double w, double x, double y,
                                      double percent) {
        //draw health bar
        int pad = GameSettings.CANVAS_PADDING;
        gc.setFill(Color.GREEN);
        gc.fillRect(x + pad, y + pad, percent * w, h);
        gc.setFill(Color.GRAY);
        gc.fillRect(x + percent * w + pad, y + pad, (1 - percent) * w, h);
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
