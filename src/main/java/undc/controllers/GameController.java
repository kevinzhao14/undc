package undc.controllers;

import javafx.scene.*;
import undc.handlers.*;
import undc.objects.*;

import undc.gamestates.GameScreen;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.util.*;

/**
 * Class for running the game. Does all the calculations and stuff.
 *
 * @author Kevin Zhao
 * @version 1.0
 */
public class GameController {
    private static GameController instance;

    private Timer timer;
    private Room room;
    private Player player;
    private double velX;
    private double velY;
    private double accelX;
    private double accelY;
    private boolean isRunning;
    private boolean isStopped;

    //debug variables
    private long ticks;
    private double totalTime;

    //boolean variables for tracking event
    private HashMap<String, Boolean> states;

    /**
     * Constructor for GameController
     */
    private GameController() {
    }

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
     * Starts the game in the provided room.
     * @param room Room to start in
     */
    public void start(Room room) {
        //reset the game on start
        reset();

        //set the current room & scene
        setRoom(room);
        Scene scene = getScreen().getScene();

        //Handle key events
        scene.setOnKeyPressed(e -> handleKey(e.getCode().toString(), true));
        scene.setOnKeyReleased(e -> handleKey(e.getCode().toString(), false));
        scene.setOnMousePressed(e -> handleKey(Controls.mbStringify(e.getButton()), true));
        scene.setOnMouseReleased(e -> handleKey(Controls.mbStringify(e.getButton()), false));
        scene.setOnScroll(e -> handleKey(Controls.scrollStringify(e.getDeltaY()), false));
    }

    /**
     * Sets the player of the game
     * @param player Player object to set to
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Changes the room.
     * @param newRoom Room to change to
     */
    public void setRoom(Room newRoom) {
        if (isRunning) {
            stop();
        }
        room = newRoom;
        Platform.runLater(() -> getScreen().setRoom(newRoom));
    }

    /**
     * Updates data after room change.
     */
    public void updateRoom() {
        pause();
    }

    /**
     * Resets the player's position to the room's starting position.
     */
    public void resetPos() {
        player.setX(room.getStartX());
        player.setY(room.getStartY());
        refresh();
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
        states = new HashMap<>();
        states.put("left", false);
        states.put("up", false);
        states.put("right", false);
        states.put("down", false);
        states.put("frictionX", false);
        states.put("frictionY", false);
        states.put("attacking", false);
        states.put("firing", false);
        states.put("pausePress", false);
        states.put("inventory", false);
        states.put("usePress", false);
        states.put("drop", false);
        ticks = 0;
        totalTime = 0;
    }

    /**
     * Pauses/resumes the game.
     */
    public void pause() {
        if (isRunning) {
            if (Vars.DEBUG) {
                Console.print("Game has been paused");
                Console.print("Average Server FPS in " + ticks + " ticks: " + round(1000.0 / (totalTime / ticks)));
            }
            timer.cancel();
        } else {
            if (Vars.DEBUG) Console.print("Game has been resumed");
            startTimer();
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
        if (Vars.DEBUG) Console.print("Game has been stopped.");
    }

    public void give(Item item, int quantity) {
        if (item == null) {
            Console.error("Item cannot be null.");
            return;
        }
        if (quantity < 1 || quantity > item.getMaxStackSize()) {
            Console.error("Invalid quantity.");
            return;
        }
        for (int i = 0; i < quantity; i++) {
            double x = player.getX() + player.getWidth() / 2;
            double y = player.getY() + player.getHeight() / 2;
            Image sprite = item.getSprite();
            x -= sprite.getWidth() / 2;
            y -= sprite.getHeight() / 2;

            DroppedItem di = new DroppedItem(item, x, y, sprite.getWidth(), sprite.getHeight());
            room.getDroppedItems().add(di);
        }
        refresh();
    }

    public void spawn(Movable ent, int x, int y) {
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
            Monster m = new Monster((Monster) ent, 1);
            m.setX(x);
            m.setY(y);
            room.getMonsters().add(m);
        } else if (ent instanceof Obstacle) {
            Obstacle o = ((Obstacle) ent).copy();
            o.setX(x);
            o.setY(y);
            room.getObstacles().add(o);
        }
        refresh();
    }

    /**
     * Starts the game timer/clock.
     */
    private void startTimer() {
        refresh();
        timer = new Timer();
        timer.schedule(new GameRunner(), 0, 1000 / Vars.i("fps"));
    }

