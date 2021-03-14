package dungeoncrawler;

public class LayoutGenerator {


    /*
    public Room (int height, int width, int startX, int startY, Obstacle[] roomObstacles, RoomType roomType) {
        this(height, width, startX, startY, roomObstacles, null, null, null, null, roomType);
    }
     */

    private static final int GRID_WIDTH = 15;
    private static final int GRID_HEIGHT = 15;

    public LayoutGenerator() {

    }

    public static DungeonLayout generateLayout() {
        Room[][] roomGrid = new Room[GRID_WIDTH][GRID_HEIGHT];

        //starting room
        roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2] = new Room(400, 400, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);

        int[] coords;

        // up path
        roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2 + 1] = new Room(400, 400, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
        coords = generateRoom(roomGrid, GRID_WIDTH / 2, GRID_HEIGHT / 2 + 1);
        int upPath = (int)(Math.random() * 7) + 4;
        for (int i = 0; i < upPath - 1; i++) {
            coords = generateRoom(roomGrid, coords[0], coords[1]);
        }
        printGrid(roomGrid);

        // right path
        roomGrid[GRID_WIDTH / 2 + 1][GRID_HEIGHT / 2] = new Room(400, 400, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
        coords = generateRoom(roomGrid, GRID_WIDTH / 2 + 1, GRID_HEIGHT / 2);
        int rightPath = (int)(Math.random() * 7) + 4;
        for (int i = 0; i < rightPath - 1; i++) {
            coords = generateRoom(roomGrid, coords[0], coords[1]);
        }
        printGrid(roomGrid);

        // down path
        roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2 - 1] = new Room(400, 400, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
        coords = generateRoom(roomGrid, GRID_WIDTH / 2, GRID_HEIGHT / 2 - 1);
        int downPath = (int)(Math.random() * 7) + 4;
        for (int i = 0; i < downPath - 1; i++) {
            coords = generateRoom(roomGrid, coords[0], coords[1]);
        }


        // left path
        roomGrid[GRID_WIDTH / 2 - 1][GRID_HEIGHT / 2] = new Room(400, 400, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
        coords = generateRoom(roomGrid, GRID_WIDTH / 2 - 1, GRID_HEIGHT / 2);
        System.out.println(coords);
        int leftPath = (int)(Math.random() * 7) + 4;
        for (int i = 0; i < leftPath - 1; i++) {
           coords = generateRoom(roomGrid, coords[0], coords[1]);
        }

        /*
        int x, int y, int w, int h, Room r, DoorOrientation d
         */

        // create doors
        for (int i = 1; i < GRID_WIDTH - 1; i++) {
            for (int j = 1; j < GRID_HEIGHT - 1; j++) {
                if (roomGrid[i + 1][j] != null) {
                    roomGrid[i][j].setRightDoor(new Door(480,245,20,10, roomGrid[i + 1][j], DoorOrientation.RIGHT));
                }
                if (roomGrid[i - 1][j] != null) {
                    roomGrid[i][j].setLeftDoor(new Door(100,245,20,10, roomGrid[i - 1][j], DoorOrientation.LEFT));
                }
                if (roomGrid[i][j + 1] != null) {
                    roomGrid[i][j].setBottomDoor(new Door(200,500,20,10, roomGrid[i][j + 1], DoorOrientation.BOTTOM));
                }
                if (roomGrid[i][j - 1] != null) {
                    roomGrid[i][j].setTopDoor(new Door(200,300,20,10, roomGrid[i][j - 1], DoorOrientation.TOP));
                }
            }
        }
        return new DungeonLayout(roomGrid[GRID_WIDTH / 2][GRID_HEIGHT / 2], null);
    }

    private static int[] generateRoom(Room[][] grid, int x, int y) {
        boolean[] blockedDirections = new boolean[]{false, false, false, false};

        if (y == 0 || grid[x][y - 1] != null) {
            blockedDirections[0] = true;
        }
        if (x == GRID_WIDTH - 1 || grid[x + 1][y] != null) {
            blockedDirections[1] = true;
        }
        if (y == GRID_HEIGHT - 1 || grid[x][y + 1] != null) {
            blockedDirections[2] = true;
        }
        if (x == 0 || grid[x - 1][y] != null) {
            blockedDirections[3] = true;
        }

        int newDirection;
        if (!(blockedDirections[0] && blockedDirections[1] && blockedDirections[2] && blockedDirections[3])) {
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
                    grid[x][y - 1] = new Room(400, 400, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
                    return new int[]{x, y - 1};
                case 1:
                    grid[x + 1][y] = new Room(400, 400, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
                    return new int[]{x + 1, y};
                case 2:
                    grid[x][y + 1] = new Room(400, 400, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
                    return new int[]{x, y + 1};
                default:
                    grid[x - 1][y] = new Room(400, 400, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
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
                } else if (grid[i][j] != null) {
                    col += "* ";
                } else {
                    col += "_ ";
                }
            }
            System.out.println(col);
        }
        System.out.println();
    }

}
