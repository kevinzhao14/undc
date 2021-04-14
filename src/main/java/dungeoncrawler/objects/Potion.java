package dungeoncrawler.objects;
import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.handlers.GameSettings;
import javafx.scene.image.Image;

/**
 * Implementation of the Potion data class
 *
 * @author Manas Harbola
 */
public class Potion extends Item {
    private PotionType type;
    private double modifier;

    public Potion(String name, String path, int stackSize,
                  boolean isDroppable, PotionType potionType, double potionModifier) {
        super(new Image(path), name, stackSize, isDroppable);
        type = potionType;
        modifier = potionModifier;
    }
    public Potion(String path, String itemName, PotionType potionType) {
        super(new Image(path), itemName);
        type = potionType;
    }

    public Potion copy() {
        return new Potion(getName(), getSprite().getUrl(), getMaxStackSize(),
                isDroppable(), type, modifier);
    }

    public PotionType getType() {
        return type;
    }
    public double getModifier() {
        return modifier;
    }

    /**
     * Use the potion. When it's a health potion, heal the player by the potion modifier,
     * unless they are already at max health. If it's a damage potion, damage all monsters
     * in twice the normal attack radius by potion modifier.
     */
    public void use() {
        if (!(Controller.getState() instanceof GameScreen)) {
            return;
        }
        GameScreen gameScreen = (GameScreen) Controller.getState();
        Player player = gameScreen.getPlayer();
        Room room = gameScreen.getRoom();

        switch (this.type) {
        case ATTACK:
            // damage nearby monsters by potion modifier
            for (Monster m : room.getMonsters()) {
                if (m == null) {
                    continue;
                }
                double xdist = player.getPosX() - m.getPosX();
                double ydist = player.getPosY() - m.getPosY();
                double sqdist = xdist * xdist + ydist * ydist;
                double sqattackrange = GameSettings.PLAYER_ATTACK_RANGE
                        * GameSettings.PLAYER_ATTACK_RANGE * 4;
                if (sqdist <= sqattackrange) {
                    m.attackMonster(this.getModifier(), true);
                }
            }

            //remove from inventory
            player.getInventory().remove(this);
            gameScreen.updateHud();
            break;
        case HEALTH:
            // increase health by potion modifier
            double health = gameScreen.getPlayer().getHealth();
            double newHealth = health + this.getModifier();
            double maxHealth = gameScreen.getPlayer().getMaxHealth();
            double cappedHealth = Math.min(newHealth, maxHealth);

            if (cappedHealth != health) {
                //remove from inventory
                gameScreen.getPlayer().setHealth(cappedHealth);
                player.getInventory().remove(this);
                gameScreen.updateHud();
            }
            break;
        default:
            System.err.println("Potion type not yet implemented.");
        }
    }
}
