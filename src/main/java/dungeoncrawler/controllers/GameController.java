package dungeoncrawler.controllers;

import dungeoncrawler.gamestates.GameState;
import dungeoncrawler.handlers.Controls;
import dungeoncrawler.handlers.RoomRenderer;
import dungeoncrawler.objects.*;

import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.handlers.GameSettings;
import dungeoncrawler.handlers.LayoutGenerator;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class for running the game. Does all the calculations and stuff.
 *
 * @author Kevin Zhao, Manas Harbola
 * @version 1.0
 */
public class GameController {
    private Timer timer;
    private Controls controls;
    private Room room;
    private Scene scene;
    private Player player;
    private double velX;
    private double velY;
    private double accelX;
    private double accelY;
    private boolean isRunning;
    private boolean isStopped;
    private long ticks;
    private double totalTime;

    //boolean variables for tracking event
    private boolean pressLeft;
    private boolean pressRight;
    private boolean pressUp;
    private boolean pressDown;
    private boolean frictionX;
    private boolean frictionY;
    private boolean isAttacking;
    private boolean isFiring;

    /**
     * Secondary constructor for no player
     */
    public GameController() {
        this.controls = new Controls();
    }

    /**
     * Starts the game.
     * @param room Current/first room
     */
    public void start(Room room) {
        reset();

        //Render room
        setRoom(room);
        scene = Controller.getState().getScene();

        //Handle key events
        scene.setOnKeyPressed(e -> handleKey(e.getCode().toString(), true));
        scene.setOnKeyReleased(e -> handleKey(e.getCode().toString(), false));
        scene.setOnMousePressed(e -> handleKey(mouseButton(e.getButton()), true));
        scene.setOnMouseReleased(e -> handleKey(mouseButton(e.getButton()), false));
        scene.setOnScroll(e -> handleKey(handleScroll(e.getDeltaY()), false));
    }

    /**
     * Handles mousebutton events and returns the appropriate button name.
     * @param button MouseButton event
     * @return Returns the corresponding button name
     */
    private String mouseButton(MouseButton button) {
        if (button == MouseButton.PRIMARY) {
            return "MOUSE1";
        } else if (button == MouseButton.SECONDARY) {
            return "MOUSE2";
        }
        return "";
    }

    private GameScreen getScreen() {
        GameState state = Controller.getState();
        if (!(state instanceof GameScreen)) {
            throw new IllegalStateException("Illegal Gamestate");
        }
        return (GameScreen) state;
    }

    /**
     * Handle when the player uses the mouse scroll wheel.
     * @param val Scroll length
     * @return String of the keycode
     */
    private String handleScroll(double val) {
        if (val < 0) {
            return "MWHEELDOWN";
        } else if (val > 0) {
            return "MWHEELUP";
        } else {
            return "";
        }
    }

    /**
     * Sets the player object of the game.
     * @param player Player node
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
     * Resets the player's position to the starting position.
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
        velX = 0.0;
        velY = 0.0;
        accelX = 0.0;
        accelY = 0.0;
        isRunning = false;
        isStopped = false;
        pressLeft = false;
        pressRight = false;
        pressUp = false;
        pressDown = false;
        frictionX = false;
        frictionY = false;
        isAttacking = false;
        isFiring = false;
        ticks = 0;
        totalTime = 0;
    }

    /**
     * Pauses/resumes the game.
     */
    public void pause() {
        if (isRunning) {
            //System.out.println("Game has been paused");
            //System.out.println("Average FPS in " + ticks + " ticks: " + round(1000.0
            //        / (totalTime / ticks)));
            timer.cancel();
        } else {
            //System.out.println("Game has been resumed");
            startTimer();
        }
        if (!isRunning && isStopped) {
            isStopped = false;
        }
        isRunning = !isRunning;
    }
    public void stop() {
        isRunning = true;
        pause();
        isStopped = true;
        //System.out.println("Game has been stopped.");
    }

    /**
     * Starts the game timer/clock.
     */
    private void startTimer() {
        refresh();
        timer = new Timer();
        timer.schedule(new GameRunner(), 0, 1000 / GameSettings.FPS);
    }

