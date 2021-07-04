package undc.game;

import javafx.scene.image.Image;
import undc.command.Console;
import undc.command.Vars;
import undc.game.calc.Direction;
import undc.general.Controller;
import undc.command.DataManager;
import undc.graphics.GameScreen;
import undc.graphics.SpriteGroup;
import undc.inventory.GraphicalInventory;
import undc.item.WeaponAmmo;
import undc.item.Ammunition;
import undc.inventory.Inventory;
import undc.item.Item;
import undc.entity.Monster;
import undc.item.RangedWeapon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Class that generates Layout of the Rooms.
 */
public class LayoutGenerator {
    private static final int GRID_WIDTH = 15;
    private static final int GRID_HEIGHT = 15;
    public static final int ROOM_WIDTH = 576;
    public static final int ROOM_HEIGHT = 384;
    private static final int SANDBOX_WIDTH = 1000;
    private static final int SANDBOX_HEIGHT = 1000;

    public static final SpriteGroup DOORS = new SpriteGroup(
        new Image("textures/room/doors/left.png"),
        new Image("textures/room/doors/top.png"),
        new Image("textures/room/doors/right.png"),
        new Image("textures/room/doors/bottom.png")
    );

    public static final SpriteGroup DOORS_BLOCKED = new SpriteGroup(
        new Image("textures/room/doors/left-blocked.png"),
        new Image("textures/room/doors/top-blocked.png"),
        new Image("textures/room/doors/right-blocked.png"),
        new Image("textures/room/doors/bottom-blocked.png")
    );

    public static final Image DOOR_EXIT = new Image("textures/room/doors/top-portal.png");

    public static final int DOOR_SIZE = 64;

    private static final int PATH_MIN = 6;
    private static final int PATH_MAX = 10;

    private static final double CHALLENGE_ODDS = 0.1;

    private final Inventory cr1Rewards;
    private final Inventory cr2Rewards;

    private Room startRoom;
    private Room exitRoom;
    private ChallengeRoom cr1;
    private ChallengeRoom cr2;
    private int challengeCount;
    private boolean exitPlaced;
    private int[] exitCoords;
    private Room[][] roomGrid;
    private int roomId;

    /**
     * Creates the rewards for the challenge rooms.
     */
    public LayoutGenerator() {
        //set challenge rooms
        HashMap<String, Item> items = DataManager.ITEMS;
        cr1Rewards = new Inventory(2, 5);
        RangedWeapon rl = ((RangedWeapon) items.get("rocket_launcher")).copy();
        WeaponAmmo weaponAmmo = new WeaponAmmo(2, 50, DataManager.PROJECTILES.get("rocket"));
        weaponAmmo.setRemaining(2);
        weaponAmmo.setBackupRemaining(20);
        rl.setAmmo(weaponAmmo);

        cr1Rewards.add(items.get("large_health_potion"), 1); // large health potion
        cr1Rewards.add(items.get("attack_potion"), 1); // attack potion
        cr1Rewards.add(items.get("bomb"), 2); // bomb
        cr1Rewards.add(rl, 1); //rocket launcher

        cr2Rewards = new Inventory(2, 5);
        cr2Rewards.add(items.get("small_health_potion"), 3); // small health potion
        cr2Rewards.add(items.get("medium_health_potion"), 2); // medium health potion
        cr2Rewards.add(items.get("large_health_potion"), 1); // large health potion
        cr2Rewards.add(items.get("attack_potion"), 2); // attack potion
        cr2Rewards.add(items.get("dagger"), 1); // dagger
        cr2Rewards.add(items.get("bomb"), 3); // bomb
        Ammunition rockets = (Ammunition) items.get("rockets").copy(); // ammunition
        rockets.setAmount(20);
        cr2Rewards.add(rockets);
    }

