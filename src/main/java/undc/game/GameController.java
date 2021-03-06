package undc.game;

import javafx.event.Event;
import javafx.scene.Scene;
import org.json.JSONArray;
import org.json.JSONObject;
import undc.command.Console;
import undc.entity.Entity;
import undc.game.calc.Vector;
import undc.game.objects.Door;
import undc.game.objects.DroppedItem;
import undc.game.objects.ExitDoor;
import undc.game.objects.GameObject;
import undc.game.objects.Obstacle;
import undc.game.objects.ObstacleItem;
import undc.game.objects.ObstacleType;
import undc.game.objects.ShotProjectile;
import undc.general.Controller;
import undc.command.DataManager;
import undc.graphics.Camera;
import undc.graphics.GameScreen;
import javafx.application.Platform;
import javafx.scene.image.Image;
import undc.general.Audio;
import undc.general.Config;
import undc.command.Vars;
import undc.graphics.SpriteGroup;
import undc.items.Ammunition;
import undc.items.Bomb;
import undc.game.calc.Collision;
import undc.game.calc.Coords;
import undc.game.calc.Direction;
import undc.entity.Dummy;
import undc.game.calc.Equation;
import undc.inventory.GraphicalInventory;
import undc.general.Interactable;
import undc.inventory.InventoryItem;
import undc.items.Item;
import undc.entity.Monster;
import undc.entity.MonsterType;
import undc.game.calc.Move;
import undc.entity.Player;
import undc.items.RangedWeapon;
import undc.general.Savable;
import undc.items.Weapon;
import undc.items.WeaponAmmo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class for running the game. Does all the calculations and stuff.
 *
 * @author Kevin Zhao
 * @version 1.0
 */
public class GameController implements Savable {
    private static GameController instance;

    private final Camera camera;

    private Timer timer;
    private Room room;
    private Player player;
    private double velX;
    private double velY;
    private double accelX;
    private double accelY;
    private boolean isRunning;
    private boolean isStopped;
    private GameRunner runner;

    //debug variables
    private long ticks;
    private double totalTime;

    //boolean variables for tracking event
    private HashMap<String, Boolean> states;

    /**
     * Constructor for GameController.
     */
    private GameController() {
        camera = new Camera();
    }

