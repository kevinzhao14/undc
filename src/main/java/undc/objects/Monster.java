package undc.objects;

import org.json.JSONException;
import org.json.JSONObject;
import undc.controllers.Console;
import undc.controllers.Controller;
import undc.controllers.DataManager;
import undc.gamestates.GameScreen;
import javafx.application.Platform;
import javafx.scene.image.Image;
import undc.handlers.Vars;

import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation of the Monster class.
 */
public class Monster extends Entity {
    private String id;
    private String name;
    private MonsterType type;
    private double speed;
    private double attackSpeed;

    // status variables, changed through the game
    private double reaction;
    private ArrayList<Move> moveQueue;
    private double opacity;

    /**
     * Creates a Monster object.
     */
    private Monster() {
        reaction = -1;
        moveQueue = new ArrayList<>();
        opacity = 1;
    }

    /**
     * Creates a copy of a Monster with modified health and attack stats.
     * @param modifier double to be applied to health and attack stats
     * @return Monster that is modified
     */
    public Monster copy(double modifier) {
        Monster m = new Monster();
        copy(m);
        m.maxHealth = (int) (maxHealth * modifier);
        m.attack = attack * modifier;
        m.id = this.id;
        m.name = this.name;
        m.type = this.type;
        m.speed = this.speed;
        m.attackSpeed = this.attackSpeed;
        return m;
    }