    /**
     * Makes the star, exit, and challenge rooms, and populates them with monsters, items, etc..
     */
    private void reset() {
        roomId = 0;

        startRoom = new Room(roomId++, ROOM_WIDTH, ROOM_HEIGHT, (int) ((ROOM_WIDTH
                - Vars.i("sv_player_width")) / 2.0), (int) (ROOM_HEIGHT / 2.0
                - Vars.i("sv_player_height")), RoomType.STARTROOM, generateFloors(ROOM_WIDTH, ROOM_HEIGHT));
        startRoom.setMonsters(new ArrayList<>());
        generateObstacles(startRoom);

        int exitWidth = ROOM_WIDTH * 2;
        int exitHeight = ROOM_HEIGHT * 2;

        exitRoom = new Room(roomId++, exitWidth, exitHeight, 100, 100, RoomType.EXITROOM,
                generateFloors(exitWidth, exitHeight));
        generateObstacles(exitRoom, 4);

        Monster boss = DataManager.getFinalBoss();
        boss.setX(exitWidth / 2.0 - boss.getWidth() / 2.0);
        boss.setY(exitHeight - boss.getHeight() - 5);
        exitRoom.setMonsters(new ArrayList<>());
        exitRoom.getMonsters().add(DataManager.getFinalBoss());
        ExitDoor ed = new ExitDoor(DOOR_EXIT, (exitWidth - DOOR_SIZE) / 2, exitHeight - 1, DOOR_SIZE, DOOR_SIZE);
        exitRoom.setTopDoor(ed);

        cr1 = new ChallengeRoom(roomId++, ROOM_WIDTH, ROOM_HEIGHT, 100, 100, cr1Rewards,
                generateFloors(ROOM_WIDTH, ROOM_HEIGHT));
        cr2 = new ChallengeRoom(roomId++, ROOM_WIDTH, ROOM_HEIGHT, 100, 100, cr2Rewards,
                generateFloors(ROOM_WIDTH, ROOM_HEIGHT));

        setMonsters(cr1);
        setMonsters(cr2);

        challengeCount = 0;
        exitPlaced = false;
        exitCoords = new int[2];
        roomGrid = new Room[GRID_WIDTH][GRID_HEIGHT];
    }

    /**
     * generate the layout of the rooms.
     * @return the layout
     */
    public DungeonLayout generateLayout() {
        reset();

        //starting room
        roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2] = startRoom;

        //challenge rooms
        challengeCount = 0;

        //left path
        for (int i = 0; i < 4; i++) {
            generatePath(i);
        }

        //check exit distance
        double exitDistance = Math.abs(exitCoords[0] - GRID_WIDTH / 2) + Math.abs(exitCoords[1]
                - GRID_HEIGHT / 2);
        if (!exitPlaced || exitDistance < 6 || challengeCount < 2) {
            return generateLayout();
        }
        printGrid(roomGrid);

        // create doors
        for (int i = 1; i < GRID_WIDTH - 1; i++) {
            for (int j = 1; j < GRID_HEIGHT - 1; j++) {
                if (roomGrid[i][j] != null && roomGrid[i][j].getType() != RoomType.EXITROOM) {
                    if (roomGrid[i + 1][j] != null) {
                        roomGrid[i][j].setRightDoor(new Door(Direction.EAST, ROOM_WIDTH - 1,
                                (ROOM_HEIGHT - DOOR_SIZE) / 2.0, DOOR_SIZE, DOOR_SIZE, roomGrid[i + 1][j]));
                    }
                    if (roomGrid[i - 1][j] != null) {
                        roomGrid[i][j].setLeftDoor(new Door(Direction.WEST, -DOOR_SIZE + 1,
                                (ROOM_HEIGHT - DOOR_SIZE) / 2.0, DOOR_SIZE, DOOR_SIZE, roomGrid[i - 1][j]));
                    }
                    if (roomGrid[i][j + 1] != null) {
                        roomGrid[i][j].setBottomDoor(new Door(Direction.SOUTH, (ROOM_WIDTH - DOOR_SIZE) / 2.0,
                                -DOOR_SIZE + 1, DOOR_SIZE, DOOR_SIZE, roomGrid[i][j + 1]));
                    }
                    if (roomGrid[i][j - 1] != null) {
                        roomGrid[i][j].setTopDoor(new Door(Direction.NORTH, (ROOM_WIDTH - DOOR_SIZE) / 2.0,
                                ROOM_HEIGHT - 1, DOOR_SIZE, DOOR_SIZE, roomGrid[i][j - 1]));
                    }
                }
            }
        }

