package undc.graphics;

import undc.command.Vars;
import undc.game.DroppedItem;
import undc.entity.Monster;
import undc.game.Floor;
import undc.game.GameController;
import undc.game.Obstacle;
import undc.entity.Player;
import undc.game.Room;
import undc.game.ShotProjectile;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import undc.game.calc.Direction;


/**
 * Class that handles the graphics for the current Room the player is in.
 */
public class RoomRenderer {
    private static final SpriteGroup WALLS = new SpriteGroup(
            new Image("textures/room/left.png"),
            new Image("textures/room/top-left.png"),
            new Image("textures/room/top.png"),
            new Image("textures/room/top-right.png"),
            new Image("textures/room/right.png"),
            new Image("textures/room/bottom-right.png"),
            new Image("textures/room/bottom.png"),
            new Image("textures/room/bottom-left.png")
    );

    private static final double WALL_SIZE = 64;

    private static double offsetX = 0;
    private static double offsetY = 0;

    /**
     * Draws the room, adding all the doors, obstacles, and treasures.
     * @param scene The room's scene
     * @param room The room to draw
     * @param canvas Canvas to draw on
     * @return a pane with all the room's doors and obstacles and treasures
     */
    public static Pane drawRoom(Scene scene, Room room, Canvas canvas) {
        Pane root = new Pane();
        Pane main = new Pane();
        main.getChildren().addAll(root);

        //add canvas to root
        canvas.setHeight(Vars.i("gc_screen_height"));
        canvas.setWidth(Vars.i("gc_screen_width"));

        scene.getStylesheets().add("styles/" + room.getType().name() + ".css");
        root.getChildren().add(canvas);

        return main;
    }

