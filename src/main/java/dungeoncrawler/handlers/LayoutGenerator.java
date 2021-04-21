package dungeoncrawler.handlers;

import dungeoncrawler.controllers.Controller;
import dungeoncrawler.objects.ChallengeRoom;
import dungeoncrawler.objects.Door;
import dungeoncrawler.objects.DoorOrientation;
import dungeoncrawler.objects.DroppedItem;
import dungeoncrawler.objects.DungeonLayout;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.Obstacle;
import dungeoncrawler.objects.Potion;
import dungeoncrawler.objects.PotionType;
import dungeoncrawler.objects.RangedWeapon;
import dungeoncrawler.objects.Room;
import dungeoncrawler.objects.RoomType;
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

    public LayoutGenerator() {

    }

    /**
     * generate the layout of the rooms
     * @return the layout
     */
    public static DungeonLayout generateLayout() {
        Room[][] roomGrid = new Room[GRID_WIDTH][GRID_HEIGHT];
        boolean exitPlaced = false;
        Room exitRoom = new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100,
                new Obstacle[0], RoomType.EXITROOM);
        setMonsters(exitRoom);
        int[] exitCoords = new int[]{GRID_WIDTH / 2, GRID_HEIGHT / 2};
        //starting room
        Room startRoom = new Room(ROOM_HEIGHT, ROOM_WIDTH, (int) ((ROOM_WIDTH
                - GameSettings.PLAYER_WIDTH) / 2.0), (int) (ROOM_HEIGHT / 2.0
                - GameSettings.PLAYER_HEIGHT), new Obstacle[0], RoomType.STARTROOM);
        startRoom.setMonsters(new Monster[0]);
        roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2] = startRoom;
        int[] coords;
        // up path
        int challengeCount = 0;
        DroppedItem[] firstRoomRewards = {new DroppedItem(new RangedWeapon("Rocket Launcher", "weapons/rocketlauncher.png", 4, false, 1, 1), ROOM_HEIGHT / 2, ROOM_WIDTH / 2, 20, 20)};
        DroppedItem[] secondRoomRewards = {new DroppedItem(new Potion("Large Health Potion", "items/health-potion-large.png", 3, true,
                PotionType.HEALTH, 100), ROOM_HEIGHT / 2, ROOM_WIDTH / 2, 20, 20)};
        roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2 + 1] =
                new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100,
                        new Obstacle[5], RoomType.EMPTYROOM);
        setMonsters(roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2 + 1]);
        coords = generateRoom(roomGrid, GRID_WIDTH / 2, GRID_HEIGHT / 2 + 1, 0);
        int upPath = (int) (Math.random() * 7) + 4;
        for (int i = 0; i < upPath - 1; i++) {
            if (coords == null) {
                break;
            }
            if (challengeCount < 2) {
                if (Math.random() < 0.5) {
                    if (challengeCount == 0) {
                        roomGrid[coords[0]][coords[1]] = new ChallengeRoom(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, new Obstacle[5], RoomType.EMPTYROOM, firstRoomRewards);
                    } else {
                        roomGrid[coords[0]][coords[1]] = new ChallengeRoom(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, new Obstacle[5], RoomType.EMPTYROOM, secondRoomRewards);
                    }
                    challengeCount++;
                }
            }
            coords = generateRoom(roomGrid, coords[0], coords[1], 0);
        }
        if (upPath >= 6 && !exitPlaced && coords != null) {
            exitPlaced = true;
            roomGrid[coords[0]][coords[1]] = exitRoom;
            exitCoords = coords;
        }

        // right path

        roomGrid[GRID_WIDTH / 2 + 1][GRID_HEIGHT / 2] =
                new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100,
                        new Obstacle[5], RoomType.EMPTYROOM);
        setMonsters(roomGrid[GRID_WIDTH / 2 + 1][GRID_HEIGHT / 2]);
        coords = generateRoom(roomGrid, GRID_WIDTH / 2 + 1, GRID_HEIGHT / 2, 1);
        int rightPath = (int) (Math.random() * 7) + 4;
        for (int i = 0; i < rightPath - 1; i++) {
            if (coords == null) {
                break;
            }
            if (challengeCount < 2) {
                if (Math.random() < 0.5) {
                    if (challengeCount == 0) {
                        roomGrid[coords[0]][coords[1]] = new ChallengeRoom(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, new Obstacle[5], RoomType.EMPTYROOM, firstRoomRewards);
                    } else {
                        roomGrid[coords[0]][coords[1]] = new ChallengeRoom(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, new Obstacle[5], RoomType.EMPTYROOM, secondRoomRewards);
                    }
                    challengeCount++;
                }
            }
            coords = generateRoom(roomGrid, coords[0], coords[1], 1);
        }
        if (rightPath >= 6 && !exitPlaced && coords != null) {
            exitPlaced = true;
            roomGrid[coords[0]][coords[1]] = exitRoom;
            exitCoords = coords;

        }

        // down path

        roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2 - 1] =
                new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100,
                        new Obstacle[5], RoomType.EMPTYROOM);
        setMonsters(roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2 - 1]);
        coords = generateRoom(roomGrid, GRID_WIDTH / 2, GRID_HEIGHT / 2 - 1, 2);
        int downPath = (int) (Math.random() * 7) + 4;

        for (int i = 0; i < downPath - 1; i++) {
            if (coords == null) {
                break;
            }
            if (challengeCount < 2) {
                if (Math.random() < 0.5) {
                    if (challengeCount == 0) {
                        roomGrid[coords[0]][coords[1]] = new ChallengeRoom(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, new Obstacle[5], RoomType.EMPTYROOM, firstRoomRewards);
                    } else {
                        roomGrid[coords[0]][coords[1]] = new ChallengeRoom(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, new Obstacle[5], RoomType.EMPTYROOM, secondRoomRewards);
                    }
                    challengeCount++;
                }
            }
            coords = generateRoom(roomGrid, coords[0], coords[1], 2);
        }
        if (downPath >= 6 && !exitPlaced && coords != null) {
            exitPlaced = true;
            roomGrid[coords[0]][coords[1]] = exitRoom;
            exitCoords = coords;
        }

        // left path

        roomGrid[GRID_WIDTH / 2 - 1][GRID_HEIGHT / 2] =
                new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100,
                        new Obstacle[5], RoomType.EMPTYROOM);
        setMonsters(roomGrid[GRID_WIDTH / 2 - 1][GRID_HEIGHT / 2]);
        coords = generateRoom(roomGrid, GRID_WIDTH / 2 - 1, GRID_HEIGHT / 2, 3);

        int leftPath;
        if (upPath < 6 && rightPath < 6 && downPath < 6) {
            leftPath = 7;
        } else {
            leftPath = (int) (Math.random() * 7) + 4;
        }
        for (int i = 0; i < leftPath - 1; i++) {
            if (coords == null) {
                break;
            }
            if (challengeCount < 2) {
                    if (challengeCount == 0) {
                        roomGrid[coords[0]][coords[1]] = new ChallengeRoom(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, new Obstacle[5], RoomType.EMPTYROOM, firstRoomRewards);
                    } else {
                        roomGrid[coords[0]][coords[1]] = new ChallengeRoom(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, new Obstacle[5], RoomType.EMPTYROOM, secondRoomRewards);
                    }
                    challengeCount++;
            }
            coords = generateRoom(roomGrid, coords[0], coords[1], 3);
        }
        if (leftPath >= 6 && !exitPlaced && coords != null) {
            exitPlaced = true;
            roomGrid[coords[0]][coords[1]] = exitRoom;
            exitCoords = coords;
        }



        //check exit distance
        if (!exitPlaced || (Math.abs(exitCoords[0] - GRID_WIDTH / 2) + Math.abs(exitCoords[1]
                - GRID_HEIGHT / 2)) < 6) {
            return generateLayout();
        }
        //create challenge rooms
        int randX = (int) (Math.random() * GRID_WIDTH);
        int randY = (int) (Math.random() * GRID_HEIGHT);

        printGrid(roomGrid);


        /*
        int x, int y, int w, int h, Room r, DoorOrientation d
         */


        // create doors
        for (int i = 1; i < GRID_WIDTH - 1; i++) {
            for (int j = 1; j < GRID_HEIGHT - 1; j++) {
                if (roomGrid[i][j] != null) {
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


    /**
     * Populate room grid with the room and adjacent rooms at the coordinate
     * @param grid the grid to populate
     * @param x the x coordinate in the grid
     * @param y the y coordinate in the grid
     * @param direction the direction of the path being generated
     * @return the next coordinate
     */
    private static int[] generateRoom(Room[][] grid, int x, int y, int direction) {
        boolean[] blockedDirections = new boolean[]{false, false, false, false};

        if ((direction == 2 && y >= GRID_HEIGHT / 2 - 1) || y == 0 || grid[x][y - 1] != null) {
            blockedDirections[0] = true;
        }
        if ((direction == 3 && x >= GRID_WIDTH / 2 - 1) || x == GRID_WIDTH - 1
                || grid[x + 1][y] != null) {
            blockedDirections[1] = true;
        }
        if ((direction == 0 && y <= GRID_HEIGHT / 2 + 1) || y == GRID_HEIGHT - 1
                || grid[x][y + 1] != null) {
            blockedDirections[2] = true;
        }
        if ((direction == 1 && x <= GRID_WIDTH / 2 + 1) || x == 0 || grid[x - 1][y] != null) {
            blockedDirections[3] = true;
        }

        int newDirection;
        if (!(blockedDirections[0] && blockedDirections[1]
                && blockedDirections[2]
                && blockedDirections[3])) {
            do {
                newDirection = (int) (Math.random() * 4);
            } while (blockedDirections[newDirection]);

            /* 0 - up
             * 1 - right
             * 2 - down
             * 3 - left
             */
            switch (newDirection) {
            case 0:
                grid[x][y - 1] = new Room(ROOM_HEIGHT, ROOM_WIDTH,
                        100, 100, new Obstacle[5], RoomType.EMPTYROOM);
                setMonsters(grid[x][y - 1]);
                return new int[]{x, y - 1};
            case 1:
                grid[x + 1][y] = new Room(ROOM_HEIGHT, ROOM_WIDTH,
                        100, 100, new Obstacle[5], RoomType.EMPTYROOM);
                setMonsters(grid[x + 1][y]);
                return new int[]{x + 1, y};
            case 2:
                grid[x][y + 1] = new Room(ROOM_HEIGHT, ROOM_WIDTH,
                        100, 100, new Obstacle[5], RoomType.EMPTYROOM);
                setMonsters(grid[x][y + 1]);
                return new int[]{x, y + 1};
            default:
                grid[x - 1][y] = new Room(ROOM_HEIGHT, ROOM_WIDTH,
                        100, 100, new Obstacle[5], RoomType.EMPTYROOM);
                setMonsters(grid[x - 1][y]);
                return new int[]{x - 1, y};
            }
        } else {
            return null;
        }
    }

    /**
     * Adds monsters to a room
     * @param room the room to add the monsters to
     */
    public static void setMonsters(Room room) {
        int numMonsters =
                (int) (Math.random()
                        * (GameSettings.MAX_MONSTERS - GameSettings.MIN_MONSTERS + 1))
                        +  GameSettings.MIN_MONSTERS;
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

    /**
     * Prints out a representation of the Layout
     * @param grid the grid to print out.
     */
    private static void printGrid(Room[][] grid) {
        for (int i = 0; i < GRID_WIDTH; i++) {
            String col = "";
            for (int j = 0; j < GRID_HEIGHT; j++) {
                if (i == GRID_WIDTH / 2 && j == GRID_HEIGHT / 2) {
                    col += "o ";
                } else if (grid[j][i] != null) {
                    if (grid[j][i].getType().equals(RoomType.EXITROOM)) {
                        col += "e ";
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
