package dungeoncrawler.objects;

import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.gamestates.GameState;
import dungeoncrawler.handlers.GameSettings;
import javafx.application.Platform;
import javafx.scene.layout.HBox;

import java.util.LinkedList;

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

    //need to implement
    public void attackMonster(double dmgAmt) {
        this.setHealth(Math.max(0, this.getHealth() - dmgAmt));
        if (getHealth() <= 0) {
            System.out.println("Monster slain.");
        }
    }

    public void attackMonster(Player player, double damageAmount) {
        if (this.getHealth() > 0) {
            double dist = Math.sqrt(Math.pow(player.getPosX() - this.getPosX(), 2)
                    + Math.pow(player.getPosY() - this.getPosY(), 2));
            if (dist <= GameSettings.PLAYER_ATTACK_RANGE) {
                this.setHealth(Math.max(0, this.getHealth() - damageAmount));
                if (getHealth() <= 0) {
                    System.out.println("Monster slain.");
                }
                //Give gold to player after slaying a monster
                if (this.getHealth() == 0.0) {
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
                    player.setGold(player.getGold()
                            + (int) (GameSettings.MONSTER_KILL_GOLD / modifier));
                    GameState screen = Controller.getState();
                    this.setOpacity(1 - (1000.0 / (GameSettings.MONSTER_FADE_TIME
                            * GameSettings.FPS)));
                    //use run later to prevent any thread issues
                    if (screen instanceof GameScreen) {
                        Platform.runLater(() -> {
                            ((GameScreen) screen).updateHud();
                        });
                    } else {
                        Platform.runLater(() -> {
                            ((GameScreen) screen).getGame().pause();
                            throw new IllegalStateException("Illegal Game State.");
                        });
                    }
                }
            }
        }



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