    /**
     * Creates the room and the objects in the room that the player is in.
     * @param c Canvas
     * @param room Room the play is in
     * @param player Player
     */
    public static void drawFrame(Canvas c, Room room, Player player) {
        //clear canvas
        GraphicsContext gc = c.getGraphicsContext2D();
        gc.clearRect(0, 0, c.getWidth(), c.getHeight());
        gc.setGlobalAlpha(1);

        GameController game = GameController.getInstance();
        // how far the edge of the canvas is from the edge of the room
        offsetX = game.getCamX() - c.getWidth() / 2.0 / Vars.d("gc_ppu");
        offsetY = game.getCamY() - c.getHeight() / 2.0 / Vars.d("gc_ppu");

        double x;
        double y;
        double h;
        double w;
        Image img;

        // draw floor
        for (Floor f : room.getFloors()) {
            if (f.getX() + f.getWidth() > offsetX && f.getX() < offsetX + c.getWidth()
                    && f.getY() + f.getHeight() > offsetY && f.getY() < offsetY + c.getHeight()) {
                drawImg(gc, f.getSprite(), getPx(f.getHeight()), getPx(f.getWidth()), getPx(f.getX()),
                        getPx(getY(room, f.getY(), f.getHeight())));
            }
        }

        // draw walls
        // left
        double size = getPx(WALL_SIZE);
        if (offsetX < 0) {
            y = Math.max(offsetY, 0);
            while (y < room.getHeight()) {
                drawImg(gc, WALLS.get(Direction.WEST), size, size, -size, getPx(getY(room, y, WALL_SIZE)));
                y += WALL_SIZE;
            }
        }
        // right
        if (offsetX + c.getWidth() > room.getWidth()) {
            y = Math.max(offsetX, 0);
            while (y < room.getHeight()) {
                drawImg(gc, WALLS.get(Direction.EAST), size, size, getPx(room.getWidth()),
                        getPx(getY(room, y, WALL_SIZE)));
                y += WALL_SIZE;
            }
        }
        // top
        if (offsetY + c.getHeight() > room.getHeight()) {
            x = Math.max(offsetX, 0);
            while (x < room.getWidth()) {
                drawImg(gc, WALLS.get(Direction.NORTH), size, size, getPx(x),
                        getPx(getY(room, room.getHeight(), WALL_SIZE)));
                x += WALL_SIZE;
            }
        }
        // corners
        // bottom left
        if (offsetX < 0 && offsetY < 0) {
            drawImg(gc, WALLS.get(Direction.SOUTHWEST), size, size, -size,
                    getPx(getY(room, -WALL_SIZE + 16, WALL_SIZE)));
        }
        // top left
        if (offsetX < 0 && offsetY + c.getHeight() > room.getHeight()) {
            drawImg(gc, WALLS.get(Direction.NORTHWEST), size, size, -size,
                    getPx(getY(room, room.getHeight(), WALL_SIZE)));
        }
        // top right
        if (offsetX + c.getWidth() > room.getWidth() && offsetY + c.getHeight() > room.getHeight()) {
            drawImg(gc, WALLS.get(Direction.NORTHEAST), size, size, getPx(room.getWidth()),
                    getPx(getY(room, room.getHeight(), WALL_SIZE)));
        }
        // bottom right
        if (offsetX + c.getWidth() > room.getWidth() && offsetY < 0) {
            drawImg(gc, WALLS.get(Direction.SOUTHEAST), size, size, getPx(room.getWidth()),
                    getPx(getY(room, -WALL_SIZE + 16, WALL_SIZE)));
        }

        //draw obstacles
        if (room.getObstacles() != null) {
            for (Obstacle obstacle : room.getObstacles()) {
                if (obstacle == null) {
                    continue;
                }
                x = getPx(obstacle.getX());
                y = getPx(getY(room, obstacle.getY(), obstacle.getHeight()));
                w = getPx(obstacle.getWidth());
                h = getPx(obstacle.getHeight());
                img = obstacle.getSprite();
                drawImg(gc, img, h, w, x, y);
            }
        }
        if (room.getMonsters() != null) {
            for (Monster m : room.getMonsters()) {
                if (m != null && (m.getHealth() > 0 || m.getOpacity() > 0)) {
                    h = getPx(m.getHeight());
                    w = getPx(m.getWidth());
                    x = getPx(m.getX());
                    y = getPx(getY(room, m.getY(), m.getHeight()));
                    gc.setGlobalAlpha(m.getOpacity());
                    drawImg(gc, m.getSprite(), h, w, x, y);
                    int hbh = Vars.i("gc_healthbar_height");
                    drawHealthbar(gc, hbh, w, x, y - hbh - 10, m.getHealth() / m.getMaxHealth());
                    if (m.getOpacity() < 1) {
                        m.setOpacity(m.getOpacity() - (1000.0 / Vars.i("gc_monster_fade_dur") / Vars.i("sv_tickrate")));
                    }
                }
            }
        }
        gc.setGlobalAlpha(1);
        if (room.getDroppedItems() != null) {
            for (DroppedItem item : room.getDroppedItems()) {
                double scale = Vars.d("gc_dropitem_scale");
                h = getPx(item.getHeight()) * scale;
                w = getPx(item.getWidth()) * scale;
                x = getPx(item.getX() + item.getWidth() * (1 - scale) / 2);
                y = getPx(getY(room, item.getY(), item.getHeight()) + item.getHeight() * (1 - scale) / 2);
                img = item.getItem().getSprite();
                drawImg(gc, img, h, w, x, y);
            }
        }
        //draw projectiles
        for (ShotProjectile p : room.getProjectiles()) {
            h = getPx(p.getHeight());
            w = getPx(p.getWidth());
            x = getPx(p.getX());
            y = getPx(getY(room, p.getY(), p.getHeight()));
            img = p.getSprite();
            drawImg(gc, img, h, w, x, y);
        }

        //draw player
        if (player.getHealth() > 0) {
            x = getPx(player.getX());
            y = getPx(getY(room, player.getY(), player.getHeight() * 2));
            h = getPx(player.getHeight() * 2);
            w = getPx(player.getWidth());
            img = player.getSprite();
            drawImg(gc, img, h, w, x, y);
        }

        // bottom wall
        if (offsetY < 0) {
            x = Math.max(offsetX, 0);
            while (x < room.getWidth()) {
                drawImg(gc, WALLS.get(Direction.SOUTH), size, size, getPx(x), getPx(getY(room, -WALL_SIZE + 16,
                        WALL_SIZE)));
                x += WALL_SIZE;
            }
        }
    }

    private static void drawImg(GraphicsContext gc, Image img, double h, double w, double x, double y) {
        gc.drawImage(img, x - getPx(offsetX), y - getPx(offsetY), w, h);
    }

    /**
     * Creates the graphics for monster's health bars.
     * @param gc GraphicContext that is the visual representation of a health bar
     * @param h double height
     * @param w double width
     * @param x double x-cord
     * @param y double y-cord
     * @param percent double percentage of monster's remaining health
     */
    private static void drawHealthbar(GraphicsContext gc, double h, double w, double x, double y, double percent) {
        //draw health bar
        gc.setFill(Color.GREEN);
        gc.fillRect(x - getPx(offsetX), y - getPx(offsetY), percent * w, h);
        gc.setFill(Color.GRAY);
        gc.fillRect(x + percent * w - getPx(offsetX), y - getPx(offsetY), (1 - percent) * w, h);
    }

    /**
     * Converting coordinates from game units to pixels.
     * @param coord the coordinate to convert
     * @return the converted coordinate
     */
    private static double getPx(double coord) {
        return (coord * Vars.d("gc_ppu"));
    }

    private static double getY(Room r, double y, double h) {
        return r.getHeight() - y - h;
    }
}