    /**
     * Updates health of monster after it recieves damage and gives player gold for killing it.
     * @param damageAmount double amount of damage taken.
     * @param giveGold boolean for whether or not to give gold after killing the monster.
     * @return
     */
    public boolean attackMonster(double damageAmount, boolean giveGold) {
        if (this.getHealth() <= 0) {
            return false;
        }
        //update damage dealt stat in player object
        ((GameScreen) Controller.getState()).getPlayer().addDamageDealt(
                Math.min(this.getHealth(), damageAmount));
        //change monster health
        this.setHealth(Math.max(0, this.getHealth() - damageAmount));

        //Give gold to player after slaying a monster
        if (this.getHealth() == 0.0) {
            GameScreen screen = (GameScreen) Controller.getState();

            if (giveGold) {
                double modifier;
                switch (Controller.getDataManager().getDifficulty()) {
                    case MEDIUM:
                        modifier = Vars.d("sv_modifier_medium");
                        break;
                    case HARD:
                        modifier = Vars.d("sv_modifier_hard");
                        break;
                    default:
                        modifier = 1.0;
                        break;
                }
                screen.getPlayer().setGold(screen.getPlayer().getGold()
                        + (int) (Vars.d("sv_monster_gold") / modifier));

                //update number of monsters player killed
                screen.getPlayer().addMonsterKilled();
            }

            //make monster disappear
            this.setOpacity(1 - (1000.0 / Vars.i("sv_tickrate") / Vars.i("gc_monster_fade_dur")));

            //only drop items if it's not a challenge room
            if (!(screen.getRoom() instanceof ChallengeRoom)) {
                //generate drop items and add to Room ArrayList
                DroppedItem[] itemDrops = dropItems();

                //Add dropped items to Room ArrayList
                for (DroppedItem item : itemDrops) {
                    screen.getRoom().getDroppedItems().add(item);
                }
            } else {    //drop the rewards if challenge room is complete
                boolean allDead = true;
                for (Monster m : screen.getRoom().getMonsters()) {
                    if (m != null && m.getHealth() > 0) {
                        allDead = false;
                        break;
                    }
                }
                if (allDead) {
                    ((ChallengeRoom) screen.getRoom()).setCompleted(true);
                    Platform.runLater(screen::updateRoom);
                    for (InventoryItem[] itemRow
                            : ((ChallengeRoom) screen.getRoom()).getRewards().getItems()) {
                        if (itemRow != null) {
                            for (InventoryItem item : itemRow) {
                                if (item != null) {
                                    if (item.getItem().equals(DataManager.ITEMS.get("rocket_launcher"))) {
                                        DataManager.setUnlockedAmmo(true);
                                    }
                                    for (int i = 0; i < item.getQuantity(); i++) {
                                        DroppedItem newItem = new DroppedItem(item.getItem());
                                        int width = (int) item.getItem().getSprite().getWidth();
                                        int height = (int) item.getItem().getSprite().getHeight();
                                        newItem.setWidth(width);
                                        newItem.setHeight(height);

                                        double maxRadius = Vars.i("sv_player_pickup_range");
                                        Random generator = new Random();
                                        Player player = screen.getPlayer();
                                        double x = 0;
                                        double y = 0;

                                        boolean isValidLocation = false;

                                        while (!isValidLocation) {
                                            //generate randDist between item and monster
                                            double randDist = maxRadius * generator.nextDouble();
                                            //generate randAngle
                                            double randAngle = 2 * Math.PI * generator.nextDouble();

                                            //calculate x and y
                                            x = player.getX() + player.getWidth() / 2 + (randDist
                                                    * Math.cos(randAngle)) - width / 2.0;
                                            y = player.getY() + player.getHeight()  + (randDist
                                                    * Math.sin(randAngle)) - height / 2.0;

                                            isValidLocation = x >= 0.0 && x < screen.getRoom()
                                                    .getWidth() && y > 0.0 && y < screen.getRoom()
                                                    .getHeight();
                                        }

                                        //Set x and y
                                        newItem.setX(x);
                                        newItem.setY(y);
                                        screen.getRoom().getDroppedItems().add(newItem);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //use run later to prevent any thread issues
            Platform.runLater(screen::updateHud);
            return true;
        }
        return false;
    }

    public void revive(int posX, int posY) {
        setX(posX);
        setY(posY);
        setHealth(getMaxHealth());
        setOpacity(1.0);
    }

    public DroppedItem[] dropItems() {
        if (DataManager.ITEMS.size() == 0 && type != MonsterType.FINALBOSS) {
            return new DroppedItem[0];
        }
        if (type == MonsterType.FINALBOSS) {
            Item key = DataManager.getExitKey().copy();
            Image sprite = key.getSprite();
            double x = getX() + getWidth() / 2.0 - sprite.getWidth() / 2;
            double y = getY() + getHeight() / 2.0 - sprite.getHeight() / 2;
            return new DroppedItem[]{
                new DroppedItem(key, x, y, (int) sprite.getWidth(), (int) sprite.getHeight())
            };
        }
        Random generator = new Random();
        //Calculate number of items to drop
        int min = Vars.i("sv_itemdrop_min");
        int max = Vars.i("sv_itemdrop_max");
        int numItems = generator.nextInt(max - min + 1) + min;

        DroppedItem[] droppedItems = new DroppedItem[numItems];

        //Maximum distance between monster death location and item spawn location
        double maxRadius = 1.5 * getWidth();
        double x = getX();   //x-pos of item spawn
        double y = getY();   //y-pos of item spawn
        boolean isValidLocation = false; //flag for whether item spawn location is valid

        double randDist;   //random distance between item and monster
        double randAngle;  //random angle at which item is created

        double roomWidth = ((GameScreen) Controller.getState()).getRoom().getWidth();
        double roomHeight = ((GameScreen) Controller.getState()).getRoom().getHeight();

        for (int i = 0; i < numItems; i++) {
            isValidLocation = false; //reset flag


            //keep generating a new index until a droppable item is found
            Item item;
            ArrayList<Item> items = new ArrayList<>(DataManager.ITEMS.values());
            do {
                item = items.get(generator.nextInt(items.size()));
            } while (!item.isDroppable() || (item instanceof Ammunition && !DataManager.isUnlockedAmmo()));

            droppedItems[i] = new DroppedItem(item.copy());

            //Set width and height
            droppedItems[i].setWidth((int) droppedItems[i].getItem().getSprite().getWidth());
            droppedItems[i].setHeight((int) droppedItems[i].getItem().getSprite().getHeight());

            //Keep generating x and y position of item until an acceptable one is found
            while (!isValidLocation) {
                //generate randDist between item and monster
                randDist = maxRadius * generator.nextDouble();
                //generate randAngle
                randAngle = 2 * Math.PI * generator.nextDouble();

                //calculate x and y
                x = getX() + (randDist * Math.cos(randAngle));
                y = getY() + (randDist * Math.sin(randAngle));

                isValidLocation = (x > 0.0 && x < roomWidth && y > 0.0 && y < roomHeight);
            }

            //Set x and y
            droppedItems[i].setX(x);
            droppedItems[i].setY(y);
        }
        return droppedItems;
    }

    public double getReaction() {
        return reaction;
    }

    public void setReaction(double reaction) {
        this.reaction = reaction;
    }

    public double getSpeed() {
        return speed;
    }

    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Type: " + type + " | Speed: " + speed + " | Attack: "
                + attack + " " + attackSpeed + " " + super.toString();
    }

    public ArrayList<Move> getMoveQueue() {
        return moveQueue;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public MonsterType getType() {
        return this.type;
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public static Monster parse(JSONObject o) {
        Monster monster = new Monster();
        try {
            monster.id = o.getString("id");
        } catch (JSONException e) {
            Console.error("Invalid value for monster id.");
            return null;
        }
        try {
            monster.name = o.getString("name");
        } catch (JSONException e) {
            Console.error("Invalid value for monster name.");
            return null;
        }
        try {
            monster.maxHealth = o.getInt("health");
            monster.health = monster.maxHealth;
        } catch (JSONException e) {
            Console.error("Invalid value for monster health.");
            return null;
        }
        try {
            monster.attack = o.getDouble("attack");
        } catch (JSONException e) {
            Console.error("Invalid value for monster attack.");
            return null;
        }
        try {
            monster.attackSpeed = o.getDouble("attackSpeed");
        } catch (JSONException e) {
            Console.error("Invalid value for monster attack speed.");
            return null;
        }
        try {
            monster.speed = o.getDouble("speed");
        } catch (JSONException e) {
            Console.error("Invalid value for monster speed.");
            return null;
        }
        try {
            monster.width = o.getInt("width");
        } catch (JSONException e) {
            Console.error("Invalid value for monster width.");
            return null;
        }
        try {
            monster.height = o.getInt("height");
        } catch (JSONException e) {
            Console.error("Invalid value for monster height.");
            return null;
        }
        try {
            monster.type = MonsterType.valueOf(o.getString("type").toUpperCase());
        } catch (JSONException e) {
            Console.error("Invalid value for monster type.");
            return null;
        } catch (IllegalArgumentException a) {
            Console.error("Invalid type for monster type.");
            return null;
        }
        try {
            monster.sprite = new Image(o.getString("sprite"));
        } catch (IllegalArgumentException e) {
            Console.error("Invalid value for monster sprite.");
            return null;
        }
        return monster;
    }

    public String getId() {
        return id;
    }
}
