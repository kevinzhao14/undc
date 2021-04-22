package dungeoncrawler.objects;

import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.handlers.GameSettings;
import javafx.application.Platform;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation of the Monster class
 *
 * @author Manas Harbola
 * @version 1.0
 */
public class Monster extends Entity {
    private MonsterType type;
    private double speed;
    private double attackSpeed;
    private double reaction;
    private LinkedList<double[]> moveQueue;
    private double opacity;

    public Monster(int maxHealth, double attack, double speed, double attackSpeed, MonsterType type,
                   double height, double width) {

        //healthBar should be instantiated and assigned in this Monster constructor
        super(maxHealth, attack, height, width, null);
        this.type = type;
        this.speed = speed;
        this.attackSpeed = attackSpeed;
        this.reaction = -1;
        moveQueue = new LinkedList<>();
        opacity = 1;
        switch (type) {
        case FAST:
            setNode("monsters/monster-fast.png");
            break;
        case NORMAL:
            setNode("monsters/monster-normal.png");
            break;
        case TANK:
            setNode("monsters/monster-tank.png");
            break;
        default:
            setNode("monsters/monster-normal.png");
            break;
        }
    }

    public Monster(Monster m, double modifier) {
        this((int) (m.getMaxHealth() * modifier), m.getAttack() * modifier, m.speed,
                m.attackSpeed, m.type, m.getHeight(), m.getWidth());
    }

    public boolean attackMonster(double damageAmount, boolean givePlayerGoldIfSlain) {
        if (this.getHealth() <= 0) {
            return false;
        }
        //update damage dealt stat in player object
        ((GameScreen) Controller.getState()).getPlayer().addDamageDealt(Math.min(this.getHealth(), damageAmount));
        //change monster health
        this.setHealth(Math.max(0, this.getHealth() - damageAmount));

        //Give gold to player after slaying a monster
        if (this.getHealth() == 0.0) {
            GameScreen screen = (GameScreen) Controller.getState();

            if (givePlayerGoldIfSlain) {
                double modifier;
                switch (Controller.getDataManager().getDifficulty()) {
                case MEDIUM:
                    modifier = GameSettings.MODIFIER_MEDIUM;
                    break;
                case HARD:
                    modifier = GameSettings.MODIFIER_HARD;
                    break;
                default:
                    modifier = 1.0;
                    break;
                }
                screen.getPlayer().setGold(screen.getPlayer().getGold()
                        + (int) (GameSettings.MONSTER_KILL_GOLD / modifier));

                //update number of monsters player killed
                screen.getPlayer().addMonsterKilled();
            }

            //make monster disappear
            this.setOpacity(1 - (1000.0 / (GameSettings.MONSTER_FADE_TIME
                    * GameSettings.FPS)));

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
                    for (InventoryItem[] itemRow : ((ChallengeRoom) screen.getRoom()).getRewards().getItems()) {
                        if (itemRow != null) {
                            for (InventoryItem item : itemRow) {
                                if (item != null) {
                                    for (int i = 0; i < item.getQuantity(); i++) {
                                        DroppedItem newItem = new DroppedItem(item.getItem());
                                        newItem.setWidth(item.getItem().getSprite().getWidth());
                                        newItem.setHeight(item.getItem().getSprite().getHeight());

                                        double maxRadius = screen.getPlayer().getSpriteWidth() * 3.0;
                                        Random generator = new Random();
                                        double randDist;
                                        double randAngle;
                                        double x = screen.getPlayer().getX();
                                        double y = screen.getPlayer().getY();

                                        boolean isValidLocation = false;

                                        while (!isValidLocation) {
                                            //generate randDist between item and monster
                                            randDist = maxRadius * generator.nextDouble();
                                            //generate randAngle
                                            randAngle = 2 * Math.PI * generator.nextDouble();

                                            //calculate x and y
                                            x = screen.getPlayer().getX() + (randDist * Math.cos(randAngle));
                                            y = screen.getPlayer().getY() + (randDist * Math.sin(randAngle));

                                            isValidLocation = (x > 0.0 && x < screen.getRoom().getWidth() && y > 0.0 && y < screen.getRoom().getHeight());
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
            Platform.runLater(() -> {
                screen.updateHud();
            });
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
        if (Controller.getDataManager().ITEMS.length == 0 && type != MonsterType.FINALBOSS) {
            return new DroppedItem[0];
        }
        Random generator = new Random();
        //Calculate number of items to drop
        int numItems = GameSettings.MIN_ITEM_DROP
                + generator.nextInt(GameSettings.MAX_ITEM_DROP - GameSettings.MIN_ITEM_DROP + 1);

        //if monster is the final boss, add another space in array for key
        if (getType() == MonsterType.FINALBOSS) {
            if (Controller.getDataManager().ITEMS.length > 0) {
                numItems++;
            } else {
                numItems = 1;
            }
        }

        DroppedItem[] droppedItems = new DroppedItem[numItems];

        //Maximum distance between monster death location and item spawn location
        double maxRadius = 1.5 * getWidth();
        double x = getX();   //x-pos of item spawn
        double y = getY();   //y-pos of item spawn
        boolean isValidLocation = false; //flag for whether item spawn location is valid

        int randIdx;    //index of random item to drop
        double randDist;   //random distance between item and monster
        double randAngle;  //random angle at which item is created

        double roomWidth = ((GameScreen) Controller.getState()).getRoom().getWidth();
        double roomHeight = ((GameScreen) Controller.getState()).getRoom().getHeight();

        for (int i = 0; i < numItems; i++) {
            isValidLocation = false; //reset flag

            randIdx = generator.nextInt(Controller.getDataManager().ITEMS.length);

            //keep generating a new index until a droppable item is found
            while (!Controller.getDataManager().ITEMS[randIdx].isDroppable()) {
                randIdx = generator.nextInt(Controller.getDataManager().ITEMS.length);
            }

            if (type != MonsterType.FINALBOSS || i < numItems - 1) {
                droppedItems[i] = new DroppedItem(
                        Controller.getDataManager().ITEMS[randIdx].copy());
            } else {
                droppedItems[i] = new DroppedItem(Controller.getDataManager().EXITKEY.copy());
            }

            //Set width and height
            droppedItems[i].setWidth(droppedItems[i].getItem().getSprite().getWidth());
            droppedItems[i].setHeight(droppedItems[i].getItem().getSprite().getHeight());

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
        return "Type: " + type + " | Speed: " + speed + " | Attack: " + getAttack() + " "
                + attackSpeed + " " + super.toString();
    }

    public LinkedList<double[]> getMoveQueue() {
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
}
