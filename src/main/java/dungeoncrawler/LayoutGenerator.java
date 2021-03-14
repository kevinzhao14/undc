package dungeoncrawler;

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

    public static final int ROOM_HEIGHT = 200;
    public static final int ROOM_WIDTH = (int) Math.round(ROOM_HEIGHT * 2.1780303);

    public static final int DOOR_HEIGHT = (int) Math.round(ROOM_HEIGHT * 0.399239544);
    public static final int DOOR_WIDTH = (int) Math.round(DOOR_HEIGHT / 3.24242424);

    private static final int pathMin = 6;
    private static final int pathMax = 10;

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
                new Obstacle[5], RoomType.EXITROOM);

        //starting room
        roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2] =
                new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100,
                        new Obstacle[5], RoomType.EMPTYROOM);

        int[] coords;

        // up path
        roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2 + 1] =
                new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100,
                        new Obstacle[5], RoomType.EMPTYROOM);
        coords = generateRoom(roomGrid, GRID_WIDTH / 2, GRID_HEIGHT / 2 + 1, 0);
        int upPath = (int) (Math.random() * 7) + 4;
        for (int i = 0; i < upPath - 1; i++) {
            if (coords == null) {
                break;
            }
            coords = generateRoom(roomGrid, coords[0], coords[1], 0);
        }
        if (upPath >= 6 && !exitPlaced && coords != null) {
            exitPlaced = true;
            roomGrid[coords[0]][coords[1]] = exitRoom;
        }
        System.out.println("top");
        printGrid(roomGrid);

        // right path

        roomGrid[GRID_WIDTH / 2 + 1][GRID_HEIGHT / 2] =
                new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100,
                        new Obstacle[5], RoomType.EMPTYROOM);
        coords = generateRoom(roomGrid, GRID_WIDTH / 2 + 1, GRID_HEIGHT / 2, 1);
        int rightPath = (int) (Math.random() * 7) + 4;
        for (int i = 0; i < rightPath - 1; i++) {
            if (coords == null) {
                break;
            }
            coords = generateRoom(roomGrid, coords[0], coords[1], 1);
        }
        if (rightPath >= 6 && !exitPlaced && coords != null) {
            exitPlaced = true;
            roomGrid[coords[0]][coords[1]] = exitRoom;
        }
        System.out.println("right");
        printGrid(roomGrid);

        // down path

        roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2 - 1] =
                new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100,
                        new Obstacle[5], RoomType.EMPTYROOM);
        coords = generateRoom(roomGrid, GRID_WIDTH / 2, GRID_HEIGHT / 2 - 1, 2);
        int downPath = (int) (Math.random() * 7) + 4;

        for (int i = 0; i < downPath - 1; i++) {
            if (coords == null) {
                break;
            }
            coords = generateRoom(roomGrid, coords[0], coords[1], 2);
        }
        if (downPath >= 6 && !exitPlaced && coords != null) {
            exitPlaced = true;
            roomGrid[coords[0]][coords[1]] = exitRoom;
        }
        System.out.println("bottom");
        printGrid(roomGrid);

        // left path

        roomGrid[GRID_WIDTH / 2 - 1][GRID_HEIGHT / 2] =
                new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100,
                        new Obstacle[5], RoomType.EMPTYROOM);
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
            coords = generateRoom(roomGrid, coords[0], coords[1], 3);
        }
        if (leftPath >= 6 && !exitPlaced && coords != null) {
            exitPlaced = true;
            roomGrid[coords[0]][coords[1]] = exitRoom;
        }
        System.out.println("left");
        printGrid(roomGrid);

        /*
        int x, int y, int w, int h, Room r, DoorOrientation d
         */


        // create doors
        for (int i = 1; i < GRID_WIDTH - 1; i++) {
            for (int j = 1; j < GRID_HEIGHT - 1; j++) {
                if (roomGrid[i][j] != null) {
                    if (roomGrid[i + 1][j] != null) {
                        roomGrid[i][j].setRightDoor(new Door(ROOM_WIDTH - 1, (ROOM_HEIGHT - DOOR_HEIGHT) / 2, DOOR_WIDTH, DOOR_HEIGHT,
                                roomGrid[i + 1][j], DoorOrientation.RIGHT));
                    }
                    if (roomGrid[i - 1][j] != null) {
                        roomGrid[i][j].setLeftDoor(
                                new Door(-DOOR_WIDTH + 1, (ROOM_HEIGHT - DOOR_HEIGHT) / 2, DOOR_WIDTH, DOOR_HEIGHT,
                                        roomGrid[i - 1][j], DoorOrientation.LEFT));
                    }
                    if (roomGrid[i][j + 1] != null) {
                        roomGrid[i][j].setBottomDoor(
                                new Door((ROOM_WIDTH - DOOR_HEIGHT) / 2, -DOOR_WIDTH + 1, DOOR_HEIGHT, DOOR_WIDTH,
                                        roomGrid[i][j + 1], DoorOrientation.BOTTOM));
                    }
                    if (roomGrid[i][j - 1] != null) {
                        roomGrid[i][j].setTopDoor(
                                new Door((ROOM_WIDTH - DOOR_HEIGHT) / 2, ROOM_HEIGHT - 1, DOOR_HEIGHT, DOOR_WIDTH,
                                        roomGrid[i][j - 1], DoorOrientation.TOP));
                    }
                }
            }
        }

        return new DungeonLayout(roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2], exitRoom);
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
                grid[x][y - 1] = new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
                return new int[]{x, y - 1};
            case 1:
                grid[x + 1][y] = new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
                return new int[]{x + 1, y};
            case 2:
                grid[x][y + 1] = new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
                return new int[]{x, y + 1};
            default:
                grid[x - 1][y] = new Room(ROOM_HEIGHT, ROOM_WIDTH, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
                return new int[]{x - 1, y};
            }
        } else {
            System.out.println("No available directions");
            return null;
        }
    }

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