    /**
     * Handler for key events.
     * @param key KeyCode of the key that was pressed
     * @param isPress Whether the event is a press or release event
     */
    private void handleKey(String key, boolean isPress) {
        //movement keys
        int sign = 0;
        boolean xval = false;
        if (key.equals(controls.getKey("up"))) {
            if (isPress == pressUp) {
                return;
            }
            sign = isPress ? 1 : -1;
            pressUp = isPress;
        } else if (key.equals(controls.getKey("down"))) {
            if (isPress == pressDown) {
                return;
            }
            sign = isPress ? -1 : 1;
            pressDown = isPress;
        } else if (key.equals(controls.getKey("right"))) {
            if (isPress == pressRight) {
                return;
            }
            sign = isPress ? 1 : -1;
            pressRight = isPress;
            xval = true;
        } else if (key.equals(controls.getKey("left"))) {
            if (isPress == pressLeft) {
                return;
            }
            sign = isPress ? -1 : 1;
            pressLeft = isPress;
            xval = true;
        }
        if (xval) {
            accelX += sign * GameSettings.ACCEL;
            accelX = round(accelX);
        } else {
            accelY += sign * GameSettings.ACCEL;
            accelY = round(accelY);
        }

        if (isStopped) {
            return;
        }

        //Global key binds, regardless of game play/pause state
        if (key.equals(controls.getKey("pause"))) {
            if (!isPress) {
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
        } else if (key.equals(controls.getKey("inventory"))) {
            if (!isPress) {
                if (!getScreen().isPaused()) {
                    pause();
                    getScreen().toggleInventory();
                }
            }
        }

        if (!isRunning) {
            return;
        }

        //non-movement keys
        if (key.equals(controls.getKey("attack"))) {
            isAttacking = isPress;
        } else if (key.equals(controls.getKey("attack2"))) {
            isFiring = isPress;
        } else if (key.equals(controls.getKey("use"))) {
            if (isPress) {
                InventoryItem selected = player.getItemSelected();
                if (selected != null) {
                    selected.getItem().use();
                }
            }
        } else if (key.equals(controls.getKey("nextinv"))) {
            player.moveRight();
            getScreen().updateHud();
        } else if (key.equals(controls.getKey("previnv"))) {
            player.moveLeft();
            getScreen().updateHud();
        } else if (key.equals(controls.getKey("drop"))) {
            InventoryItem currentItem = player.getItemSelected();
            if (currentItem == null || !isPress) {
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
            double d = GameSettings.DROP_ITEM_DISTANCE;
            double x = player.getX() + player.getWidth() / 2;
            double y = player.getY() + player.getHeight() / 2;
            Image itemSprite = currentItem.getItem().getSprite();
            int dir = player.getDirection() % 4;
            x += dir == 0 ? -d : (dir == 2 ? d : 0);
            y += dir == 3 ? -d : (dir == 1 ? d : 0);
            x -= itemSprite.getWidth() / 2;
            y -= itemSprite.getHeight() / 2;

            //keep inside room
            x = Math.max(0, x);
            y = Math.max(0, y);
            x = Math.min(room.getWidth() - itemSprite.getWidth(), x);
            y = Math.min(room.getHeight() - itemSprite.getHeight(), y);

            DroppedItem di = new DroppedItem(currentItem.getItem(), x, y, itemSprite.getWidth(),
                    itemSprite.getHeight());
            room.getDroppedItems().add(di);
            getScreen().updateHud();
        } else if (key.equals(controls.getKey("rotateinv"))) {
            if (isPress) {
                player.getInventory().rotate();
                getScreen().updateHud();
            }
        } else if (key.equals(controls.getKey("reload"))) {
            //get held weapon
            Item item = player.getItemSelected() != null
                    ? player.getItemSelected().getItem() : null;
            if (item instanceof RangedWeapon) {
                ((RangedWeapon) item).reload();
            }
        }
    }

    /**
     * Rounds a number to (precision) digits.
     * @param number Number to round
     * @return Returns the rounded number.
     */
    private double round(double number) {
        return Math.round(number * GameSettings.PRECISION) / GameSettings.PRECISION;
    }

    private void refresh() {
        Platform.runLater(() -> RoomRenderer.drawFrame(getScreen().getCanvas(), room, player));
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
                System.out.println("Invalid Run Instance!");
                this.cancel();
                return;
            }

            ticks++;
            long startTime = System.nanoTime();
            double posX = player.getX();
            double posY = player.getY();
            double newPosX = round(posX + velX);
            double newPosY = round(posY + velY);

            //check if position is valid. If it is, move.
            double[] movePos = checkPos(posX, posY, newPosX, newPosY, player.getHeight(),
                    player.getWidth());
            if (movePos[0] != posX || movePos[1] != posY) {
                newPosX = movePos[0];
                newPosY = movePos[1];

                //check collisions with obstacles
                ArrayList<Obstacle> obstacles = room.getObstacles();
                Obstacle[] obs = new Obstacle[obstacles.size()];
                for (int i = 0; i < obstacles.size(); i++) {
                    obs[i] = obstacles.get(i);
                }
                Obstacle checkObs = (Obstacle) checkCollisions(
                        obs, player, posX, posY, newPosX, newPosY)[0];
                if (checkObs == null) {

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
                    if (checkDoors(posX, posY, newPosX, newPosY)) {
                        return;
                    }
                    player.setX(newPosX);
                    player.setY(newPosY);
                }
            }

            //check item pickup
            pickupItems();

            //lower cooldowns
            checkCooldowns();

            //check for projectiles
            checkProjectiles();

            //check for player attacking
            checkPlayerAttack();

            //Manage Monsters
            monsterAI();

            //manage items (bombs)
            if (checkObstacleItems()) {
                return;
            }


            for (int i = 0; i < player.getEffects().size(); i++) {
                Effect e = player.getEffects().get(i);
                e.setDuration(e.getDuration() - 1000 / GameSettings.FPS);
                if (e.getDuration() <= 0) {
                    player.getEffects().remove(i--);
                    Platform.runLater(() -> getScreen().updateHud());
                }
            }


            refresh();

            //update velocity
            updatePlayerVelocity();

            long endTime = System.nanoTime();
            double execTime = round((endTime - startTime) / 1000000.0); //in milliseconds
            totalTime += execTime;
        }

        private void pickupItems() {
            boolean itemPickedUp = false;
            droploop:
            for (int i = 0; i < room.getDroppedItems().size(); i++) {
                DroppedItem d = room.getDroppedItems().get(i);
                double distX = (player.getX() + player.getWidth() / 2) - (d.getX()
                        + d.getWidth() / 2);
                double distY = (player.getY() + player.getHeight() / 2) - (d.getY()
                        + d.getHeight() / 2);
                double dist = round(Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2)));
                //pick up item
                if (dist <= GameSettings.PLAYER_PICKUP_RANGE) {
                    //check if ammunition
                    if (d.getItem() instanceof Ammunition) {
                        //check if a weapon that uses this ammunition exists
                        for (InventoryItem[] row : player.getInventory().getItems()) {
                            for (InventoryItem item : row) {
                                if (item == null) {
                                    continue;
                                }
                                if (item.getItem() instanceof RangedWeapon) {
                                    Ammo ammo = ((RangedWeapon) item.getItem()).getAmmo();
                                    Ammunition a = (Ammunition) d.getItem();
                                    if (ammo == null || ammo.getProjectile() == null) {
                                        continue;
                                    }
                                    if (ammo.getProjectile().equals(a.getProjectile())) {
                                        int maxChange = ammo.getBackupMax()
                                                - ammo.getBackupRemaining();
                                        if (a.getAmount() <= maxChange) {
                                            ammo.setBackupRemaining(ammo.getBackupRemaining()
                                                    + a.getAmount());
                                            itemPickedUp = true;
                                            room.getDroppedItems().remove(i);
                                            i--;
                                        } else {
                                            ammo.setBackupRemaining(ammo.getBackupMax());
                                            a.setAmount(a.getAmount() - maxChange);
                                        }
                                        continue droploop;
                                    }
                                }
                            }
                        }
                        //don't do anything if player can't pick up the ammo
                        continue;
                    }

                    //check for item existing in inventory
                    for (InventoryItem[] row : player.getInventory().getItems()) {
                        for (InventoryItem item : row) {
                            if (item == null) {
                                continue;
                            }
                            //if item exists and is not max stack
                            if (item.getItem().equals(d.getItem()) && item.getQuantity()
                                    < item.getItem().getMaxStackSize()) {
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

        private void checkCooldowns() {
            //lower player attack cooldown
            if (player.getAttackCooldown() > 0) {
                player.setAttackCooldown(Math.max(0.0, player.getAttackCooldown() - 1000.0
                        / GameSettings.FPS));
            }
            //lower held weapon delay if rangedweapon
            Item item = player.getItemSelected() != null  ? player.getItemSelected().getItem()
                    : null;
            if (item instanceof RangedWeapon && ((RangedWeapon) item).getDelay() > 0) {
                RangedWeapon weapon = (RangedWeapon) item;
                weapon.setDelay(Math.max(0, weapon.getDelay() - 1000 / GameSettings.FPS));
                if (weapon.isReloading() && weapon.getDelay() == 0) {
                    weapon.finishReloading();
                }
            }
        }

        private void checkProjectiles() {
            for (int i = 0; i < room.getProjectiles().size(); i++) {
                ShotProjectile p = room.getProjectiles().get(i);
                //move
                double x = p.getX();
                double y = p.getY();
                double newX = x + p.getVelX();
                double newY = y + p.getVelY();

                //check collisions
                double[] checked = checkPos(x, y, newX, newY, p.getHeight(), p.getWidth());
                //hit an object, then explode
                if (checked == null || (checked[0] == x && checked[1] == y)) {
                    p.hit(null);
                    i--;
                } else {
                    //check for entity collisions
                    newX = checked[0];
                    newY = checked[1];
                    Monster m = (Monster) checkCollisions(
                            room.getMonsters(), p, x, y, newX, newY)[0];
                    //hit a monster
                    if (m != null) {
                        p.hit(m);
                    } else {
                        //keep moving
                        p.setX(newX);
                        p.setY(newY);

                        //calculate distance
                        double d = Math.sqrt(Math.pow(p.getVelX(), 2) + Math.pow(p.getVelY(), 2));
                        p.setDistance(round(p.getDistance() + d));
                        if (p.getDistance() >= p.getProjectile().getRange()) {
                            p.hit(null);
                            i--;
                        }
                    }
                }
            }
        }

        private void checkPlayerAttack() {
            Item item = player.getItemSelected() != null
                    ? player.getItemSelected().getItem() : null;
            if (isAttacking && player.getAttackCooldown() == 0.0) {
                isAttacking = false;
                double damage = GameSettings.PLAYER_FIST_DAMAGE;
                double cooldown = GameSettings.PLAYER_FIST_COOLDOWN;
                double modifier = player.getAttack();
                if (item instanceof Weapon) {
                    Weapon weapon = (Weapon) item;
                    damage = weapon.getDamage();
                    cooldown = weapon.getAttackSpeed();
                }
                //check for effects
                for (int i = 0; i < player.getEffects().size(); i++) {
                    Effect e = player.getEffects().get(i);
                    if (e.getType() == EffectType.ATTACKBOOST) {
                        modifier += e.getAmount();
                    }
                }
                player.setAttackCooldown(1000 * cooldown);
                for (Monster m : room.getMonsters()) {
                    if (m != null) {
                        double dist = Math.sqrt(Math.pow(player.getX() - m.getX(), 2)
                                + Math.pow(player.getY() - m.getY(), 2));
                        if (dist <= GameSettings.PLAYER_ATTACK_RANGE) {
                            m.attackMonster(modifier * damage, true);
                        }
                    }
                }
            }

            //firing ranged weapon
            if (isFiring && item instanceof RangedWeapon) {
                RangedWeapon weapon = (RangedWeapon) item;
                if (weapon.getDelay() > 0) {
                    return;
                }

                Ammo ammo = weapon.getAmmo();

                //check for ammo
                if (ammo.getRemaining() <= 0) {
                    weapon.reload();
                    return;
                }

                isFiring = false;

                //set fire delay
                weapon.setDelay(weapon.getFireRate() * 1000);

                //reduce ammo
                ammo.setRemaining(ammo.getRemaining() - 1);

                //update ammo on HUD
                Platform.runLater(() -> getScreen().updateHud());

                //create projectile
                int dir = player.getDirection() % 4;
                double x = player.getX() + player.getWidth() / 2;
                double y = player.getY() + player.getHeight();
                Image sprite = ammo.getProjectile().getSpriteLeft();
                if (dir == 1) {
                    sprite = ammo.getProjectile().getSpriteUp();
                } else if (dir == 2) {
                    sprite = ammo.getProjectile().getSpriteRight();
                } else if (dir == 3) {
                    sprite = ammo.getProjectile().getSpriteDown();
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
                if (x < 0) {
                    x = 0;
                } else if (x > room.getWidth() - width) {
                    x = room.getWidth() - width;
                }
                if (y < 0) {
                    y = 0;
                } else if (y > room.getHeight() - height) {
                    x = room.getHeight() - height;
                }

                //velocity
                double speed = ammo.getProjectile().getSpeed();
                double velX = dir == 0 ? -speed : (dir == 2 ? speed : 0);
                double velY = dir == 1 ? speed : (dir == 3 ? -speed : 0);

                //create projectile
                ShotProjectile sp = new ShotProjectile(ammo.getProjectile(), x, y, velX, velY,
                        width, height);
                sp.setSprite(sprite);
                room.getProjectiles().add(sp);
            }
        }

        private void monsterAI() {
            for (int i = 0; i < room.getMonsters().length; i++) {
                Monster m = room.getMonsters()[i];
                if (m == null || m.getHealth() == 0) {
                    continue;
                }
                //check and move the monster
                if (m.getHealth() > 0) {
                    if (monsterMove(m)) {
                        return;
                    }
                }
            }
        }

        private boolean checkObstacleItems() {
            for (int i = 0; i < room.getObstacles().size(); i++) {
                Obstacle o = room.getObstacles().get(i);
                if (o.getItem() == null) {
                    continue;
                }
                //has an item linked
                Item item = o.getItem();
                if (item instanceof Bomb) {
                    Bomb b = (Bomb) item;
                    b.setLivefuse(b.getLivefuse() - 1000 / GameSettings.FPS);
                    //if bomb has blown up
                    if (b.getLivefuse() < 0) {
                        double x = o.getX() + o.getHeight() / 2;
                        double y = o.getY() + o.getWidth() / 2;

                        //attack player
                        double distX = Math.pow(x - player.getX() + player.getWidth() / 2, 2);
                        double distY = Math.pow(y - player.getY() + player.getHeight() / 2, 2);
                        double dist = Math.sqrt(distX + distY);

                        //draw explosion
                        ShotProjectile.addExplosion(room, o, b.getRadius() * 2);

                        if (dist <= b.getRadius()) {
                            player.setHealth(Math.max(0, player.getHealth()
                                    - b.getDamage() * GameSettings.PLAYER_ATTACK_SELF_MODIFIER));
                            Platform.runLater(() -> getScreen().updateHud());
                            if (player.getHealth() == 0) {
                                gameOver();
                                return true;
                            }
                        }

                        //get all entities within range of the bomb
                        for (Monster m : room.getMonsters()) {
                            distX = Math.pow(x - m.getX() + m.getWidth() / 2, 2);
                            distY = Math.pow(y - m.getY() + m.getHealth() / 2, 2);
                            dist = Math.sqrt(distX + distY);
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

        private void updatePlayerVelocity() {
            double ovx = velX;
            double ovy = velY;
            velX += accelX;
            velX = round(velX);
            velY += accelY;
            velY = round(velY);

            //don't allow speed to exceed max
            if (Math.abs(velX) >= GameSettings.MAX_VEL) {
                velX = (velX > 0 ? 1 : -1) * GameSettings.MAX_VEL;
                //was moving before and decelerated to 0
            } else if (frictionX && Math.abs(ovx) <= Math.abs(accelX)) {
                if (velY == 0) {
                    player.setDirection((ovx > 0) ? 2 : 0);
                }
                velX = 0;
                accelX -= (accelX > 0 ? 1 : -1) * GameSettings.FRICTION;
                frictionX = false;
            }
            if (Math.abs(velY) >= GameSettings.MAX_VEL) {
                velY = (velY > 0 ? 1 : -1) * GameSettings.MAX_VEL;
            } else if (frictionY && Math.abs(ovy) <= Math.abs(accelY)) {
                if (velX == 0) {
                    player.setDirection((ovy > 0) ? 1 : 3);
                }
                velY = 0;
                accelY -= (accelY > 0 ? 1 : -1) * GameSettings.FRICTION;
                frictionY = false;
            }
            //apply friction if not currently moving forward
            if (accelX == 0 && velX != 0) {
                frictionX = true;
                accelX += (velX > 0 ? -1 : 1) * GameSettings.FRICTION;
            }
            if (accelY == 0 && velY != 0) {
                frictionY = true;
                accelY += (velY > 0 ? -1 : 1) * GameSettings.FRICTION;
            }
        }

        /**
         * Method to check if a new position is valid.
         * @param x current x value
         * @param y current y value
         * @param newX new x value
         * @param newY new y value
         * @param h height
         * @param w width
         * @return Returns whether the movement is valid
         */
        private double[] checkPos(double x, double y, double newX,
                                  double newY, double h, double w) {
            if (newX < 0.0 || newX + w > room.getWidth()) {
                return new double[]{(newX < 0 ? 0 : room.getWidth()
                        - w), newY};
            }
            if (newY < 0.0 || newY + h > room.getHeight()) {
                return new double[]{newX, (newY < 0 ? 0 : room.getHeight()
                        - h)};
            }
            return new double[]{newX, newY};
        }

        /**
         * Checks if an object is in the range of the movement vector specified.
         * @param o Object to check for
         * @param x x position
         * @param y y position
         * @param newX new x position
         * @param newY new y position
         * @param h height
         * @param w width
         * @return Returns whether the object is in range of the player's movement
         */
        private boolean inRange(Movable o, double x, double y,
                                double newX, double newY, double h, double w) {
            /* Checks if the object is in range of the player's movement
             *          _________
             *          |[]     |
             *          |  \    |
             *          |   \   |
             *          |    \  |
             *          |     []|
             *          ---------
             */

            if (o.getX() + o.getWidth() < Math.min(x, newX)
                || o.getX() > Math.max(x, newX) + w) {
                return false;
            }
            if (o.getY() + o.getHeight() < Math.min(y, newY)
                || o.getY() > Math.max(y, newY) + h) {
                return false;
            }
            return true;
        }

        private <T extends Movable> Object[] checkCollisions(
                T[] list, Movable m, double x, double y, double newX, double newY) {
            for (T t: list) {
                if (t == null) {
                    continue;
                }
                if (t == m) {
                    continue;
                }
                //Check if monster is out of movement vector rectangle
                if (!inRange(t, x, y, newX, newY,
                        m.getHeight(), m.getWidth())) {
                    continue;
                }
                //for monsters
                if (t instanceof Monster && ((Monster) t).getHealth() == 0) {
                    continue;
                } else if (t instanceof Obstacle
                        && ((Obstacle) t).getType() == ObstacleType.NONSOLID) {
                    continue;
                }

                //movement direction
                boolean moveRight = x < newX;
                boolean moveUp = y < newY;

                //Get equation for intersection
                double[] playerEquation = equation(x, y, newX, newY);
                double[] intersects = getIntersect(t, playerEquation[0], playerEquation[1],
                        moveUp, moveRight, m.getHeight(), m.getWidth());

                //intersects
                if (intersects != null) {
                    return new Object[]{t, intersects};
                }
            }
            return new Object[]{null, null};
        }

        /**
         * Checks if the player has entered any doors
         * @param x x position
         * @param y y position
         * @param newX new x position
         * @param newY y position
         * @return Whether the player has entered a door
         */
        private boolean checkDoors(double x, double y, double newX, double newY) {
            Door[] doors = {
                room.getTopDoor(),
                room.getBottomDoor(),
                room.getLeftDoor(),
                room.getRightDoor()
            };

            //loop through doors
            Door d = (Door) checkCollisions(doors, player, x, y, newX, newY)[0];
            if (d != null) {
                Room newRoom = d.getGoesTo();

                //if next room is the exit, don't let player go through unless they have key
                if (room.getType() == RoomType.EXITROOM && d instanceof ExitDoor) {
                    InventoryItem[][] playerItems = player.getInventory().getItems();
                    for (InventoryItem[] itemRow : playerItems) {
                        for (InventoryItem playerItem : itemRow) {
                            if (playerItem != null && playerItem.getItem() instanceof Key) {
                                stop();
                                Platform.runLater(() -> getScreen().win());
                                return true;
                            }
                        }
                    }
                    //no key
                    return false;

                    // if in challenge room, don't let player leave if not completed
                } else if (room.getType() == RoomType.CHALLENGEROOM
                        && !((ChallengeRoom) room).isCompleted()) {
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
                            - GameSettings.PLAYER_WIDTH / 2;
                    newStartY = newDoor.getY() + LayoutGenerator.DOORBOTTOM_HEIGHT + 10;
                } else if (d.equals(room.getBottomDoor())) {
                    newDoor = newRoom.getTopDoor();
                    newStartX = newDoor.getX() + newDoor.getWidth() / 2.0
                            - GameSettings.PLAYER_WIDTH / 2;
                    newStartY = newDoor.getY() - GameSettings.PLAYER_HEIGHT - 1;
                } else if (d.equals(room.getRightDoor())) {
                    newDoor = newRoom.getLeftDoor();
                    newStartX = newDoor.getX() + 10 + LayoutGenerator.DOOR_WIDTH;
                    newStartY = newDoor.getY() + newDoor.getHeight() / 5.0;
                } else {
                    newDoor = newRoom.getRightDoor();
                    newStartX = newDoor.getX() - 10 - GameSettings.PLAYER_WIDTH;
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
         * Math method to calculate the intersection point of a line/vector and an object.
         * @param o Object to check
         * @param m Slope of the line
         * @param b Y-intecept of the line
         * @param moveUp Whether the player is moving up
         * @param moveRight Whether the player is moving down
         * @param h height
         * @param w width
         * @return Returns the x and y coordinate of the intersection point, null if no intersection
         */
        private double[] getIntersect(Movable o, double m, double b, boolean moveUp,
                                      boolean moveRight, double h, double w) {
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
            double intY = (o.getY() + o.getHeight() - b) / m;
            if (moveUp) {
                intY = (o.getY() - h - b) / m;
            }
            //check for zero slope ie NaN intY
            if (m == 0) {
                intY = b;
            }
            /* if the player is moving vertically, then slope and y-intercept will be
             * infinity/undefined. If so, set the intY/x position of the intersect to the x
             * coordinate of the movement vector, which will be stored in the y-intercept variable
             */
            if (m == Double.POSITIVE_INFINITY || m == Double.NEGATIVE_INFINITY) {
                intY = b;
            }

            //check if intersect is on the obstacle
            if (intY <= o.getX() + o.getWidth() && intY + w >= o.getX()) {
                double coord = m * intY + b;
                return new double[]{intY, coord};
            }

            //Calculate y-coordinate intersection point on the x-axis, using y = mx + b
            double intX = m * (o.getX() + o.getWidth()) + b;
            if (moveRight) { //intersect is the right side of player to the left side of obstacle
                intX = m * (o.getX() - w) + b;
            }

            //check if intersect is on the obstacle
            if (intX <= o.getY() + o.getHeight() && intX + h >= o.getY()) {
                double coord = (intX - b) / m;
                if (m == 0) {
                    coord = o.getX() + ((moveRight) ? -w : o.getWidth());
                }
                return new double[]{coord, intX};
            }

            return null;
        }

        /**
         * To be implemented.
         */
        private void moveCamera() {
        }

        /**
         * Returns an equation for a specified vector
         * @param x0 first x position
         * @param y0 first y position
         * @param x1 second x position
         * @param y1 second y position
         * @return array of the slope and the y-intercept of the line.
         */
        private double[] equation(double x0, double y0, double x1, double y1) {
            double m = (y1 - y0) / (x1 - x0);
            double b = y0 - m * x0;

            //moving vertically
            if (x0 == x1 && y0 != y1) {
                b = x0;
            }
            return new double[]{m, b};
        }

        /**
         * Monster AI for calculating movement and attacking.
         * @param m Monster to calculate for
         * @return Returns if the program should stop
         */
        private boolean monsterMove(Monster m) {
            //check queue
            LinkedList<double[]> removeList = new LinkedList<>();
            for (double[] e : m.getMoveQueue()) {
                e[0] -= 1000.0 / GameSettings.FPS;
                //time to apply the move
                if (e[0] <= 0) {
                    double x = m.getX();
                    double y = m.getY();

                    m.setX(e[1]);
                    m.setY(e[2]);

                    //remove
                    removeList.add(e);
                }
            }
            for (double[] e : removeList) {
                m.getMoveQueue().remove(e);
            }

            //calculate distance between player and monster
            double[] mq = (m.getMoveQueue().size() > 0)
                    ? m.getMoveQueue().getLast() : new double[]{0, m.getX(), m.getY()};
            double mPosY = mq[2];
            double mPosX = mq[1];
            double ydiff = (mPosY + m.getHeight() / 2) - (player.getY() + player.getHeight() / 2);
            double xdiff = (mPosX + m.getWidth() / 2) - (player.getX() + player.getWidth() / 2);
            double d = round(Math.sqrt(Math.pow(xdiff, 2) + Math.pow(ydiff, 2)));

            double range = GameSettings.MONSTER_MOVE_RANGE;
            double reactTime = GameSettings.MONSTER_REACTION_TIME;
            if (m.getType() == MonsterType.FINALBOSS) {
                range = GameSettings.BOSS_MOVE_RANGE;
                reactTime = GameSettings.BOSS_REACTION_TIME;
            }


            if (d <= range && d >= GameSettings.MONSTER_MOVE_MIN) {
                //move monster towards player
                double angle = Math.atan2(ydiff, xdiff) - Math.PI;
                double newPosX = mPosX + round(Math.cos(angle) * m.getSpeed());
                double newPosY = mPosY + round(Math.sin(angle) * m.getSpeed());

                //check collisions with obstacles
                double[] newPos = checkPos(mPosX, mPosY, newPosX, newPosY,
                        m.getHeight(), m.getWidth());
                int count = 0;
                if (count >= 4) {
                    System.out.println("Error: Monster stuck.");
                    return false;
                }

                //add to queue
                double[] moveItem =
                        new double[]{reactTime, newPos[0], newPos[1]};
                m.getMoveQueue().add(moveItem);
            }
            ydiff = (m.getY() + m.getHeight() / 2) - (player.getY() + player.getHeight() / 2);
            xdiff = (m.getX() + m.getWidth() / 2) - (player.getX() + player.getWidth() / 2);
            d = round(Math.sqrt(Math.pow(xdiff, 2) + Math.pow(ydiff, 2)));

            //check for current attack
            if (d <= GameSettings.MONSTER_ATTACK_RANGE) {
                if (checkAttack(m)) {
                    //set attack cooldown
                    m.setAttackCooldown(m.getAttackSpeed() * 1000);
                    //attack player
                    double newHealth = player.getHealth() - m.getAttack();
                    player.setHealth((int) Math.max(0, newHealth));
                    GameScreen screen = getScreen();

                    //use run later to prevent any thread issues
                    Platform.runLater(() -> screen.updateHud());

                    //go to game over screen if player has died
                    if (player.getHealth() == 0.0) {
                        //use run later to prevent any thread issues
                        refresh();
                        gameOver();
                        return true;
                    }
                }
            }
            return false;
        }

        private void gameOver() {
            System.out.println("Game Over");
            GameScreen screen = getScreen();
            Platform.runLater(() -> {
                room = screen.getLayout().getStartingRoom();
                screen.gameOver();
            });
        }

        /**
         * Checks if a monster can attack or not.
         * @param m Monster to check
         * @return Returns if the monster is able to attack
         */
        private boolean checkAttack(Monster m) {
            if (m.getReaction() <= 0 && m.getAttackCooldown() <= 0) {
                m.setReaction(GameSettings.MONSTER_REACTION_TIME);
                return false;
            }
            double newCooldown = m.getAttackCooldown() - 1000.0 / GameSettings.FPS;
            double newTime = m.getReaction() - 1000.0 / GameSettings.FPS;
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
    }
}
