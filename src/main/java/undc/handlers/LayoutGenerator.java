package undc.handlers;

import undc.controllers.Console;
import undc.controllers.Controller;
import undc.controllers.DataManager;
import undc.objects.WeaponAmmo;
import undc.objects.Ammunition;
import undc.objects.ChallengeRoom;
import undc.objects.Door;
import undc.objects.DoorOrientation;
import undc.objects.DungeonLayout;
import undc.objects.ExitDoor;
import undc.objects.Inventory;
import undc.objects.Item;
import undc.objects.Monster;
import undc.objects.Obstacle;
import undc.objects.RangedWeapon;
import undc.objects.Room;
import undc.objects.RoomType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Class that generates Layout of the Rooms.
 */
public class LayoutGenerator {
    private static final int GRID_WIDTH = 15;
    private static final int GRID_HEIGHT = 15;

    public static final int ROOM_HEIGHT = 264;
    public static final int ROOM_WIDTH = (int) Math.round(ROOM_HEIGHT * 2.18181818);

    public static final int DOOR_HEIGHT = (int) Math.round(ROOM_HEIGHT * 0.40530303);
    public static final int DOOR_WIDTH = (int) Math.round(DOOR_HEIGHT * 0.308411215);

    public static final int DOORTOP_HEIGHT = (int) Math.round(ROOM_HEIGHT * 0.246212121);
    public static final int DOORTOP_WIDTH = (int) Math.round(DOORTOP_HEIGHT * 1.29230769);

    public static final int DOORBOTTOM_HEIGHT = (int) Math.round(ROOM_HEIGHT * 0.125);
    public static final int DOORBOTTOM_WIDTH = (int) Math.round(DOORBOTTOM_HEIGHT * 1.75757576);

    private static final int PATH_MIN = 6;
    private static final int PATH_MAX = 10;

    private static final double CHALLENGE_ODDS = 0.25;

    private Room startRoom;
    private Room exitRoom;

    private Inventory cr1Rewards;
    private Inventory cr2Rewards;
    private ChallengeRoom cr1;
    private ChallengeRoom cr2;

    private int challengeCount;
    private boolean exitPlaced;
    private int[] exitCoords;
    private Room[][] roomGrid;

    public LayoutGenerator() {
        //set challenge rooms
        HashMap<Integer, Item> items = DataManager.ITEMS;
        cr1Rewards = new Inventory(2, 5);
        RangedWeapon rl = ((RangedWeapon) items.get(9)).copy();
        WeaponAmmo weaponAmmo = new WeaponAmmo(2, 50, DataManager.PROJECTILES.get(0));
        weaponAmmo.setRemaining(2);
        weaponAmmo.setBackupRemaining(20);
        rl.setAmmo(weaponAmmo);

        cr1Rewards.add(items.get(5), 1); // large health potion
        cr1Rewards.add(items.get(6), 1); // attack potion
        cr1Rewards.add(items.get(8), 2); // bomb
        cr1Rewards.add(rl, 1); //rocket launcher

        cr2Rewards = new Inventory(2, 5);
        cr2Rewards.add(items.get(3), 3); // small health potion
        cr2Rewards.add(items.get(4), 2); // medium health potion
        cr2Rewards.add(items.get(5), 1); // large health potion
        cr2Rewards.add(items.get(6), 2); // attack potion
        cr2Rewards.add(items.get(7), 1); // dagger
        cr2Rewards.add(items.get(8), 3); // bomb
        Ammunition rockets = (Ammunition) items.get(10).copy(); // ammunition
        rockets.setAmount(20);
        cr2Rewards.add(rockets);
    }