    /**
     * Handler for key events.
     * @param key String of the key that was pressed
     * @param isPress Whether the event is a press or release event
     */
    private void handleKey(String key, boolean isPress) {
        String control = Controls.getInstance().getControl(key);

        //movement keys
        if (control.equals("up") || control.equals("down") || control.equals("right") || control.equals("left")) {
            handleMovementKey(control, isPress);
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

        //Global key binds, regardless of game play/pause state
        if (control.equals("pause")) {
            if (!states.get("pausePress") && isPress) {
                pause();
                GameScreen screen = getScreen();
                //esc is used to leave inventory if it's currently open
                //otherwise, use it to pause/unpause game
                if (screen.isInventoryVisible()) {
                    screen.toggleInventory();
                } else {
                    screen.togglePause();
                }
            }
            states.put("pausePress", isPress);
        } else if (control.equals("inventory")) {
            if (!states.get("inventory") && isPress) {
                if (isRunning || getScreen().isInventoryVisible()) {
                    pause();
                    getScreen().toggleInventory();
                }
            }
            states.put("inventory", isPress);
        }

        if (!isRunning) {
            return;
        }

        switch(control) {
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
                player.moveRight();
                getScreen().updateHud();
                break;
            case "previnv":
                player.moveLeft();
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
                    ((RangedWeapon) item).reload();
                }
                break;
        }
    }

    /**
     * Method to handle movement when the player presses a movement key.
     * @param dir Direction to apply force to
     * @param isPress Whether player is pressing or releasing the key
     */
    private void handleMovementKey(String dir, boolean isPress) {
        if (isPress == states.get(dir)) {
            return;
        }
        int sign = isPress ? 1 : -1;
        if (dir.equals("left") || dir.equals("down")) {
            sign *= -1;
        }
        states.put(dir, isPress);
        double accel = Vars.d("accel") / Vars.i("sv_tickrate") / Vars.i("sv_tickrate");
        if (dir.equals("left") || dir.equals("right")) {
            accelX += sign * accel;
            accelX = round(accelX);
        } else {
            accelY += sign * accel;
            accelY = round(accelY);
        }
    }

    /**
     * Rounds a number to (precision) digits.
     * @param number Number to round
     * @return Returns the rounded number.
     */
    private double round(double number) {
        return Math.round(number * Vars.d("precision")) / Vars.d("precision");
    }