    /**
     * Acts as a singleton for the GameController. If it does not exist, make one.
     * @return current instance of GameController
     */
    public static GameController getInstance() {
        if (instance == null) {
            resetInstance();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = new GameController();
    }

    /**
     * Stops and removes the instance.
     */
    public static void stopInstance() {
        if (instance != null) {
            instance.stop();
        }
        instance = null;
    }

    /**
     * Starts the game in the provided room.
     * @param room Room to start in
     */
    public void start(Room room, Player player) {
        this.player = player;
        this.room = room;

        //reset the game on start
        reset();
        player.setX(room.getStartX());
        player.setY(room.getStartY());

        setupScene();
    }

    /**
     * Starts the game after a save is loaded.
     * @param room Room to load into
     * @param player Player to load
     */
    public void startLoaded(Room room, Player player) {
        this.player = player;
        this.room = room;
        setupScene();
    }

    /**
     * Sets up the scene.
     */
    private void setupScene() {
        //set the current room & scene
        setRoom(room);
        Scene scene = getScreen().getScene();

        //Handle key events
        scene.setOnKeyPressed(e -> handleKey(Config.keyStringify(e.getCode()), true, e));
        scene.setOnKeyReleased(e -> handleKey(Config.keyStringify(e.getCode()), false, e));
        scene.setOnMousePressed(e -> handleKey(Config.mbStringify(e.getButton()), true, e));
        scene.setOnMouseReleased(e -> handleKey(Config.mbStringify(e.getButton()), false, e));
        scene.setOnScroll(e -> handleKey(Config.scrollStringify(e.getDeltaY()), false, e));
    }

    /**
     * Pauses/resumes the game.
     */
    public void pause() {
        if (isRunning) {
            if (Vars.DEBUG) {
                Console.print("Game has been paused");
                Console.print("Average Server FPS in " + ticks + " ticks: " + round(1000.0 / totalTime * ticks));
            }
            timer.cancel();
            getScreen().getTimer().stop();
        } else {
            if (Vars.DEBUG) {
                Console.print("Game has been resumed");
            }
            startTimer();
            getScreen().getTimer().start();
        }
        if (!isRunning && isStopped) {
            isStopped = false;
        }
        isRunning = !isRunning;
    }

    /**
     * Stops the game.
     */
    public void stop() {
        isRunning = true;
        pause();
        isStopped = true;
        if (Vars.DEBUG) {
            Console.print("Game has been stopped.");
        }
    }

    /**
     * Starts the game timer/clock.
     */
    private void startTimer() {
        timer = new Timer();
        runner = new GameRunner();
        timer.schedule(runner, 0, 1000 / Vars.i("sv_tickrate"));
    }

    /**
     * Changes the room.
     * @param newRoom Room to change to
     */
    public void setRoom(Room newRoom) {
        if (isRunning) {
            stop();
        }
        velX = 0;
        velY = 0;
        player.setDirection(player.getDirection());
        room = newRoom;
        camera.setX(room.getWidth() / 2.0);
        camera.setY(room.getHeight() / 2.0);
        Platform.runLater(() -> getScreen().setRoom(newRoom));
    }

    /**
     * Updates data after room change.
     */
    public void updateRoom() {
        pause();
    }

    /**
     * Resets all game values.
     */
    private void reset() {
        velX = 0;
        velY = 0;
        accelX = 0;
        accelY = 0;
        isRunning = false;
        isStopped = false;
        if (room == null) {
            camera.setX(0);
            camera.setY(0);
        } else {
            camera.setX(room.getWidth() / 2.0);
            camera.setY(room.getHeight() / 2.0);
        }
        // states for player movement direction
        states = new HashMap<>();
        states.put("north", false);
        states.put("east", false);
        states.put("south", false);
        states.put("west", false);
        states.put("frictionX", false);
        states.put("frictionY", false);
        states.put("attacking", false);
        states.put("firing", false);
        states.put("pausePress", false);
        states.put("inventory", false);
        states.put("usePress", false);
        states.put("drop", false);
        states.put("interact", false);
        states.put("stopframe", false);
        ticks = 0;
        totalTime = 0;
    }

    /**
     * Handler for key events.
     * @param key String of the key that was pressed
     * @param isPress Whether the event is a press or release event
     */
    private void handleKey(String key, boolean isPress, Event e) {
        e.consume();
        String control = Config.getInstance().getControl(key);
        //movement keys
        if (control.equals("up") || control.equals("down") || control.equals("right") || control.equals("left")) {
            handleMovementKey(Direction.parse(control), isPress);
            return;
        }
        if (control.equals("console") || (getScreen().isConsoleOpen() && control.equals("pause"))) {
            if (isPress) {
                Platform.runLater(() -> getScreen().toggleConsole());
                pause();
                return;
            }
        }

        if (isStopped || getScreen().isConsoleOpen()) {
            return;
        }

        // Global key binds, regardless of game play/pause state
        if (control.equals("pause")) {
            if (!states.get("pausePress") && isPress) {
                pause();
                GameScreen screen = getScreen();
                // esc is used to leave inventory if it's currently open
                // otherwise, use it to pause/unpause game
                if (screen.isInventoryOpen()) {
                    screen.toggleInventory();
                } else if (GraphicalInventory.isActive()) {
                    Audio.playAudio("chest_close");
                    GraphicalInventory.hide();
                } else {
                    screen.togglePause();
                }
            }
            states.put("pausePress", isPress);
        } else if (control.equals("inventory")) {
            if (!states.get("inventory") && isPress) {
                if (isRunning || getScreen().isInventoryOpen()) {
                    pause();
                    getScreen().toggleInventory();
                } else if (GraphicalInventory.isActive()) {
                    pause();
                    GraphicalInventory.hide();
                }
            }
            states.put("inventory", isPress);
        }

        if (!isRunning) {
            return;
        }

        switch (control) {
            case "attack":
                states.put("attacking", isPress);
                break;
            case "attack2":
                states.put("firing", isPress);
                break;
            case "use":
                if (!states.get("usePress") && isPress) {
                    InventoryItem selected = player.getItemSelected();
                    if (selected != null) {
                        selected.getItem().use();
                    }
                }
                states.put("usePress", isPress);
                break;
            case "nextinv":
                player.selectNext();
                getScreen().updateHud();
                break;
            case "previnv":
                player.selectPrev();
                getScreen().updateHud();
                break;
            case "drop":
                states.put("drop", isPress);
                break;
            case "rotateinv":
                if (isPress) {
                    player.getInventory().rotate();
                    getScreen().updateHud();
                }
                break;
            case "reload":
                Item item = player.getItemSelected() != null ? player.getItemSelected().getItem() : null;
                if (item instanceof RangedWeapon) {
                    ((RangedWeapon) item).startReload();
                }
                break;
            case "slot1": case "slot2": case "slot3": case "slot4": case "slot5":
                if (isPress) {
                    slotSelector(control);
                }
                break;
            case "interact":
                states.put("interact", isPress);
                break;
            default:
                break;
        }
    }

    /**
     * Selects a position in the player's inventory.
     * @param slot String representing the inventory position to select
     */
    private void slotSelector(String slot) {
        slot = slot.replace("slot", "");
        try {
            int n = Integer.parseInt(slot) - 1;
            player.select(n);
            GameScreen.getInstance().updateHud();
        } catch (NumberFormatException e) {
            Console.error("Invalid slot selected.");
        }
    }

    /**
     * Method to handle movement when the player presses a movement key.
     * @param dir Direction to apply force to
     * @param isPress Whether player is pressing or releasing the key
     */
    private void handleMovementKey(Direction dir, boolean isPress) {
        if (isPress == states.get(dir.toString().toLowerCase())) {
            return;
        }
        int sign = isPress ? 1 : -1;
        if (dir == Direction.WEST || dir == Direction.SOUTH) {
            sign *= -1;
        }
        states.put(dir.toString().toLowerCase(), isPress);
        double accel = (double) Vars.i("sv_acceleration") / Vars.i("sv_tickrate") / Vars.i("sv_tickrate");
        if (dir == Direction.WEST || dir == Direction.EAST) {
            accelX += round(sign * accel);
            accelX = round(accelX);
        } else if (dir == Direction.NORTH || dir == Direction.SOUTH) {
            accelY += sign * accel;
            accelY = round(accelY);
        }
    }

    /**
     * Puts a dropped item into the room relative to the player's position.
     * @param item Item to drop
     * @param quantity int amount of item to drop
     */
    public void give(Item item, int quantity) {
        if (item == null) {
            Console.error("Item cannot be null.");
            return;
        }
        if (quantity < 1 || quantity > 100) {
            Console.error("Invalid quantity.");
            return;
        }
        double x = player.getX() + player.getWidth() / 2.0;
        double y = player.getY() + player.getHeight() / 2.0;
        Image sprite = item.getSprite();
        x -= sprite.getWidth() / 2;
        y -= sprite.getHeight() / 2;

        runner.dropAt(item, quantity, x, y, false);
        runner.run();
        getScreen().updateHud();
    }

    /**
     * Adds a monster or obstacle into a room.
     * @param ent Moveable entity that is being added
     * @param x int x-cord to spawn it to
     * @param y int y-cord to spawn it to
     */
    public void spawn(GameObject ent, int x, int y) {
        if (ent == null) {
            Console.error("Invalid entity to spawn.");
            return;
        }
        if (x < 0 || x + ent.getWidth() > room.getWidth()) {
            Console.error("Invalid x value.");
            return;
        }
        if (y < 0 || y + ent.getHeight() > room.getHeight()) {
            Console.error("Invalid y value.");
            return;
        }
        if (ent instanceof Monster) {
            Monster m = (Monster) ent;
            m.setX(x);
            m.setY(y);
            room.getEntities().add(m);
        } else if (ent instanceof Obstacle) {
            Obstacle o = (Obstacle) ent;
            o.setX(x);
            o.setY(y);
            room.getObstacles().add(o);
        }
    }

    public void drop(Item item, int quantity) {
        runner.drop(item, quantity);
    }

    public void drop(Item item) {
        drop(item, 1);
    }

    public void dropAt(Item item, double x, double y) {
        runner.dropAt(item, x, y);
    }

    /**
     * Rounds a number to (precision) digits.
     * @param number Number to round
     * @return Returns the rounded number.
     */
    private double round(double number) {
        return Math.round(number * Vars.i("sv_precision")) / (double) Vars.i("sv_precision");
    }

    /**
     * Shortcut to get the GameScreen instance.
     * @return Returns the GameScreen instance
     */
    private GameScreen getScreen() {
        return GameScreen.getInstance();
    }

    private double tickTime() {
        return 1000.0 / Vars.i("sv_tickrate");
    }

    public Camera getCamera() {
        return camera;
    }

    /**
     * Method to save the game.
     */
    public void save() {
        JSONObject saveObj = new JSONObject();
        saveObj.put("gamedata", saveObject());
        saveObj.put("game", getScreen().saveObject());
        saveObj.put("vars", Vars.saveObject());
        saveObj.put("data", DataManager.getInstance().saveObject());

        saveObj.put("name", DataManager.getInstance().getName());
        String mode = getScreen().getMode().toString().toLowerCase();
        saveObj.put("mode", mode.substring(0, 1).toUpperCase() + mode.substring(1));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        saveObj.put("date", dtf.format(LocalDateTime.now()));

        if (DataManager.getInstance().saveGame(saveObj)) {
            Console.print("Game Saved.");
        }
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("room", room.getId());
        o.put("velX", velX);
        o.put("velY", velY);
        o.put("accelX", accelX);
        o.put("accelY", accelY);
        o.put("ticks", ticks);
        o.put("totalTime", totalTime);
        o.put("camera", camera.saveObject());
        JSONArray stateso = new JSONArray();
        for (Map.Entry<String, Boolean> e : states.entrySet()) {
            JSONObject obj = new JSONObject();
            obj.put("key", e.getKey());
            obj.put("value", e.getValue());
            stateso.put(obj);
        }
        o.put("states", stateso);

        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        try {
            reset();
            velX = o.getDouble("velX");
            velY = o.getDouble("velY");
            accelX = o.getDouble("accelX");
            accelY = o.getDouble("accelY");
            ticks = o.getLong("ticks");
            totalTime = o.getDouble("totalTime");
            if (!camera.parseSave(o.getJSONObject("camera"))) {
                return false;
            }
            JSONArray statesObj = o.getJSONArray("states");
            for (int i = 0; i < statesObj.length(); i++) {
                JSONObject obj = statesObj.getJSONObject(i);
                states.put(obj.getString("key"), obj.getBoolean("value"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Console.error("Failed to load Game Controller.");
            return false;
        }
        return true;
    }

    /**
     * Class that is used to calculate stuff on each tick.
     */
    class GameRunner extends TimerTask {
        /**
         * Primary runner method, controls the data calculations of each tick.
         */
        public void run() {
            if (!(Controller.getState() instanceof GameScreen)) {
                Console.error("Invalid Run Instance!");
                this.cancel();
                return;
            }

            ticks++;
            final long startTime = System.nanoTime();

            //move the player
            managePlayerMovement();
            if (states.get("stopframe")) {
                states.put("stopframe", false);
                return;
            }

            //update velocity
            updatePlayerVelocity();

            //check for player attacking
            managePlayerAttack();

            manageInteraction();

            //drop the items
            manageItemDropping();

            //check item pickup
            manageItemPickup();

            //Manage Monsters
            manageMonsters();

            //manage items (bombs)
            if (manageObstacleItems()) {
                return;
            }

            //lower cooldowns
            manageCooldowns();

            //check for projectiles
            manageProjectiles();

            //manage player status effects
            managePlayerEffects();

            //manage player status
            managePlayer();

            long endTime = System.nanoTime();
            double execTime = round((endTime - startTime) / 1000000.0); //in milliseconds
            totalTime = round(totalTime + execTime);
        }

        /**
         * Calculates and moves the player based on their velocity. Takes collisions into account.
         */
        private void managePlayerMovement() {
            double posX = player.getX();
            double posY = player.getY();
            double newPosX = round(posX + velX);
            double newPosY = round(posY + velY);

            //check if position is valid. If it is, move.
            Coords movePos = checkPos(new Coords(newPosX, newPosY), player.getWidth(), player.getHeight());
            if (movePos.getX() != posX || movePos.getY() != posY) {
                // give a cool down to playing the audio
                if (player.getWalkCooldown() == 0) {
                    Audio.playAudio("footsteps");
                    double cooldown = Vars.d("sv_walk_cooldown");
                    player.setWalkCooldown(800 * cooldown);
                }
                newPosX = movePos.getX();
                newPosY = movePos.getY();

                //check collisions with obstacles
                Obstacle[] obs = room.getObstacles().toArray(new Obstacle[0]);
                Collision<Obstacle> check = checkCollisions(obs, player, new Coords(newPosX, newPosY));
                if (check.getCollider() == null) {
                    //set player sprite
                    Direction dir;
                    if (newPosX < posX) {
                        dir = Direction.WEST;
                    } else if (newPosX > posX) {
                        dir = Direction.EAST;
                    } else if (newPosY > posY) {
                        dir = Direction.NORTH;
                    } else {
                        dir = Direction.SOUTH;
                    }
                    player.setDirection(dir, true);

                    //check for door intersections
                    if (checkDoors(new Coords(newPosX, newPosY))) {
                        states.put("stopframe", true);
                        return;
                    }
                    player.setX(newPosX);
                    player.setY(newPosY);
                }


            }
        }

        /**
         * Manages player item pickup.
         */
        private void manageItemPickup() {
            boolean itemPickedUp = false;
            droploop:
            for (int i = 0; i < room.getDroppedItems().size(); i++) {
                DroppedItem d = room.getDroppedItems().get(i);
                if (d.getCooldown() > 0) {
                    continue;
                }
                double dist = distance(player, d);
                //pick up item
                if (dist <= Vars.i("sv_player_pickup_range")) {
                    //check if ammunition
                    if (d.getItem() instanceof Ammunition) {
                        //check if a weapon that uses this ammunition exists
                        Ammunition a = (Ammunition) d.getItem();
                        //loop through items
                        for (InventoryItem item : player.getInventory()) {
                            //if item is a weapon
                            if (item != null && item.getItem() instanceof RangedWeapon) {
                                WeaponAmmo weaponAmmo = ((RangedWeapon) item.getItem()).getAmmo();
                                //if the weapon's ammo exists & is the same as the dropped item
                                if (weaponAmmo != null && weaponAmmo.getProjectile() != null
                                        && weaponAmmo.getProjectile().equals(a.getProjectile())) {
                                    int maxChange = weaponAmmo.getBackupSize() - weaponAmmo.getBackupRemaining();
                                    if (a.getAmount() <= maxChange) {
                                        weaponAmmo.setBackupRemaining(weaponAmmo.getBackupRemaining() + a.getAmount());
                                        itemPickedUp = true;
                                        room.getDroppedItems().remove(i);
                                        i--;
                                    } else {
                                        weaponAmmo.setBackupRemaining(weaponAmmo.getBackupSize());
                                        a.setAmount(a.getAmount() - maxChange);
                                    }
                                    continue droploop;
                                }
                            }
                        }
                        //don't do anything if player can't pick up the ammo
                        continue;
                    }


                    // check for item existing in inventory
                    for (InventoryItem item : player.getInventory()) {
                        if (item != null) {
                            // if item exists and is not max stack
                            if (item.getItem().equals(d.getItem()) && item.getQuantity()
                                    < item.getItem().getMaxStackSize()) {
                                itemPickedUp = true;
                                int total = item.getQuantity() + d.getQuantity();
                                if (total > item.getItem().getMaxStackSize()) {
                                    item.setQuantity(item.getItem().getMaxStackSize());
                                    d.setQuantity(total - item.getItem().getMaxStackSize());
                                } else {
                                    item.setQuantity(total);
                                    room.getDroppedItems().remove(i);
                                    i--;
                                    continue droploop;
                                }
                            }
                        }
                    }
                    // not in inventory or inventory items are full
                    while (d.getQuantity() > 0) {
                        if (player.getInventory().add(d.getItem())) {
                            d.setQuantity(d.getQuantity() - 1);
                            itemPickedUp = true;
                        } else {
                            break;
                        }
                    }
                    if (d.getQuantity() <= 0) {
                        // remove dropped item
                        room.getDroppedItems().remove(i);
                        i--;
                    }
                }
            }
            if (itemPickedUp) {
                Platform.runLater(() -> getScreen().updateHud());
            }
        }

        /**
         * Manages player dropping items. If the drop key is held down, will drop 1 item per tick.
         */
        private void manageItemDropping() {
            InventoryItem currentItem = player.getItemSelected();
            if (!states.get("drop") || currentItem == null || currentItem.isInfinite()) {
                return;
            }
            //remove from inventory
            if (currentItem.getQuantity() > 1) {
                currentItem.setQuantity(currentItem.getQuantity() - 1);
            } else {
                if (!player.getInventory().remove(currentItem)) {
                    Console.error("Failed to drop item");
                    return;
                }
            }
            drop(currentItem.getItem(), 1);
        }

        /**
         * Manages the player interacting with things.
         */
        private void manageInteraction() {
            if (states.get("interact")) {
                ArrayList<Object> check = new ArrayList<>();
                check.addAll(room.getObstacles());
                check.addAll(room.getEntities());
                // check what player is facing & close enough to
                for (Object o : check) {
                    // player is facing the obstacle & it is interactable & it is in range
                    if (o instanceof Interactable && o instanceof GameObject
                            && directionOf(player, (GameObject) o).contains(player.getDirection())
                            && distance(player, (GameObject) o) < Vars.i("sv_interact_distance")) {
                        ((Interactable) o).interact();
                        states.put("interact", false);
                        return;
                    }
                }
            }
        }

        /**
         * Places an item removed from the player's inventory into the room.
         * @param item Item to remove and drop into room
         */
        private void drop(Item item, int quantity) {
            int d = Vars.i("sv_dropitem_distance");
            //get player center
            double x = player.getX() + player.getWidth() / 2.0;
            double y = player.getY() + player.getHeight() / 2.0;
            Image itemSprite = item.getSprite();
            Direction dir = player.getDirection();
            x += dir == Direction.WEST ? -d : (dir == Direction.EAST ? d : 0);
            y += dir == Direction.SOUTH ? -d : (dir == Direction.NORTH ? d : 0);
            x -= itemSprite.getWidth() / 2;
            y -= itemSprite.getHeight() / 2;
            dropAt(item, quantity, x, y, true);
        }

        /**
         * Drops an item at a specified location.
         * @param item Item to drop
         * @param x X position
         * @param y Y position
         */
        private void dropAt(Item item, int quantity, double x, double y, boolean setCooldown) {
            int width = (int) item.getSprite().getWidth();
            int height = (int) item.getSprite().getHeight();

            //keep inside room
            Coords check = checkPos(new Coords(x, y), width, height);
            x = check.getX();
            y = check.getY();

            Dummy dummy = new Dummy(x, y, width, height);

            // check for nearby items to combine with
            int max = Vars.i("sv_droppeditem_max_quantity");
            int range = Vars.i("sv_droppeditem_combine_range");
            for (DroppedItem i : room.getDroppedItems()) {
                if (i.getItem().equals(item) && i.getQuantity() < max && distance(i, dummy) <= range) {
                    int total = i.getQuantity() + quantity;

                    if (total > max) {
                        i.setQuantity(max);
                        quantity = total - max;
                    } else {
                        i.setQuantity(total);
                        quantity = 0;
                    }
                    if (setCooldown) {
                        i.setCooldown();
                    }
                }
            }

            // create new dropped items
            while (quantity > 0) {
                int amt = Math.min(quantity, max);
                quantity -= amt;

                DroppedItem di = new DroppedItem(item.getId(), amt, x, y, width, height);
                if (setCooldown) {
                    di.setCooldown();
                }
                room.getDroppedItems().add(di);
            }
            Platform.runLater(() -> getScreen().updateHud());
        }

        private void dropAt(Item item, double x, double y) {
            dropAt(item, 1, x, y, true);
        }

        /**
         * Manages all cooldowns.
         */
        private void manageCooldowns() {
            double time = tickTime();
            // lower player attack cooldown
            if (player.getAttackCooldown() > 0) {
                player.setAttackCooldown(Math.max(0, player.getAttackCooldown() - time));
            }
            if (player.getWalkCooldown() > 0) {
                player.setWalkCooldown(Math.max(0, player.getWalkCooldown() - time));
            }
            // lower held weapon delay if rangedweapon
            Item item = player.getItemSelected() != null ? player.getItemSelected().getItem() : null;
            if (item instanceof RangedWeapon && ((RangedWeapon) item).getDelay() > 0) {
                RangedWeapon weapon = (RangedWeapon) item;
                weapon.setDelay(Math.max(0, weapon.getDelay() - time));
                if (weapon.isReloading() && weapon.getDelay() == 0) {
                    weapon.finishReload();
                }
            }

            // dropped item cooldowns
            for (DroppedItem i : room.getDroppedItems()) {
                if (i.getCooldown() > 0) {
                    i.setCooldown(Math.max(0, i.getCooldown() - time));
                }
            }
        }

        /**
         * Manages projectiles, including movement & damage.
         */
        private void manageProjectiles() {
            for (int i = 0; i < room.getProjectiles().size(); i++) {
                ShotProjectile p = room.getProjectiles().get(i);
                //move
                double x = p.getX();
                double y = p.getY();
                double newX = x + p.getVelX() / Vars.i("sv_tickrate");
                double newY = y + p.getVelY() / Vars.i("sv_tickrate");

                //check collisions
                Coords check = checkPos(new Coords(newX, newY), p.getWidth(), p.getHeight());
                //hit an object, then explode
                if (check.getX() == x && check.getY() == y) {
                    p.hit();
                    i--;
                } else {
                    //check for entity collisions
                    newX = check.getX();
                    newY = check.getY();
                    Collision<Entity> c = checkCollisions(room.getEntities().toArray(new Entity[0]), p,
                            new Coords(newX, newY));
                    Entity m = c.getCollider();
                    //hit a monster
                    if (m != null) {
                        p.hit(m);
                    } else {
                        //keep moving
                        p.setX(newX);
                        p.setY(newY);

                        //calculate distance
                        double d = Math.sqrt(Math.pow(p.getVelX() / Vars.i("sv_tickrate"), 2)
                                + Math.pow(p.getVelY() / Vars.i("sv_tickrate"), 2));
                        p.setDistance(round(p.getDistance() + d));
                        if (p.getDistance() >= p.getProjectile().getRange()) {
                            p.hit();
                            i--;
                        }
                    }
                }
            }
        }

        /**
         * Manages player's ability to attack.
         */
        private void managePlayerAttack() {
            Item item = player.getItemSelected() != null ? player.getItemSelected().getItem() : null;
            if (states.get("attacking") && player.getAttackCooldown() == 0) {
                Audio.playAudio("weapon_swing");
                double damage = Vars.d("sv_fist_damage");
                double cooldown = Vars.d("sv_fist_cooldown");
                double modifier = player.getAttack();
                if (item instanceof Weapon) {
                    Weapon weapon = (Weapon) item;
                    damage = weapon.getDamage();
                    cooldown = weapon.getAttackSpeed();
                }
                //check for effects
                for (Effect e : player.getEffects()) {
                    if (e.getType() == EffectType.ATTACKBOOST) {
                        modifier += e.getAmount();
                    }
                }
                player.setAttackCooldown(1000 * cooldown);
                for (Entity e : room.getEntities()) {
                    if (e instanceof Monster && e.getHealth() > 0) {
                        double dist = distance(player, e);
                        if (dist <= Vars.i("sv_player_attack_range")) {
                            ((Monster) e).attackMonster(modifier * damage);
                            break;
                        }
                    }
                }
            }

            //firing ranged weapon
            if (states.get("firing") && item instanceof RangedWeapon) {
                RangedWeapon weapon = (RangedWeapon) item;
                if (weapon.getDelay() > 0) {
                    return;
                }

                WeaponAmmo weaponAmmo = weapon.getAmmo();

                //check for ammo
                if (weaponAmmo.getRemaining() <= 0 && !weapon.isReloading()) {
                    weapon.startReload();
                    return;
                }

                states.put("firing", false);

                //set fire delay
                weapon.setDelay(weapon.getFireRate() * 1000);

                //reduce ammo
                if (!Vars.b("sv_infinite_ammo")) {
                    weaponAmmo.setRemaining(weaponAmmo.getRemaining() - 1);
                }

                //update ammo on HUD
                Platform.runLater(() -> getScreen().updateHud());

                //create projectile
                Direction dir = player.getDirection();
                double x = player.getX() + player.getWidth() / 2.0;
                double y = player.getY() + player.getHeight();
                int height = weaponAmmo.getProjectile().getHeight();
                int width = weaponAmmo.getProjectile().getWidth();
                if (dir == Direction.WEST) {
                    x -= 5;
                } else if (dir == Direction.EAST) {
                    x += 5;
                } else if (dir == Direction.NORTH) {
                    y += 5;
                } else if (dir == Direction.SOUTH) {
                    y -= 5;
                }
                x -= width / 2.0;
                y -= height / 2.0;

                //reset x and y coordinates
                Coords check = checkPos(new Coords(x, y), width, height);
                x = check.getX();
                y = check.getY();

                //velocity
                double speed = weaponAmmo.getProjectile().getSpeed();
                double velX = dir == Direction.WEST ? -speed : (dir == Direction.EAST ? speed : 0);
                double velY = dir == Direction.NORTH ? speed : (dir == Direction.SOUTH ? -speed : 0);

                SpriteGroup sprites = weaponAmmo.getProjectile().getSprites();
                Image sprite = sprites.get(dir);

                //create projectile
                ShotProjectile sp = new ShotProjectile(weaponAmmo.getProjectile(), x, y, velX, velY);
                sp.setSprite(sprite);
                room.getProjectiles().add(sp);
                if (sp.getProjectile().getId().equals("rocket")) {
                    Audio.playAudio("rocket_launch");
                } else if (sp.getProjectile().getId().equals("arrow")) {
                    Audio.playAudio("bow");
                }
            }
        }

        /**
         * Manages monsters.
         */
        private void manageMonsters() {
            for (Entity e : room.getEntities()) {
                if (!(e instanceof Monster) || e.getHealth() == 0) {
                    continue;
                }
                //check and move the monster
                if (monsterMove((Monster) e)) {
                    return;
                }
            }
        }

        /**
         * Manages obstacle items.
         * @return Whether to continue the tick or not
         */
        private boolean manageObstacleItems() {
            for (int i = 0; i < room.getObstacles().size(); i++) {
                if (!(room.getObstacles().get(i) instanceof ObstacleItem)) {
                    continue;
                }
                ObstacleItem o = (ObstacleItem) room.getObstacles().get(i);
                //has an item linked
                Item item = o.getItem();
                if (item instanceof Bomb) {
                    Bomb b = (Bomb) item;
                    //decrement fuse
                    b.setLivefuse(b.getLivefuse() - tickTime());

                    //if bomb has blown up
                    if (b.getLivefuse() <= 0) {
                        //attack player
                        double dist = distance(player, o);

                        //draw explosion
                        Audio.playAudio("bomb_explosion");
                        ShotProjectile.addExplosion(room, o, (int) b.getRadius() * 2);

                        if (dist <= b.getRadius()) {
                            player.setHealth(Math.max(0, player.getHealth() - b.getDamage()
                                    * Vars.d("sv_self_damage_modifier")));
                            Platform.runLater(() -> getScreen().updateHud());
                            if (player.getHealth() == 0) {
                                gameOver();
                                return true;
                            }
                        }

                        //get all entities within range of the bomb
                        for (Entity e : room.getEntities()) {
                            dist = distance(e, o);
                            if (dist <= b.getRadius()) {
                                ((Monster) e).attackMonster(b.getDamage());
                            }
                        }

                        //remove obstacle
                        room.getObstacles().remove(i);
                        i--;
                    }
                }
            }
            return false;
        }

        /**
         * Updates the player's velocity and acceleration.
         */
        private void updatePlayerVelocity() {
            final double originalVelX = velX;
            final double originalVelY = velY;
            velX += accelX;
            velX = round(velX);
            velY += accelY;
            velY = round(velY);

            //don't allow speed to exceed max
            double maxVel = (double) Vars.i("sv_max_velocity") / Vars.i("sv_tickrate");
            double friction = (double) Vars.i("sv_friction") / Vars.i("sv_tickrate") / Vars.i("sv_tickrate");
            if (Math.abs(velX) > maxVel) {
                velX = (velX > 0 ? 1 : -1) * maxVel;
                //was moving before and decelerated to 0
            } else if (states.get("frictionX") && Math.abs(originalVelX) <= Math.abs(accelX)) {
                if (velY == 0) {
                    player.setDirection((originalVelX > 0) ? Direction.EAST : Direction.WEST);
                }
                velX = 0;
                accelX -= (accelX > 0 ? 1 : -1) * friction;
                states.put("frictionX", false);
            }
            if (Math.abs(velY) > maxVel) {
                velY = (velY > 0 ? 1 : -1) * maxVel;
            } else if (states.get("frictionY") && Math.abs(originalVelY) <= Math.abs(accelY)) {
                if (velX == 0) {
                    player.setDirection((originalVelY > 0) ? Direction.NORTH : Direction.SOUTH);
                }
                velY = 0;
                accelY -= (accelY > 0 ? 1 : -1) * friction;
                states.put("frictionY", false);
            }

            //apply friction if not currently moving forward
            if (accelX == 0 && velX != 0) {
                states.put("frictionX", true);
                accelX += (velX > 0 ? -1 : 1) * friction;
            }
            if (accelY == 0 && velY != 0) {
                states.put("frictionY", true);
                accelY += (velY > 0 ? -1 : 1) * friction;
            }
        }

        /**
         * Manages player effects, such as speed or attack boost.
         */
        private void managePlayerEffects() {
            for (int i = 0; i < player.getEffects().size(); i++) {
                Effect e = player.getEffects().get(i);
                e.setDuration(e.getDuration() - tickTime());
                if (e.getDuration() <= 0) {
                    player.getEffects().remove(i--);
                    Platform.runLater(() -> getScreen().updateHud());
                }
            }
        }

        /**
         * Manages player status.
         */
        private void managePlayer() {
            if (player.getHealth() <= 0) {
                gameOver();
            }
        }

        /**
         * Checks if the specified coordinates are out of bounds of the game.
         * @param coords Coordinates of the object
         * @param w Width of the object
         * @param h Height of the object
         * @return Returns the coordinates to move to, the original if no change is required
         */
        private Coords checkPos(Coords coords, double w, double h) {
            double x = coords.getX();
            double y = coords.getY();
            if (x < 0 || x + w > room.getWidth()) {
                return new Coords((x < 0 ? 0 : room.getWidth() - w), y);
            }
            if (y < 0 || y + h > room.getHeight()) {
                return new Coords(x, (y < 0 ? 0 : room.getHeight() - h));
            }
            return new Coords(x, y);
        }

        /**
         * Checks whether Movable o is in range of Movable m's movement, using its new coordinates.
         * @param o Object to check if it is within range of the other object
         * @param m Object that is moving
         * @param newPos New coordinates of object m's movmement
         * @return Whether object o is in range of object m's movement
         */
        private boolean inRange(GameObject o, GameObject m, Coords newPos) {
            /* Checks if the object is in range of the player's movement
             *          _________
             *          |[]     |
             *          |  \    |
             *          |   \   |
             *          |    \  |
             *          |     []|
             *          ---------
             */

            double x = m.getX();
            double y = m.getY();
            double w = m.getWidth();
            double h = m.getHeight();
            double newX = newPos.getX();
            double newY = newPos.getY();

            if (o.getX() + o.getWidth() < Math.min(x, newX) || o.getX() > Math.max(x, newX) + w) {
                return false;
            }
            if (o.getY() + o.getHeight() < Math.min(y, newY) || o.getY() > Math.max(y, newY) + h) {
                return false;
            }
            return true;
        }

        /**
         * Checks which direction obj is in relation to the origin. Includes overlapping.
         * @param origin Origin of the relationship
         * @param obj Object to check
         * @return Returns the direction obj is to origin
         */
        private ArrayList<Direction> directionOf(GameObject origin, GameObject obj) {
            ArrayList<Direction> dirs = new ArrayList<>();
            if (obj.getX() <= origin.getX()) {
                dirs.add(Direction.WEST);
            }
            if (obj.getX() > origin.getX()) {
                dirs.add(Direction.EAST);
            }
            if (obj.getY() <= origin.getY()) {
                dirs.add(Direction.SOUTH);
            }
            if (obj.getY() > origin.getY()) {
                dirs.add(Direction.NORTH);
            }
            return dirs;
        }

        /**
         * Checks if a Movable object collides with any objects in a list.
         * @param list List of objects to check
         * @param m Object that is moving
         * @param newPos New coordinates of the object m
         * @param <T> Type of object to check
         * @return Returns with the collision data of the collision, with null values if no collision
         */
        private <T extends GameObject> Collision<T> checkCollisions(T[] list, GameObject m, Coords newPos) {
            double x = m.getX();
            double y = m.getY();
            double newX = newPos.getX();
            double newY = newPos.getY();
            for (T t : list) {
                if (t == null || t == m) {
                    continue;
                }

                //Check if monster is out of movement vector rectangle
                if (!inRange(t, m, new Coords(newX, newY))) {
                    continue;
                }

                //for monsters
                if (t instanceof Monster && ((Monster) t).getHealth() == 0) {
                    continue;
                } else if (t instanceof Obstacle && ((Obstacle) t).getType() == ObstacleType.NONSOLID) {
                    continue;
                }

                //movement direction
                boolean moveRight = x < newX;
                boolean moveUp = y < newY;

                //Get equation for intersection
                Equation equation = equation(x, y, newX, newY);
                Coords intersects = getIntersect(t, m, equation, moveUp, moveRight);

                //intersects
                if (intersects != null) {
                    return new Collision<>(intersects, t);
                }
            }
            return new Collision<>();
        }

        private <T extends GameObject> Collision<T> checkCollisions(ArrayList<T> list, GameObject m, Coords newPos) {
            return checkCollisions(list.toArray((T[]) new GameObject[0]), m, newPos);
        }

        /**
         * Checks if the player has entered a door. If so, teleport player.
         * @param newPos New coordinates of the player's movement
         * @return Whether to continue the tick
         */
        private boolean checkDoors(Coords newPos) {
            Door[] doors = {
                room.getTopDoor(),
                room.getBottomDoor(),
                room.getLeftDoor(),
                room.getRightDoor()
            };

            //loop through doors
            Collision<Door> check = checkCollisions(doors, player, newPos);
            Door d = check.getCollider();
            if (d != null) {
                Room newRoom = d.getGoesTo();

                //if next room is the exit, don't let player go through unless they have key
                if (room.getType() == RoomType.EXITROOM && d instanceof ExitDoor) {
                    if (player.getInventory().contains(DataManager.getExitKey())) {
                        stop();
                        Platform.runLater(() -> getScreen().win());
                    }
                    return player.getInventory().contains(DataManager.getExitKey());

                    // if in challenge room, don't let player leave if not completed
                } else if (room instanceof ChallengeRoom && !((ChallengeRoom) room).isCompleted()) {
                    return false;
                }

                //check if not visited & if there are still monsters
                if (!newRoom.visited()) {
                    for (Entity e : room.getEntities()) {
                        if (e instanceof Monster && e.getHealth() > 0) {
                            return false;
                        }
                    }
                }

                Door newDoor;
                double newStartX;
                double newStartY;
                if (newRoom.getType() == RoomType.EXITROOM) {
                    newStartX = (newRoom.getWidth() + player.getWidth()) / 2.0;
                    newStartY = 20;
                } else if (d.equals(room.getTopDoor())) {
                    newDoor = newRoom.getBottomDoor();
                    newStartX = newDoor.getX() + newDoor.getWidth() / 2.0
                            - player.getWidth() / 2.0;
                    newStartY = newDoor.getY() + LayoutGenerator.DOOR_SIZE + 10;
                } else if (d.equals(room.getBottomDoor())) {
                    newDoor = newRoom.getTopDoor();
                    newStartX = newDoor.getX() + newDoor.getWidth() / 2.0
                            - player.getWidth() / 2.0;
                    newStartY = newDoor.getY() - player.getHeight() - 1;
                } else if (d.equals(room.getRightDoor())) {
                    newDoor = newRoom.getLeftDoor();
                    newStartX = newDoor.getX() + 10 + LayoutGenerator.DOOR_SIZE;
                    newStartY = newDoor.getY() + newDoor.getHeight() / 5.0;
                } else {
                    newDoor = newRoom.getRightDoor();
                    newStartX = newDoor.getX() - 10 - player.getWidth();
                    newStartY = newDoor.getY() + newDoor.getHeight() / 5.0;
                }
                player.setX(newStartX);
                player.setY(newStartY);
                setRoom(newRoom);
                return true;
            }
            return false;
        }

        /**
         * Gets the intersection point of Movable mo's movement equation with an object o.
         * @param o Object to check collision with
         * @param mo Object that is moving
         * @param eq Equation of object mo's movement
         * @param moveUp Whether mo is moving up or down
         * @param moveRight Whether mo is moving right or left
         * @return The coordinates of the intersection point
         */
        private Coords getIntersect(GameObject o, GameObject mo, Equation eq, boolean moveUp, boolean moveRight) {
            /* Calculate x-coordinate intersection point on the y-axis
             *
             *          v obstacle
             *          ------------------
             *          |     \          |
             *          |      \         |
             *          --------*---------
             *                  ^\
             *                    \ < movement vector
             *
             * We have equation y = mx + b from playerEquation, which is the vector/line for the
             * player's movement. Since the door is within the rectangle bound by the player's
             * vector as calculated above, we know that any collisions are on the vector and not
             * past it.
             * We pass in the y value of either the top or bottom of the obstacle, depending on if
             * the player is moving downward or upward. If horizontal, it defaults to downward,
             * but it doesn't matter.
             * We then substitute the y in "y = mx + b", along with the m and b values obtained
             * earlier, and solve for x, resulting in an equation "x = (y - b) / m"
             */
            final double m = eq.getSlope();
            final double b = eq.getIntercept();
            final double w = mo.getWidth();
            final double h = mo.getHeight();

            //x value of the y/vertical intersection point
            double yIntXVal = (o.getY() + o.getHeight() - b) / m;
            if (moveUp) {
                yIntXVal = (o.getY() - h - b) / m;
            }
            //check for zero slope ie NaN yIntXVal
            if (m == 0) {
                yIntXVal = b;
            }
            /* if the player is moving vertically, then slope and y-intercept will be
             * infinity/undefined. If so, set the yIntXVal/x position of the intersect to the x
             * coordinate of the movement vector, which will be stored in the y-intercept variable
             */
            if (eq.isVertical()) {
                yIntXVal = b;
            }

            //check if intersect is on the obstacle
            if (yIntXVal <= o.getX() + o.getWidth() && yIntXVal + w >= o.getX()) {
                double coord = m * yIntXVal + b;
                return new Coords(yIntXVal, coord);
            }

            //Calculate y-coordinate intersection point on the x-axis, using y = mx + b
            double xIntYVal = m * (o.getX() + o.getWidth()) + b;
            if (moveRight) { //intersect is the right side of player to the left side of obstacle
                xIntYVal = m * (o.getX() - w) + b;
            }

            //check if intersect is on the obstacle
            if (xIntYVal <= o.getY() + o.getHeight() && xIntYVal + h >= o.getY()) {
                double coord = (xIntYVal - b) / m;
                if (m == 0) {
                    coord = o.getX() + ((moveRight) ? -w : o.getWidth());
                }
                return new Coords(coord, xIntYVal);
            }

            return null;
        }

        /**
         * Returns an equation for a specified vector.
         * @param x0 first x position
         * @param y0 first y position
         * @param x1 second x position
         * @param y1 second y position
         * @return array of the slope and the y-intercept of the line.
         */
        private Equation equation(double x0, double y0, double x1, double y1) {
            double m = (y1 - y0) / (x1 - x0);
            double b = y0 - m * x0;

            Equation eq = new Equation(m, b);

            //moving vertically
            if (x0 == x1 && y0 != y1) {
                eq = new Equation(m, x0, true);
            }
            return eq;
        }

        /**
         * Calculates the shortest distance between two objects.
         * @param a First object to check
         * @param b Second object to check
         * @return Returns the distance between the two objects
         */
        private double distance(GameObject a, GameObject b) {
            Vector v = shortestVector(a, b);
            Coords ac = v.getStart();
            Coords bc = v.getEnd();
            return round(Math.sqrt(Math.pow(ac.getX() - bc.getX(), 2) + Math.pow(ac.getY() - bc.getY(), 2)));
        }

        /**
         * Calculates the angle of the shortest distance between two objects.
         * @param a First object to check
         * @param b Second object to check
         * @return Angle of the distance between the two objects
         */
        private double angle(GameObject a, GameObject b) {
            Vector v = shortestVector(a, b);
            Coords ac = v.getStart();
            Coords bc = v.getEnd();
            return Math.atan2(ac.getY() - bc.getY(), ac.getX() - bc.getX());
        }

        /**
         * Finds the shortest vector between two objects.
         * @param a First object
         * @param b Second object
         * @return Returns a vector with the shortest distance
         */
        private Vector shortestVector(GameObject a, GameObject b) {
            double ax;
            double ay;
            double bx;
            double by;

            if (b.getX() + b.getWidth() <= a.getX()) {
                ax = a.getX();
                bx = b.getX() + b.getWidth();
            } else if (b.getX() >= a.getX() + a.getWidth()) {
                ax = a.getX() + a.getWidth();
                bx = b.getX();
            } else {
                bx = b.getX();
                ax = bx;
            }

            if (b.getY() + b.getHeight() <= a.getY()) {
                ay = a.getY();
                by = b.getY() + b.getHeight();
            } else if (b.getY() >= a.getY() + a.getHeight()) {
                ay = a.getY() + a.getHeight();
                by = b.getY();
            } else {
                by = b.getY();
                ay = by;
            }

            return new Vector(new Coords(ax, ay), new Coords(bx, by));
        }

        /**
         * Monster AI for calculating movement and attacking.
         * @param m Monster to calculate for
         * @return Returns if the tick should stop
         */
        private boolean monsterMove(Monster m) {
            //check queue
            for (int i = 0; i < m.getMoveQueue().size(); i++) {
                Move move = m.getMoveQueue().get(i);
                move.setDelay(move.getDelay() - tickTime());

                //time to apply the move
                if (move.getDelay() <= 0) {
                    double x = move.getPos().getX();
                    double y = move.getPos().getY();

                    m.setX(x);
                    m.setY(y);

                    //remove
                    m.getMoveQueue().remove(i--);
                }
            }

            //calculate distance between player and monster
            Move mq = (m.getMoveQueue().size() > 0)
                    ? m.getMoveQueue().get(m.getMoveQueue().size() - 1)
                    : new Move(new Coords(m.getX(), m.getY()), 0);
            double mPosX = mq.getPos().getX();
            double mPosY = mq.getPos().getY();
            Dummy md = new Dummy(mPosX, mPosY, m.getWidth(), m.getHeight());
            double d = distance(md, player);

            double range = Vars.i("ai_monster_move_range");
            double reactTime = Vars.i("ai_monster_reaction_time");
            if (m.getType() == MonsterType.FINALBOSS) {
                range = Vars.i("ai_boss_move_range");
                reactTime = Vars.i("ai_boss_reaction_time");
            }

            if (d <= range && d >= Vars.i("ai_monster_move_min")) {
                int counter = 0;
                int dir = Math.random() < 0.1 ? 1 : -1;
                Collision<Obstacle> obs;
                Coords newPos;
                do {
                    //move monster towards player
                    double angle = angle(md, player) - Math.PI + (dir * counter * Math.PI / 4);
                    double speed = m.getSpeed() / Vars.i("sv_tickrate");
                    double newPosX = mPosX + round(Math.cos(angle) * speed);
                    double newPosY = mPosY + round(Math.sin(angle) * speed);

                    //check collisions with obstacles
                    newPos = checkPos(new Coords(round(newPosX), round(newPosY)), m.getWidth(), m.getHeight());

                    obs = checkCollisions(room.getObstacles(), m, newPos);
                    counter++;
                } while (obs.getCollider() != null && counter < 8);

                if (obs.getCollider() != null) {
                    return false;
                }

                //add to queue
                Move moveItem = new Move(obs.getCollisionPoint() == null ? newPos : obs.getCollisionPoint(), reactTime);
                m.getMoveQueue().add(moveItem);
            }
            d = distance(m, player);

            //check for current attack
            if (d <= Vars.i("ai_monster_attack_range")) {
                if (checkAttack(m)) {
                    //set attack cooldown
                    m.setAttackCooldown(m.getAttackSpeed() * 1000);
                    //attack player
                    Audio.playAudio("monster");
                    double newHealth = player.getHealth() - m.getAttack();
                    player.setHealth(Math.max(0, newHealth));

                    //use run later to prevent any thread issues
                    Platform.runLater(() -> getScreen().updateHud());

                    //go to game over screen if player has died
                    if (player.getHealth() == 0) {
                        gameOver();
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Checks if a monster can attack or not.
         * @param m Monster to check
         * @return Returns if the monster is able to attack
         */
        private boolean checkAttack(Monster m) {
            if (m.getReaction() <= 0 && m.getAttackCooldown() <= 0) {
                m.setReaction(Vars.i("ai_monster_reaction_time"));
                return false;
            }
            double newCooldown = m.getAttackCooldown() - tickTime();
            double newTime = m.getReaction() - tickTime();
            m.setAttackCooldown(newCooldown);
            m.setReaction(newTime);

            if (newTime <= 0 && newCooldown <= 0) {
                m.setAttackCooldown(0);
                m.setReaction(0);
                return true;
            } else {
                return false;
            }
        }

        /**
         * Shortcut for handling game-over.
         */
        private void gameOver() {
            Audio.stopAudio();
            Audio.playAudio("game_over");
            Console.print("Game Over");
            GameScreen screen = getScreen();
            Platform.runLater(() -> {
                room = screen.getLayout().getStartingRoom();
                screen.gameOver();
            });
        }
    }
}