    private void reset() {
        startRoom = new Room(ROOM_WIDTH, ROOM_HEIGHT, (int) ((ROOM_WIDTH
                - Vars.i("sv_player_width")) / 2.0), (int) (ROOM_HEIGHT / 2.0
                - Vars.i("sv_player_height")), RoomType.STARTROOM);
        startRoom.setMonsters(new ArrayList<>());
        generateObstacles(startRoom);

        int exitWidth = 832;
        int exitHeight = 444;

        exitRoom = new Room(exitWidth, exitHeight, 100, 100, RoomType.EXITROOM);
        generateObstacles(exitRoom, 4);

        Monster boss = DataManager.getFinalBoss();
        boss.setX(exitWidth / 2.0 - boss.getWidth() / 2);
        boss.setY(exitHeight - boss.getHeight() - 5);
        exitRoom.setMonsters(new ArrayList<>());
        exitRoom.getMonsters().add(DataManager.getFinalBoss());
        ExitDoor ed = new ExitDoor((exitWidth - DOORTOP_WIDTH) / 2,
                exitHeight - 1, DOORTOP_WIDTH, DOORTOP_HEIGHT);
        exitRoom.setTopDoor(ed);

        cr1 = new ChallengeRoom(ROOM_WIDTH, ROOM_HEIGHT, 100, 100, cr1Rewards);
        cr2 = new ChallengeRoom(ROOM_WIDTH, ROOM_HEIGHT, 100, 100, cr2Rewards);

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
                        roomGrid[i][j].setRightDoor(
                                new Door(ROOM_WIDTH - 1, (ROOM_HEIGHT - DOOR_HEIGHT) / 2,
                                        DOOR_WIDTH, DOOR_HEIGHT / 2,
                                roomGrid[i + 1][j], DoorOrientation.RIGHT));
                    }
                    if (roomGrid[i - 1][j] != null) {
                        roomGrid[i][j].setLeftDoor(
                                new Door(-DOOR_WIDTH + 1, (ROOM_HEIGHT - DOOR_HEIGHT) / 2,
                                        DOOR_WIDTH, DOOR_HEIGHT / 2,
                                        roomGrid[i - 1][j], DoorOrientation.LEFT));
                    }
                    if (roomGrid[i][j + 1] != null) {
                        roomGrid[i][j].setBottomDoor(
                                new Door((ROOM_WIDTH - DOORBOTTOM_WIDTH) / 2,
                                        -DOORBOTTOM_HEIGHT + 1,
                                        DOORBOTTOM_WIDTH, DOORBOTTOM_HEIGHT,
                                        roomGrid[i][j + 1], DoorOrientation.BOTTOM));
                    }
                    if (roomGrid[i][j - 1] != null) {
                        roomGrid[i][j].setTopDoor(
                                new Door((ROOM_WIDTH - DOORTOP_WIDTH) / 2, ROOM_HEIGHT - 1,
                                        DOORTOP_WIDTH, DOORTOP_HEIGHT,
                                        roomGrid[i][j - 1], DoorOrientation.TOP));
                    }
                }
            }
        }

        return new DungeonLayout(startRoom, exitRoom, roomGrid);
    }

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
        Room r = new Room(ROOM_WIDTH, ROOM_HEIGHT, 100, 100, RoomType.EMPTYROOM);
        setMonsters(r);
        generateObstacles(r);
        roomGrid[x][y] = r;

        int[] coords = generateRoom(roomGrid, x, y, 0);
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

        if (!(blockedDirections[0] && blockedDirections[1]
                && blockedDirections[2] && blockedDirections[3])) {
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
            Room r = new Room(ROOM_WIDTH, ROOM_HEIGHT,100, 100, RoomType.EMPTYROOM);
            setMonsters(r);
            generateObstacles(r);
            grid[nx][ny] = r;

            return new int[]{nx, ny};
        } else {
            return null;
        }
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
        for (int i = 0; i < numMonsters; i++) {
            int n = (int) (Math.random() * 3);
            Difficulty diff = Controller.getDataManager().getDifficulty();
            double modifier = 1;
            if (diff == Difficulty.MEDIUM) {
                modifier = Vars.d("sv_modifier_medium");
            } else if (diff == Difficulty.HARD) {
                modifier = Vars.d("sv_modifier_hard");
            }
            Monster m = DataManager.MONSTERS.get(n).copy(modifier);
            int monsterX = (int) (Math.random() * (room.getWidth() - 39)) + 20;
            int monsterY = (int) (Math.random() * (room.getHeight() - 39)) + 20;
            m.setX(monsterX);
            m.setY(monsterY);
            monsters.add(m);
        }
        room.setMonsters(monsters);
    }

    private void generateObstacles(Room room, int modifier) {
        Random rand = new Random();
        int min = Vars.i("sv_obstacles_min");
        int max = Vars.i("sv_obstacles_max");
        int numObstacles = rand.nextInt(modifier * (max - min) + 1) + modifier * min;
        for (int i = 0; i < numObstacles; i++) {
            //random num for obstacle
            int index = rand.nextInt(DataManager.OBSTACLES.size());
            Obstacle o = DataManager.OBSTACLES.get(index).copy();
            int posX;
            int posY;
            boolean validPos;
            do {
                posX = rand.nextInt((int) (room.getWidth() - o.getWidth()) - 99) + 50;
                posY = rand.nextInt((int) (room.getHeight() - o.getHeight()) - 99) + 50;
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
            String col = "";
            for (int j = 0; j < GRID_HEIGHT; j++) {
                if (i == GRID_WIDTH / 2 && j == GRID_HEIGHT / 2) {
                    col += "o ";
                } else if (grid[j][i] != null) {
                    if (grid[j][i].getType().equals(RoomType.EXITROOM)) {
                        col += "e ";
                    } else if (grid[j][i].getType().equals(RoomType.CHALLENGEROOM)) {
                        col += grid[j][i].equals(cr1) ? "1 " : "2 ";
                    } else {
                        col += "* ";
                    }
                } else {
                    col += "- ";
                }
            }
            Console.print(col);
        }
        Console.print("");
    }

}