    /**
     * Shortcut to refresh/draw the frame.
     */
    private void refresh() {
        Platform.runLater(() -> RoomRenderer.drawFrame(getScreen().getCanvas(), room, player));
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

    public boolean isRunning() {
        return isRunning;
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
            long startTime = System.nanoTime();

            //move the player
            managePlayerMovement();

            //check item pickup
            manageItemPickup();

            //drop the items
            manageItemDropping();

            //lower cooldowns
            manageCooldowns();

            //check for projectiles
            manageProjectiles();

            //check for player attacking
            managePlayerAttack();

            //Manage Monsters
            manageMonsters();

            //manage items (bombs)
            if (manageObstacleItems()) {
                return;
            }

            //manage player status effects
            managePlayerEffects();

            //draw the frame
            refresh();

            //update velocity
            updatePlayerVelocity();

            if (Vars.DEBUG) {
                long endTime = System.nanoTime();
                double execTime = round((endTime - startTime) / 1000000.0); //in milliseconds
                totalTime += execTime;
            }
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
                newPosX = movePos.getX();
                newPosY = movePos.getY();

                //check collisions with obstacles
                Obstacle[] obs = room.getObstacles().toArray(new Obstacle[0]);
                Collision<Obstacle> check = checkCollisions(obs, player, new Coords(newPosX, newPosY));
                if (check.getCollider() == null) {
                    //set player sprite
                    int dir;
                    if (newPosX < posX) {
                        dir = 4;
                    } else if (newPosX > posX) {
                        dir = 6;
                    } else if (newPosY > posY) {
                        dir = 5;
                    } else {
                        dir = 7;
                    }
                    player.setDirection(dir);

                    //check for door intersections
                    if (checkDoors(new Coords(newPosX, newPosY))) {
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
                                if (weaponAmmo != null && weaponAmmo.getProjectile() != null && weaponAmmo.getProjectile().equals(a.getProjectile())) {
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

                    //check for item existing in inventory
                    for (InventoryItem item : player.getInventory()) {
                        if (item != null) {
                            //if item exists and is not max stack
                            if (item.getItem().equals(d.getItem()) && item.getQuantity() < item.getItem().getMaxStackSize()) {
                                item.setQuantity(item.getQuantity() + 1);
                                room.getDroppedItems().remove(i);
                                i--;
                                itemPickedUp = true;
                                continue droploop;
                            }
                        }
                    }
                    //not in inventory or inventory items are full
                    if (!player.getInventory().full()) {
                        player.getInventory().add(d.getItem());
                        //remove dropped item
                        room.getDroppedItems().remove(i);
                        i--;
                        itemPickedUp = true;
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
            if (!states.get("drop") || currentItem == null) {
                return;
            }
            //remove from inventory
            if (currentItem.getQuantity() > 1) {
                currentItem.setQuantity(currentItem.getQuantity() - 1);
            } else {
                if (!player.getInventory().remove(currentItem)) {
                    return;
                }
            }
            double d = Vars.d("sv_dropitem_distance");
            //get player center
            double x = player.getX() + player.getWidth() / 2;
            double y = player.getY() + player.getHeight() / 2;
            Image itemSprite = currentItem.getItem().getSprite();
            int dir = player.getDirection() % 4;
            x += dir == 0 ? -d : (dir == 2 ? d : 0);
            y += dir == 3 ? -d : (dir == 1 ? d : 0);
            x -= itemSprite.getWidth() / 2;
            y -= itemSprite.getHeight() / 2;

            //keep inside room
            Coords check = checkPos(new Coords(x, y), itemSprite.getWidth(), itemSprite.getHeight());
            x = check.getX();
            y = check.getY();

            DroppedItem di = new DroppedItem(currentItem.getItem(), x, y, itemSprite.getWidth(), itemSprite.getHeight());
            room.getDroppedItems().add(di);
            Platform.runLater(() -> getScreen().updateHud());
        }

        /**
         * Manages all cooldowns.
         */
        private void manageCooldowns() {
            //lower player attack cooldown
            if (player.getAttackCooldown() > 0) {
                player.setAttackCooldown(Math.max(0.0, player.getAttackCooldown() - tickTime()));
            }

            //lower held weapon delay if rangedweapon
            Item item = player.getItemSelected() != null ? player.getItemSelected().getItem() : null;
            if (item instanceof RangedWeapon && ((RangedWeapon) item).getDelay() > 0) {
                RangedWeapon weapon = (RangedWeapon) item;
                weapon.setDelay(Math.max(0, weapon.getDelay() - tickTime()));
                if (weapon.isReloading() && weapon.getDelay() == 0) {
                    weapon.finishReloading();
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
                    Collision<Monster> c = checkCollisions(room.getMonsters().toArray(new Monster[0]), p, new Coords(newX, newY));
                    Monster m = c.getCollider();
                    //hit a monster
                    if (m != null) {
                        p.hit(m);
                    } else {
                        //keep moving
                        p.setX(newX);
                        p.setY(newY);

                        //calculate distance
                        double d = Math.sqrt(Math.pow(p.getVelX() / Vars.i("sv_tickrate"), 2) + Math.pow(p.getVelY() / Vars.i("sv_tickrate"), 2));
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
                for (Monster m : room.getMonsters()) {
                    if (m != null) {
                        double dist = distance(player, m);
                        if (dist <= Vars.i("sv_player_attack_range")) {
                            m.attackMonster(modifier * damage, true);
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
                if (weaponAmmo.getRemaining() <= 0) {
                    weapon.reload();
                    return;
                }

                states.put("firing", false);

                //set fire delay
                weapon.setDelay(weapon.getFireRate() * 1000);

                //reduce ammo
                weaponAmmo.setRemaining(weaponAmmo.getRemaining() - 1);

                //update ammo on HUD
                Platform.runLater(() -> getScreen().updateHud());

                //create projectile
                int dir = player.getDirection() % 4;
                double x = player.getX() + player.getWidth() / 2;
                double y = player.getY() + player.getHeight();
                Image sprite = weaponAmmo.getProjectile().getSpriteLeft();
                if (dir == 1) {
                    sprite = weaponAmmo.getProjectile().getSpriteUp();
                } else if (dir == 2) {
                    sprite = weaponAmmo.getProjectile().getSpriteRight();
                } else if (dir == 3) {
                    sprite = weaponAmmo.getProjectile().getSpriteDown();
                }
                double height = sprite.getHeight();
                double width = sprite.getWidth();
                if (dir == 0) {
                    x -= 5;
                } else if (dir == 2) {
                    x += 5;
                } else if (dir == 1) {
                    y += 5;
                } else {
                    y -= 5;
                }
                x -= width / 2;
                y -= height / 2;

                //reset x and y coordinates
                Coords check = checkPos(new Coords(x, y), width, height);
                x = check.getX();
                y = check.getY();


                //velocity
                double speed = weaponAmmo.getProjectile().getSpeed();
                double velX = dir == 0 ? -speed : (dir == 2 ? speed : 0);
                double velY = dir == 1 ? speed : (dir == 3 ? -speed : 0);

                //create projectile
                ShotProjectile sp = new ShotProjectile(weaponAmmo.getProjectile(), x, y, velX, velY, width, height);
                sp.setSprite(sprite);
                room.getProjectiles().add(sp);
            }
        }

        /**
         * Manages monsters
         */
        private void manageMonsters() {
            for (Monster m : room.getMonsters()) {
                if (m == null || m.getHealth() == 0) {
                    continue;
                }
                //check and move the monster
                if (monsterMove(m)) {
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
                        ShotProjectile.addExplosion(room, o, b.getRadius() * 2);

                        if (dist <= b.getRadius()) {
                            player.setHealth(Math.max(0, player.getHealth() - b.getDamage() * Vars.d("sv_self_damage_modifier")));
                            Platform.runLater(() -> getScreen().updateHud());
                            if (player.getHealth() == 0) {
                                gameOver();
                                return true;
                            }
                        }

                        //get all entities within range of the bomb
                        for (Monster m : room.getMonsters()) {
                            dist = distance(m, o);
                            if (dist <= b.getRadius()) {
                                m.attackMonster(b.getDamage(), true);
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
            double originalVelX = velX;
            double originalVelY = velY;
            velX += accelX;
            velX = round(velX);
            velY += accelY;
            velY = round(velY);

            //don't allow speed to exceed max
            double maxVel = Vars.d("sv_max_velocity") / Vars.i("sv_tickrate");
            double friction = Vars.d("friction") / Vars.i("sv_tickrate") / Vars.i("sv_tickrate");
            if (Math.abs(velX) > maxVel) {
                velX = (velX > 0 ? 1 : -1) * maxVel;
                //was moving before and decelerated to 0
            } else if (states.get("frictionX") && Math.abs(originalVelX) < Math.abs(accelX)) {
                if (velY == 0) {
                    player.setDirection((originalVelX > 0) ? 2 : 0);
                }
                velX = 0;
                accelX -= (accelX > 0 ? 1 : -1) * friction;
                states.put("frictionX", false);
            }
            if (Math.abs(velY) > maxVel) {
                velY = (velY > 0 ? 1 : -1) * maxVel;
            } else if (states.get("frictionY") && Math.abs(originalVelY) < Math.abs(accelY)) {
                if (velX == 0) {
                    player.setDirection((originalVelY > 0) ? 1 : 3);
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
        private boolean inRange(Movable o, Movable m, Coords newPos) {
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
         * Checks if a Movable object collides with any objects in a list.
         * @param list List of objects to check
         * @param m Object that is moving
         * @param newPos New coordinates of the object m
         * @param <T> Type of object to check
         * @return Returns with the collision data of the collision, with null values if no collision
         */
        private <T extends Movable> Collision<T> checkCollisions(T[] list, Movable m, Coords newPos) {
            double x = m.getX();
            double y = m.getY();
            double newX = newPos.getX();
            double newY = newPos.getY();
            for (T t: list) {
                if (t == null || t == m) continue;

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
                    return player.getInventory().contains(DataManager.getExitKey());

                    // if in challenge room, don't let player leave if not completed
                } else if (room instanceof ChallengeRoom && !((ChallengeRoom) room).isCompleted()) {
                    return false;
                }

                //check if not visited & if there are still monsters
                if (!newRoom.wasVisited()) {
                    for (Monster m : room.getMonsters()) {
                        if (m != null && m.getHealth() > 0) {
                            return false;
                        }
                    }
                }

                Door newDoor;
                double newStartX;
                double newStartY;
                if (newRoom.getType() == RoomType.EXITROOM) {
                    newStartX = (newRoom.getWidth() + player.getWidth()) / 2;
                    newStartY = 20;
                } else if (d.equals(room.getTopDoor())) {
                    newDoor = newRoom.getBottomDoor();
                    newStartX = newDoor.getX() + newDoor.getWidth() / 2.0
                            - player.getWidth() / 2;
                    newStartY = newDoor.getY() + LayoutGenerator.DOORBOTTOM_HEIGHT + 10;
                } else if (d.equals(room.getBottomDoor())) {
                    newDoor = newRoom.getTopDoor();
                    newStartX = newDoor.getX() + newDoor.getWidth() / 2.0
                            - player.getWidth() / 2;
                    newStartY = newDoor.getY() - player.getHeight() - 1;
                } else if (d.equals(room.getRightDoor())) {
                    newDoor = newRoom.getLeftDoor();
                    newStartX = newDoor.getX() + 10 + LayoutGenerator.DOOR_WIDTH;
                    newStartY = newDoor.getY() + newDoor.getHeight() / 5.0;
                } else {
                    newDoor = newRoom.getRightDoor();
                    newStartX = newDoor.getX() - 10 - player.getWidth();
                    newStartY = newDoor.getY() + newDoor.getHeight() / 5.0;
                }
                newRoom.setStartX((int) newStartX);
                newRoom.setStartY((int) newStartY);
                setRoom(newRoom);
                return true;
            }
            return false;
        }

        /**
         * Gets the intersection point of Movable mo's movement equation with an object o
         * @param o Object to check collision with
         * @param mo Object that is moving
         * @param eq Equation of object mo's movement
         * @param moveUp Whether mo is moving up or down
         * @param moveRight Whether mo is moving right or left
         * @return The coordinates of the intersection point
         */
        private Coords getIntersect(Movable o, Movable mo, Equation eq, boolean moveUp, boolean moveRight) {
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
            double m = eq.getSlope();
            double b = eq.getIntercept();
            double w = mo.getWidth();
            double h = mo.getHeight();

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
         * Returns an equation for a specified vector
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
                eq.setVertical(x0);
            }
            return eq;
        }

        /**
         * Calculates the shortest distance between two objects.
         * @param a First object to check
         * @param b Second object to check
         * @return Returns the distance between the two objects
         */
        private double distance(Movable a, Movable b) {
            double distX = (a.getX() + a.getWidth() / 2) - (b.getX() + b.getWidth() / 2);
            double distY = (a.getY() + a.getHeight() / 2) - (b.getY() + b.getHeight() / 2);
            return round(Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2)));
        }

        /**
         * Calculates the angle of the shortest distance between two objects.
         * @param a First object to check
         * @param b Second object to check
         * @return Angle of the distance between the two objects
         */
        private double angle(Movable a, Movable b) {
            double distX = (a.getX() + a.getWidth() / 2) - (b.getX() + b.getWidth() / 2);
            double distY = (a.getY() + a.getHeight() / 2) - (b.getY() + b.getHeight() / 2);
            return Math.atan2(distY, distX);
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
            Move mq = (m.getMoveQueue().size() > 0) ? m.getMoveQueue().get(m.getMoveQueue().size() - 1) : new Move(new Coords(m.getX(), m.getY()), 0);
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
                //move monster towards player
                double angle = angle(md, player) - Math.PI;
                double speed = m.getSpeed() / Vars.i("sv_tickrate");
                double newPosX = mPosX + round(Math.cos(angle) * speed);
                double newPosY = mPosY + round(Math.sin(angle) * speed);

                //check collisions with obstacles
                Coords newPos = checkPos(new Coords(newPosX, newPosY), m.getWidth(), m.getHeight());

                //add to queue
                Move moveItem = new Move(newPos, 0);
                m.getMoveQueue().add(moveItem);
            }
            d = distance(m, player);

            //check for current attack
            if (d <= Vars.i("ai_monster_attack_range")) {
                if (checkAttack(m)) {
                    //set attack cooldown
                    m.setAttackCooldown(m.getAttackSpeed() * 1000);
                    //attack player
                    double newHealth = player.getHealth() - m.getAttack();
                    player.setHealth(Math.max(0, newHealth));

                    //use run later to prevent any thread issues
                    Platform.runLater(() -> getScreen().updateHud());

                    //go to game over screen if player has died
                    if (player.getHealth() == 0) {
                        //use run later to prevent any thread issues
                        refresh();
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
            Console.print("Game Over");
            GameScreen screen = getScreen();
            Platform.runLater(() -> {
                room = screen.getLayout().getStartingRoom();
                screen.gameOver();
            });
        }
    }
}
