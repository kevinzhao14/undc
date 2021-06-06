package undc.objects;
import org.json.JSONException;
import org.json.JSONObject;
import undc.controllers.*;
import undc.gamestates.GameScreen;
import javafx.scene.image.Image;

/**
 * Implementation of the Potion data class
 *
 * @author Manas Harbola
 */
public class Potion extends Item {
    private PotionType type;
    private double modifier;

    private Potion() {

    }

    public Potion copy() {
        Potion potion = new Potion();
        copy(potion);
        potion.type = this.type;
        potion.modifier = this.modifier;
        return potion;
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
            Console.error("Potion type does not exist");
        }
    }

    static Potion parseJSON(JSONObject o) {
        Potion potion = new Potion();
        try {
            potion.type = PotionType.valueOf(o.getString("potionType").toUpperCase());
        } catch (JSONException e) {
            Console.error("Invalid value for potion type.");
            return null;
        } catch (IllegalArgumentException i) {
            Console.error("Invalid option for potion type.");
            return null;
        }
        try {
            potion.modifier = o.getDouble("amount");
        } catch (JSONException e) {
            Console.error("Invalid value for potion amount.");
            return null;
        }
        return potion;
    }
}