        return new DungeonLayout(startRoom, exitRoom, roomGrid);
    }

    /**
     * Generates the layout for a Sandbox game.
     * @return Returns the DungeonLayout for a Sandbox.
     */
    public DungeonLayout generateSandbox() {
        Room start = new Room(0, SANDBOX_WIDTH, SANDBOX_HEIGHT,
                (int) ((SANDBOX_WIDTH - Vars.i("sv_player_width")) / 2.0),
                (int) (SANDBOX_HEIGHT / 2.0 - Vars.i("sv_player_height")), RoomType.STARTROOM,
                generateFloors(SANDBOX_WIDTH, SANDBOX_HEIGHT));
        start.setMonsters(new ArrayList<>());

        Room exit = new Room(1, 10, 10, 0, 0, RoomType.EXITROOM, generateFloors(10, 10));
        exit.setMonsters(new ArrayList<>());

        // chest
        Inventory inv = new Inventory(4, 4);
        inv.add(DataManager.ITEMS.get("sword"));
        inv.add(DataManager.ITEMS.get("small_health_potion"), 4);
        inv.add(DataManager.ITEMS.get("attack_potion"), 2);
        inv.add(DataManager.ITEMS.get("bomb"));

        inv.setGraphicalInventory(new GraphicalInventory("Chest", inv,
                GameScreen.getInstance().getPlayer().getInventory()));

        Chest chest = new Chest(400, 400, inv);
        start.getObstacles().add(chest);

        Room[][] arr = new Room[][]{new Room[]{start, exit}};
        return new DungeonLayout(start, exit, arr);
    }

    /**
     * Generates a path of rooms in a specified direction.
     * @param dir Direction to generate in
     */
    private void generatePath(int dir) {
        int x = GRID_WIDTH / 2;
        if (dir == 0) {
            x--;
        } else if (dir == 2) {
            x++;
        }
        int y = GRID_HEIGHT / 2;
        if (dir == 1) {
            y++;
        } else if (dir == 3) {
            y--;
        }
        //create origin room
        Room r = new Room(roomId++, ROOM_WIDTH, ROOM_HEIGHT, 100, 100, RoomType.EMPTYROOM,
                generateFloors(ROOM_WIDTH, ROOM_HEIGHT));
        setMonsters(r);
        generateObstacles(r);
        roomGrid[x][y] = r;

        int[] coords = generateRoom(roomGrid, x, y, dir);
        Random rand = new Random();
        int pathLength = rand.nextInt(PATH_MAX - PATH_MIN + 1) + PATH_MIN;
        for (int i = 0; i < pathLength; i++) {
            if (coords == null) {
                break;
            }
            if (challengeCount < 2) {
                if (Math.random() < CHALLENGE_ODDS) {
                    if (challengeCount == 0) {
                        roomGrid[coords[0]][coords[1]] = cr1;
                    } else {
                        roomGrid[coords[0]][coords[1]] = cr2;
                    }
                    challengeCount++;
                }
            }
            coords = generateRoom(roomGrid, coords[0], coords[1], 0);
        }
        if (!exitPlaced && coords != null) {
            exitPlaced = true;
            roomGrid[coords[0]][coords[1]] = exitRoom;
            exitCoords = coords;
        }
    }


    /**
     * Populate room grid with the room and adjacent rooms at the coordinate.
     * @param grid the grid to populate
     * @param x the x coordinate in the grid
     * @param y the y coordinate in the grid
     * @param direction the direction of the path being generated
     * @return the next coordinate
     */
    private int[] generateRoom(Room[][] grid, int x, int y, int direction) {
        boolean[] blockedDirections = new boolean[]{false, false, false, false};

        if ((direction == 3 && y >= GRID_HEIGHT / 2 - 1) || y == 0 || grid[x][y - 1] != null) {
            blockedDirections[1] = true;
        }
        if ((direction == 0 && x >= GRID_WIDTH / 2 - 1) || x == GRID_WIDTH - 1
                || grid[x + 1][y] != null) {
            blockedDirections[2] = true;
        }
        if ((direction == 1 && y <= GRID_HEIGHT / 2 + 1) || y == GRID_HEIGHT - 1
                || grid[x][y + 1] != null) {
            blockedDirections[3] = true;
        }
        if ((direction == 2 && x <= GRID_WIDTH / 2 + 1) || x == 0 || grid[x - 1][y] != null) {
            blockedDirections[0] = true;
        }

        if (!(blockedDirections[0] && blockedDirections[1] && blockedDirections[2] && blockedDirections[3])) {
            int newDirection;
            do {
                newDirection = (int) (Math.random() * 4);
            } while (blockedDirections[newDirection]);

            /* 0 - left
             * 1 - up
             * 2 - right
             * 3 - down
             */
            int nx = x;
            int ny = y;
            switch (newDirection) {
                case 0:
                    nx--;
                    break;
                case 1:
                    ny--;
                    break;
                case 2:
                    nx++;
                    break;
                case 3:
                    ny++;
                    break;
                default:
                    break;
            }
            Room r = new Room(roomId++, ROOM_WIDTH, ROOM_HEIGHT, 100, 100, RoomType.EMPTYROOM,
                    generateFloors(ROOM_WIDTH, ROOM_HEIGHT));
            setMonsters(r);
            generateObstacles(r);
            grid[nx][ny] = r;

            return new int[]{nx, ny};
        } else {
            return null;
        }
    }

    /**
     * Generates the floors for a room given the room's width and height. For optimal tiling, height and width should be
     * a multiple of the floor size.
     * @param width Width of the room
     * @param height Height of the room
     * @return A list of the floors
     */
    private ArrayList<Floor> generateFloors(int width, int height) {
        ArrayList<Floor> floors = new ArrayList<>();
        Random rand = new Random();
        int size = DataManager.FLOOR_SIZE;
        double x = 0;
        ArrayList<String> floorsList = new ArrayList<>(DataManager.FLOORS.keySet());
        while (x < width) {
            double y = 0;
            while (y < height) {
                String id = floorsList.get(rand.nextInt(floorsList.size()));
                floors.add(new Floor(id, size, size, x, y));
                y += size;
            }
            x += size;
        }
        return floors;
    }

    /**
     * Adds monsters to a room.
     * @param room the room to add the monsters to
     */
    private void setMonsters(Room room) {
        int min = Vars.i("sv_monsters_min");
        int max = Vars.i("sv_monsters_max");
        int numMonsters = (int) (Math.random() * (max - min + 1)) +  min;
        if (room instanceof ChallengeRoom) {
            numMonsters = 5;
        }
        ArrayList<Monster> monsters = new ArrayList<>();
        ArrayList<Monster> monsterslist = new ArrayList<>();
        for (Map.Entry<String, Monster> e : DataManager.MONSTERS.entrySet()) {
            if (!e.getValue().getId().equalsIgnoreCase("final")) {
                monsterslist.add(e.getValue());
            }
        }
        for (int i = 0; i < numMonsters; i++) {
            int n = (int) (Math.random() * monsterslist.size());
            Difficulty diff = Controller.getDataManager().getDifficulty();
            double modifier = 1;
            if (diff == Difficulty.MEDIUM) {
                modifier = Vars.d("sv_modifier_medium");
            } else if (diff == Difficulty.HARD) {
                modifier = Vars.d("sv_modifier_hard");
            }
            Monster m = monsterslist.get(n).copy(modifier);
            int monsterX = (int) (Math.random() * (room.getWidth() - 39)) + 20;
            int monsterY = (int) (Math.random() * (room.getHeight() - 39)) + 20;
            m.setX(monsterX);
            m.setY(monsterY);
            monsters.add(m);
        }
        room.setMonsters(monsters);
    }

    /**
     * Creates obstacles for a room.
     * @param room Room to create obstacles in
     * @param modifier int used to determine how many obstacles to add to a room
     */
    private void generateObstacles(Room room, int modifier) {
        Random rand = new Random();
        int min = Vars.i("sv_obstacles_min");
        int max = Vars.i("sv_obstacles_max");
        int numObstacles = rand.nextInt(modifier * (max - min) + 1) + modifier * min;
        for (int i = 0; i < numObstacles; i++) {
            //random num for obstacle
            int index = rand.nextInt(DataManager.OBSTACLES.size());
            ArrayList<Obstacle> obstacles = new ArrayList<>(DataManager.OBSTACLES.values());
            Obstacle o = obstacles.get(index).copy();
            int posX;
            int posY;
            boolean validPos;
            do {
                posX = rand.nextInt((room.getWidth() - o.getWidth()) - 99) + 50;
                posY = rand.nextInt((room.getHeight() - o.getHeight()) - 99) + 50;
                validPos = true;
                for (Obstacle oc : room.getObstacles()) {
                    double distX = Math.pow(posX - oc.getX(), 2);
                    double distY = Math.pow(posY - oc.getY(), 2);
                    double dist = Math.sqrt(distX + distY);
                    if (dist < Vars.i("sv_obstacle_gendist")) {
                        validPos = false;
                    }
                }
            } while (!validPos);
            o.setX(posX);
            o.setY(posY);
            room.getObstacles().add(o);
        }
    }

    private void generateObstacles(Room room) {
        generateObstacles(room, 1);
    }

    /**
     * Prints out a representation of the Layout.
     * @param grid the grid to print out.
     */
    private void printGrid(Room[][] grid) {
        for (int i = 0; i < GRID_WIDTH; i++) {
            StringBuilder col = new StringBuilder();
            for (int j = 0; j < GRID_HEIGHT; j++) {
                if (i == GRID_WIDTH / 2 && j == GRID_HEIGHT / 2) {
                    col.append("o ");
                } else if (grid[j][i] != null) {
                    if (grid[j][i].getType().equals(RoomType.EXITROOM)) {
                        col.append("e ");
                    } else if (grid[j][i].getType().equals(RoomType.CHALLENGEROOM)) {
                        col.append(grid[j][i].equals(cr1) ? "1 " : "2 ");
                    } else {
                        col.append("* ");
                    }
                } else {
                    col.append("- ");
                }
            }
            Console.print(col.toString());
        }
        Console.print("");
    }

}
