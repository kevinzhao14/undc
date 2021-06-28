package undc.objects;

import org.json.JSONException;
import org.json.JSONObject;
import undc.controllers.Console;
import undc.controllers.Controller;
import undc.gamestates.GameScreen;
import undc.handlers.Audio;

/**
 * Class that handles consumable items that buff the player called Potions.
 */
public class Potion extends Item {
    private PotionType type;
    private double modifier;

    private Potion() {

    }

    /**
     * Creates a copy of the potion.
     * @return Potion that is the copy
     */
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

    /**
     * Gets the type of potion as a String.
     * @return String representation of the potion
     */
    public String getTypeString() {
        switch (type) {
            case HEALTH:
                return "Health Potion";
            case ATTACK:
                return "Attack Potion";
            default:
                return "";
        }
    }

    public double getModifier() {
        return modifier;
    }

    /**
     * Gets modifier information on a potion.
     * @return String of modifier information
     */
    public String getModifierString() {
        switch (type) {
            case HEALTH:
                return "+" + (int) modifier + " Health";
            case ATTACK:
                return "+" + (int) (modifier * 100) + "% Damage";
            default:
                return "";
        }
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

        //update items consumed stat of player
        player.addItemConsumed();

        switch (this.type) {
            case ATTACK:
                Audio.playAudio("potion");

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

                Effect effect = new Effect(EffectType.ATTACKBOOST, 0.25, duration);
                player.getEffects().add(effect);

                //remove from inventory
                player.getInventory().remove(this);
                gameScreen.updateHud();
                break;
            case HEALTH:
                // increase health by potion modifier
                double health = gameScreen.getPlayer().getHealth();
                if (health != gameScreen.getPlayer().getMaxHealth()) {
                    Audio.playAudio("potion");
                }
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
                break;
        }
    }

    /**
     * Makes JSON data into a Potion.
     * @param o JSONObjec to pull information from.
     * @return Potion that is created from JSONObject
     */
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
