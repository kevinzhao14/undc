package dungeoncrawler.objects;
import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
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

        //update items consumed stat of player
        player.addItemConsumed();

        switch (this.type) {
        case ATTACK:
            // damage nearby monsters by potion modifier

            double duration = 15 * 1000;

            //check for existing effect
            for (Effect e : player.getEffects()) {
                if (e.getType() == EffectType.ATTACKBOOST) {
                    e.setDuration(e.getDuration() + duration);
                    //remove from inventory
                    player.getInventory().remove(this);
                    gameScreen.updateHud();
                    return;
                }
            }

            Effect effect = new Effect(EffectType.ATTACKBOOST, 0.25, -1, duration);
            player.getEffects().add(effect);

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
