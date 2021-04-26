package dungeoncrawler.handlers;

import dungeoncrawler.controllers.Controller;
import dungeoncrawler.controllers.DataManager;
import dungeoncrawler.objects.Ammo;
import dungeoncrawler.objects.Ammunition;
import dungeoncrawler.objects.ChallengeRoom;
import dungeoncrawler.objects.Door;
import dungeoncrawler.objects.DoorOrientation;
import dungeoncrawler.objects.DungeonLayout;
import dungeoncrawler.objects.ExitDoor;
import dungeoncrawler.objects.Inventory;
import dungeoncrawler.objects.Item;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.Obstacle;
import dungeoncrawler.objects.RangedWeapon;
import dungeoncrawler.objects.Room;
import dungeoncrawler.objects.RoomType;
import java.util.Random;

/**
 * Class that generates Layout of the Rooms
 * @author Ishaan Guha, Trenton Wong
 * @version 1.0
 */
public class LayoutGenerator {


    /*
    public Room (int height, int width, int startX, int startY,
     Obstacle[] roomObstacles, RoomType roomType) {
        this(height, width, startX, startY,
         roomObstacles, null, null, null, null, roomType);
    }
     */

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
        Item[] items = DataManager.ITEMS;
        cr1Rewards = new Inventory(2, 5);
        RangedWeapon rl = ((RangedWeapon) items[6]).copy();
        Ammo ammo = new Ammo(2, 50, DataManager.PROJECTILES[0].copy());
        ammo.setRemaining(2);
        ammo.setBackupRemaining(20);
        rl.setAmmo(ammo);

        cr1Rewards.add(items[2], 1);
        cr1Rewards.add(items[3], 1);
        cr1Rewards.add(items[5], 2);
        cr1Rewards.add(rl, 1);

        cr2Rewards = new Inventory(2, 5);
        cr2Rewards.add(items[0], 3);
        cr2Rewards.add(items[1], 2);
        cr2Rewards.add(items[2], 1);
        cr2Rewards.add(items[3], 2);
        cr2Rewards.add(items[4], 1);
        cr2Rewards.add(items[5], 3);
        Ammunition rockets = (Ammunition) items[7].copy();
        rockets.setAmount(20);
        cr2Rewards.add(rockets);
    }

    private void reset() {
        startRoom = new Room(ROOM_HEIGHT, ROOM_WIDTH, (int) ((ROOM_WIDTH
                - GameSettings.PLAYER_WIDTH) / 2.0), (int) (ROOM_HEIGHT / 2.0
                - GameSettings.PLAYER_HEIGHT), RoomType.STARTROOM);
        startRoom.setMonsters(new Monster[0]);
        generateObstacles(startRoom);

        int exitWidth = 832;
        int exitHeight = 444;

        exitRoom = new Room(exitHeight, exitWidth, 100, 100, RoomType.EXITROOM);
        generateObstacles(exitRoom, 4);

        Monster boss = DataManager.FINALBOSS;
        boss.setX(exitWidth / 2 - boss.getWidth() / 2);
        boss.setY(exitHeight - boss.getHeight() - 5);
        exitRoom.setMonsters(new Monster[]{DataManager.FINALBOSS});
        ExitDoor ed = new ExitDoor((exitWidth - DOORTOP_WIDTH) / 2,
                exitHeight - 1, DOORTOP_WIDTH, DOORTOP_HEIGHT);
        exitRoom.setTopDoor(ed);

        cr1 = new ChallengeRoom(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, cr1Rewards);
        cr2 = new ChallengeRoom(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, cr2Rewards);

        setMonsters(cr1);
        setMonsters(cr2);

        challengeCount = 0;
        exitPlaced = false;
        exitCoords = new int[2];
        roomGrid = new Room[GRID_WIDTH][GRID_HEIGHT];
    }

    /**
     * generate the layout of the rooms
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

        return new DungeonLayout(roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2], exitRoom, roomGrid);
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
        Room r = new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, RoomType.EMPTYROOM);
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
     * Populate room grid with the room and adjacent rooms at the coordinate
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
            Room r = new Room(ROOM_HEIGHT, ROOM_WIDTH,
                    100, 100, RoomType.EMPTYROOM);
            setMonsters(r);
            generateObstacles(r);
            grid[nx][ny] = r;

            return new int[]{nx, ny};
        } else {
            return null;
        }
    }

    /**
     * Adds monsters to a room
     * @param room the room to add the monsters to
     */
    private void setMonsters(Room room) {
        int numMonsters =
                (int) (Math.random()
                        * (GameSettings.MAX_MONSTERS - GameSettings.MIN_MONSTERS + 1))
                        +  GameSettings.MIN_MONSTERS;
        if (room instanceof ChallengeRoom) {
            numMonsters = 5;
        }
        Monster[] monsters = new Monster[numMonsters];
        for (int i = 0; i < monsters.length; i++) {
            int n = (int) (Math.random() * 3);
            Difficulty diff = Controller.getDataManager().getDifficulty();
            double modifier = 1;
            if (diff == Difficulty.MEDIUM) {
                modifier = GameSettings.MODIFIER_MEDIUM;
            } else if (diff == Difficulty.HARD) {
                modifier = GameSettings.MODIFIER_HARD;
            }
            monsters[i] = new Monster(Controller.getDataManager().MONSTERS[n], modifier);

            int monsterX = (int) (Math.random() * (room.getWidth() - 39)) + 20;
            int monsterY = (int) (Math.random() * (room.getHeight() - 39)) + 20;
            monsters[i].setX(monsterX);
            monsters[i].setY(monsterY);
        }
        room.setMonsters(monsters);
    }

    private void generateObstacles(Room room, int modifier) {
        Random rand = new Random();
        int numObstacles = rand.nextInt(modifier * (GameSettings.OBSTACLES_MAX
                - GameSettings.OBSTACLES_MIN) + 1) + modifier * GameSettings.OBSTACLES_MIN;
        for (int i = 0; i < numObstacles; i++) {
            //random num for obstacle
            int index = rand.nextInt(DataManager.OBSTACLES.length);
            Obstacle o = DataManager.OBSTACLES[index].copy();
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
                    if (dist < GameSettings.OBSTACLES_DISTANCE) {
                        validPos = false;
                    }
                }
            } while (!validPos);
            System.out.println("Obstacle " + posX + " " + posY);
            o.setX(posX);
            o.setY(posY);
            room.getObstacles().add(o);
        }
    }

    private void generateObstacles(Room room) {
        generateObstacles(room, 1);
    }

    /**
     * Prints out a representation of the Layout
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
                    col += "_ ";
                }
            }
            System.out.println(col);
        }
        System.out.println();
    }

}
