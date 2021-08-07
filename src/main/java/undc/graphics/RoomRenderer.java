package undc.graphics;

import undc.command.DataManager;
import undc.command.Vars;
import undc.entity.Entity;
import undc.game.objects.Door;
import undc.game.objects.DroppedItem;
import undc.entity.Monster;
import undc.game.objects.Floor;
import undc.game.GameController;
import undc.game.objects.Obstacle;
import undc.entity.Player;
import undc.game.Room;
import undc.game.objects.ShotProjectile;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import undc.game.calc.Direction;
import undc.general.AVL;

import java.util.ArrayList;


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
    private static final AVL<RenderObject> PIPE = new AVL<>();

    private static double offsetX = 0;
    private static double offsetY = 0;
    private static GraphicsContext gc;
    private static Room room;

    /**
     * Draws the room, adding all the doors, obstacles, and treasures.
     * @param canvas Canvas to draw on
     * @return a pane with all the room's doors and obstacles and treasures
     */
    public static Pane drawRoom(Canvas canvas) {
        Pane main = new Pane();

        // add canvas to root
        canvas.setHeight(Vars.i("gc_screen_height"));
        canvas.setWidth(Vars.i("gc_screen_width"));
        canvas.widthProperty().bind(main.widthProperty());
        canvas.heightProperty().bind(main.heightProperty());

        main.getChildren().addAll(canvas);

        return main;
    }

    /**
     * Creates the room and the objects in the room that the player is in.
     * @param c Canvas
     * @param r Room the play is in
     * @param player Player
     */
    public static void drawFrame(Canvas c, Room r, Player player) {
        //clear canvas
        gc = c.getGraphicsContext2D();
        gc.clearRect(0, 0, c.getWidth(), c.getHeight());
        gc.setGlobalAlpha(1);
        room = r;

        // data vars
        GameController game = GameController.getInstance();
        Camera cam = game.getCamera();
        // how far the edge of the canvas is from the edge of the room
        offsetX = cam.getX() - c.getWidth() / 2.0 / Vars.d("gc_ppu");
        offsetY = room.getHeight() - (cam.getY() + c.getHeight() / 2.0 / Vars.d("gc_ppu"));

        double x;
        double y;
        double w;
        double h;
        double sw;
        double sh;
        Image img;

        // draw floor
        for (Floor f : room.getFloors()) {
            // if the floor unit is within the visible bounds of the canvas, ie it should be visible, then draw it
            // right side of floor is within the left wall of the canvas, or the left side of floor is within right
            // wall of the canvas, and vice versa with Y
            if ((f.getX() + f.getWidth() > offsetX || f.getX() < offsetX + c.getWidth())
                    && (getY(f.getY(), f.getHeight()) + f.getHeight() > offsetY
                    || getY(f.getY(), f.getHeight()) < offsetY)) {
                h = f.getHeight();
                w = f.getWidth();

                // clip the sprite if it overflows the room
                // if the right side of the floor passes the right wall of the room, then set the display width to as
                // far as the floor can go until it hits the room's wall
                if (f.getX() + f.getWidth() > room.getWidth()) {
                    w = room.getWidth() - f.getX();
                }
                if (f.getY() + f.getHeight() > room.getHeight()) {
                    h = room.getHeight() - f.getY();
                }

                // get the height/width of the sprite based on percentage of new h / original h
                Image sprite = DataManager.FLOORS.get(f.getId());
                sw = w / f.getWidth() * sprite.getWidth();
                sh = h / f.getHeight() * sprite.getHeight();
                PIPE.add(new RenderObject(sprite, sw, sh, getPx(w), getPx(h), getPx(f.getX()),
                        getPx(getY(f.getY(), h)), f.getZ()));
            }
        }

        // draw walls
        // left
        double size = getPx(WALL_SIZE);
        if (offsetX < 0) {
            // start drawing walls at the earliest visible wall tile
            y = Math.floor(Math.max(offsetY, 0) / WALL_SIZE) * WALL_SIZE;
            // keep drawing until the walls hit the room's wall or should not be visible
            while (y < room.getHeight() && y - offsetY < getUnits(c.getHeight())) {
                drawWall(Direction.WEST, 0, y, -100);
                y += WALL_SIZE;
            }
        }
        // right
        if (offsetX + c.getWidth() > room.getWidth()) {
            y = Math.floor(Math.max(offsetY, 0) / WALL_SIZE) * WALL_SIZE;
            while (y < room.getHeight() && y - offsetY < getUnits(c.getHeight())) {
                drawWall(Direction.EAST, 0, y, -100);
                y += WALL_SIZE;
            }
        }
        // top
        if (offsetY < 0) {
            x = Math.floor(Math.max(offsetX, 0) / WALL_SIZE) * WALL_SIZE;
            while (x < room.getWidth() && x - offsetX < getUnits(c.getWidth())) {
                drawWall(Direction.NORTH, x, 0, -1000);
                x += WALL_SIZE;
            }
        }

        // corners
        // bottom left
        // if the bottom-left corner should be visible, then draw it
        // if the left wall is showing & the bottom wall is showing
        if (offsetX < 0 && offsetY + c.getHeight() > room.getHeight()) {
            PIPE.add(new RenderObject(WALLS.get(Direction.SOUTHWEST), size, size, -size,
                    getPx(getY(-WALL_SIZE + 16, WALL_SIZE)), 1000));
        }
        // top left
        if (offsetX < 0 && offsetY < 0) {
            PIPE.add(new RenderObject(WALLS.get(Direction.NORTHWEST), size, size, -size,
                    getPx(getY(room.getHeight(), WALL_SIZE)), -1000));
        }
        // top right
        if (offsetX + c.getWidth() > room.getWidth() && offsetY < 0) {
            PIPE.add(new RenderObject(WALLS.get(Direction.NORTHEAST), size, size, getPx(room.getWidth()),
                    getPx(getY(room.getHeight(), WALL_SIZE)), -1000));
        }
        // bottom right
        if (offsetX + c.getWidth() > room.getWidth() && offsetY + c.getHeight() > room.getHeight()) {
            PIPE.add(new RenderObject(WALLS.get(Direction.SOUTHEAST), size, size, getPx(room.getWidth()),
                    getPx(getY(-WALL_SIZE + 16, WALL_SIZE)), 1000));
        }

        // doors
        if (room.getTopDoor() != null) {
            drawDoor(room.getTopDoor(), Direction.NORTH, -1000);
        }
        if (room.getRightDoor() != null) {
            drawDoor(room.getRightDoor(), Direction.EAST, 0);
        }
        if (room.getLeftDoor() != null) {
            drawDoor(room.getLeftDoor(), Direction.WEST, 0);
        }

        //draw obstacles
        if (room.getObstacles() != null) {
            for (Obstacle obstacle : room.getObstacles()) {
                if (obstacle == null) {
                    continue;
                }
                x = getPx(obstacle.getX());
                y = getPx(getY(obstacle.getY(), obstacle.getHeight()));
                w = getPx(obstacle.getWidth());
                h = getPx(obstacle.getHeight());
                img = obstacle.getSprite();
                PIPE.add(new RenderObject(img, w, h, x, y, obstacle.getZ()));
            }
        }
        if (room.getEntities() != null) {
            double hbh = Vars.i("gc_healthbar_height");
            for (Entity e : room.getEntities()) {
                if (e.getHealth() > 0 || (e instanceof Monster && ((Monster) e).getOpacity() > 0)) {
                    h = getPx(e.getHeight());
                    w = getPx(e.getWidth());
                    x = getPx(e.getX());
                    y = getPx(getY(e.getY(), e.getHeight()));
                    RenderObject o = new RenderObject(e.getSprite(), w, h, x, y, e.getZ());
                    if (e instanceof Monster) {
                        Monster m = (Monster) e;
                        o.opacity = ((Monster) e).getOpacity();

                        // draw healthbar
                        PIPE.add(new RenderObject(e.getHealth() / e.getMaxHealth(), w, hbh, x, y - hbh - 10, 200));

                        // update opacity
                        if (m.getOpacity() < 1) {
                            double newOpacity = m.getOpacity() - (1000.0 / Vars.i("gc_monster_fade_dur")
                                    / Vars.i("sv_tickrate"));
                            newOpacity = Math.round(newOpacity * Vars.i("sv_precision"))
                                    / (double) Vars.i("sv_precision");
                            m.setOpacity(newOpacity);
                        }
                    }
                    PIPE.add(o);
                }
            }
        }
        if (room.getDroppedItems() != null) {
            for (DroppedItem item : room.getDroppedItems()) {
                double scale = Vars.d("gc_dropitem_scale");
                h = getPx(item.getHeight()) * scale;
                w = getPx(item.getWidth()) * scale;
                x = getPx(item.getX() + item.getWidth() * (1 - scale) / 2);
                y = getPx(getY(item.getY(), item.getHeight()) + item.getHeight() * (1 - scale) / 2);
                img = item.getItem().getSprite();
                PIPE.add(new RenderObject(img, w, h, x, y, item.getZ()));
            }
        }
        //draw projectiles
        for (ShotProjectile p : room.getProjectiles()) {
            h = getPx(p.getHeight());
            w = getPx(p.getWidth());
            x = getPx(p.getX());
            y = getPx(getY(p.getY(), p.getHeight()));
            img = p.getSprite();
            PIPE.add(new RenderObject(img, w, h, x, y, p.getZ()));
        }

        //draw player
        if (player.getHealth() > 0) {
            x = getPx(player.getX());
            y = getPx(getY(player.getY(), player.getHeight() * 2));
            h = getPx(player.getHeight() * 2);
            w = getPx(player.getWidth());
            img = player.getSprite();
            PIPE.add(new RenderObject(img, w, h, x, y, player.getZ()));

            // move camera if necessary
            int spacing = Vars.i("gc_camera_spacing_x");
            x = player.getX() - offsetX;
            if (x < spacing) {
                cam.setX(cam.getX() - (spacing - x));
            } else if (getUnits(c.getWidth()) - x + player.getWidth() < spacing) {
                cam.setX(cam.getX() + (spacing - (getUnits(c.getWidth()) - x + player.getWidth())));
            }
            spacing = Vars.i("gc_camera_spacing_y");
            y = getY(player.getY(), player.getHeight()) - offsetY;
            if (y < spacing) {
                cam.setY(cam.getY() + (spacing - y));
            } else if (getUnits(c.getHeight()) - y + player.getHeight() < spacing) {
                cam.setY(cam.getY() - (spacing - (getUnits(c.getHeight()) - y + player.getHeight())));
            }
        }

        // bottom wall
        if (offsetY + c.getHeight() > room.getHeight()) {
            x = Math.floor(Math.max(offsetX, 0) / WALL_SIZE) * WALL_SIZE;
            while (x < room.getWidth() && x - offsetX < getUnits(c.getWidth())) {
                drawWall(Direction.SOUTH, x, 0, 1000);
                x += WALL_SIZE;
            }
        }
        if (room.getBottomDoor() != null) {
            drawDoor(room.getBottomDoor(), Direction.SOUTH, 1000);
        }

        // render items in order of pipe
        ArrayList<RenderObject> list = PIPE.inOrder();
        for (RenderObject o : list) {
            if (o.type == RenderType.IMAGE) {
                gc.setGlobalAlpha(o.opacity);
                drawImg(o.image, o.width, o.height, o.x, o.y);
            } else if (o.type == RenderType.PARTIAL_IMAGE) {
                drawImg(o.image, o.sourceWidth, o.sourceHeight, o.width, o.height, o.x, o.y);
            } else if (o.type == RenderType.HEALTHBAR) {
                drawHealthbar(o.width, o.height, o.x, o.y, o.percent);
            }
        }
        PIPE.clear();
    }

    /**
     * Draws a door on the canvas.
     * @param door Door to draw
     * @param dir Orientation of the door
     */
    private static void drawDoor(Door door, Direction dir, double z) {
        int x = dir == Direction.WEST ? -1 : (dir == Direction.EAST ? 1 : 0);
        int y = dir == Direction.SOUTH ? 15 : (dir == Direction.NORTH ? 1 : 0);
        PIPE.add(new RenderObject(door.getSprite(), getPx(door.getWidth()), getPx(door.getHeight()),
                getPx(door.getX() + x), getPx(getY(door.getY() + y, door.getHeight())), z));
    }

    /**
     * Draws a wall on the canvas.
     * @param dir Side of the room the wall is on
     * @param x X position of the wall
     * @param y Y position of the wall
     */
    private static void drawWall(Direction dir, double x, double y, double z) {
        // data variables
        double size = getPx(WALL_SIZE);
        double w = WALL_SIZE;
        double h = WALL_SIZE;
        Image sprite = WALLS.get(dir);
        double sw = sprite.getWidth();
        double sh = sprite.getHeight();

        // clip the sprite if it overflows the room; see floor rendering for how this works
        if ((dir == Direction.SOUTH || dir == Direction.NORTH) && x + WALL_SIZE > room.getWidth()) {
            w = room.getHeight() - x;
            sw = w / WALL_SIZE * sprite.getWidth();
        }
        if ((dir == Direction.EAST || dir == Direction.WEST) && y + WALL_SIZE > room.getHeight()) {
            h = room.getHeight() - y;
            sh = h / WALL_SIZE * sprite.getHeight();
        }

        double dx = 0;
        double dy = 0;
        double dw = size;
        double dh = size;
        if (dir == Direction.WEST || dir == Direction.EAST) {
            dh = getPx(h);
            dy = getPx(y);
            dx = dir == Direction.WEST ? -size : getPx(room.getWidth());
        }
        if (dir == Direction.SOUTH || dir == Direction.NORTH) {
            dw = getPx(w);
            dx = getPx(x);
            dy = getPx(getY(dir == Direction.SOUTH ? -WALL_SIZE + 16 : room.getHeight(), WALL_SIZE));
        }
        PIPE.add(new RenderObject(sprite, sw, sh, dw, dh, dx, dy, z));
    }

    private static void drawImg(Image img, double w, double h, double x, double y) {
        gc.drawImage(img, x - getPx(offsetX), y - getPx(offsetY), w, h);
    }

    private static void drawImg(Image img, double sw, double sh, double dw, double dh, double dx, double dy) {
        gc.drawImage(img, 0, 0, sw, sh, dx - getPx(offsetX), dy - getPx(offsetY), dw, dh);
    }

    /**
     * Creates the graphics for monster's health bars.
     * @param h double height
     * @param w double width
     * @param x double x-coord
     * @param y double y-coord
     * @param percent double percentage of monster's remaining health
     */
    private static void drawHealthbar(double w, double h, double x, double y, double percent) {
        //draw health bar
        gc.setFill(Color.GREEN);
        gc.fillRect(x - getPx(offsetX), y - getPx(offsetY), percent * w, h);
        gc.setFill(Color.GRAY);
        gc.fillRect(x + percent * w - getPx(offsetX), y - getPx(offsetY), (1 - percent) * w, h);
    }

    private static double getPx(double coord) {
        return (coord * Vars.d("gc_ppu"));
    }

    private static double getUnits(double px) {
        return px / Vars.d("gc_ppu");
    }

    private static double getY(double y, double h) {
        return room.getHeight() - y - h;
    }

    enum RenderType {
        IMAGE, HEALTHBAR, PARTIAL_IMAGE
    }

    static class RenderObject implements Comparable<RenderObject> {
        RenderType type;
        Image image;
        double width;
        double height;
        double x;
        double y;
        double z;

        double percent;
        double sourceWidth;
        double sourceHeight;
        double opacity;

        /**
         * Constructor for an image-based RenderObject.
         * @param image Image to render
         * @param width Width of the object
         * @param height Height of the object
         * @param x X coordinate of the object
         * @param y Y coordinate of the object
         * @param z Z coordinate of the object
         */
        public RenderObject(Image image, double width, double height, double x, double y, double z) {
            this.type = RenderType.IMAGE;
            this.image = image;
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
            this.z = z;
            this.opacity = 1;
        }

        /**
         * Constructor for a healthbar-based RenderObject.
         * @param percent Percentage of the healthbar
         * @param width Width of the object
         * @param height Height of the object
         * @param x X coordinate of the object
         * @param y Y coordinate of the object
         * @param z Z coordinate of the object
         */
        public RenderObject(double percent, double width, double height, double x, double y, double z) {
            this(null, width, height, x, y, z);
            this.type = RenderType.HEALTHBAR;
            this.percent = percent;
        }

        /**
         * Constructor for a partial image-based RenderObject.
         * @param image Image to render
         * @param sourceWidth Width of the source rectangle
         * @param sourceHeight Height of the source rectangle
         * @param width Width of the object
         * @param height Height of the object
         * @param x X coordinate of the object
         * @param y Y coordinate of the object
         * @param z Z coordinate of the object
         */
        public RenderObject(Image image, double sourceWidth, double sourceHeight, double width, double height,
                            double x, double y, double z) {
            this(image, width, height, x, y, z);
            this.type = RenderType.PARTIAL_IMAGE;
            this.sourceWidth = sourceWidth;
            this.sourceHeight = sourceHeight;
        }

        @Override
        public int compareTo(RenderObject o) {
            if (this.z != o.z) {
                return (int) Math.round(this.z - o.z);
            } else {
                return (int) Math.round(this.y - o.y);
            }
        }
    }
}
