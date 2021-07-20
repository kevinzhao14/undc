package undc.game;

import org.json.JSONObject;
import undc.command.Console;
import undc.general.Savable;

/**
 * Class that handles buffs the player can receive in game.
 */
public class Effect implements Savable {
    private final EffectType type;
    //amount of effect per occurence
    private final double amount;
    //seconds per occurence
    private double duration;

    /**
     * Constructor for an effect.
     * @param type EffectType
     * @param amount double amount of the effect
     * @param duration double time that the effect lasts
     */
    public Effect(EffectType type, double amount, double duration) {
        this.type = type;
        this.amount = amount;
        this.duration = duration;
    }

    public EffectType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("type", type.toString());
        o.put("amount", amount);
        o.put("duration", duration);
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        return true;
    }

    /**
     * Loads save data into a Effect object.
     * @param o The data to load
     * @return The corresponding Effect object
     */
    public static Effect parseSaveObject(JSONObject o) {
        try {
            EffectType type = EffectType.valueOf(o.getString("type"));
            double amount = o.getDouble("amount");
            double duration = o.getDouble("duration");
            return new Effect(type, amount, duration);
        } catch (Exception e) {
            Console.error("Failed to create Effect.");
            return null;
        }
    }
}
