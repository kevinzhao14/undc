package undc.command;

/**
 * Represents a CVar with integer values.
 */
public class IntCVar extends CVar {
    //inclusive min and max
    private int min;
    private int max;
    private int def;
    private int value;

    /**
     * Constructor for an IntCVar.
     * @param name Name of the CVar
     * @param nick Nickname of the CVar
     * @param min Minimum value of the CVar
     * @param max Maximum value of the CVar
     * @param def Default value of the CVar
     * @param rc Whether the CVar requires cheats. Defaults to true
     */
    public IntCVar(String name, String nick, int min, int max, int def, boolean rc) {
        super(name, nick, rc);
        if (min > max) {
            Console.error("Minimum cannot be greater than the maximum");
            return;
        }
        if (def < min || def > max) {
            Console.error("Default value is out of range");
            return;
        }
        this.min = min;
        this.max = max;
        this.def = def;
        this.value = def;
    }

    public IntCVar(String name, String nick, int min, int max, int def) {
        this(name, nick, min, max, def, true);
    }

    public IntCVar(String name, String nick, int min, int max, int def, boolean rc, boolean modifiable) {
        this(name, nick, min, max, def, rc);
        this.modifiable = modifiable;
    }

    public int getVal() {
        return value;
    }

    @Override
    public String value() {
        return getVal() + "";
    }

    @Override
    public String defValue() {
        return def + "";
    }

    @Override
    public boolean setVal(String val, boolean override) {
        if (!override && !checkSet()) {
            return false;
        }
        try {
            int temp = Integer.parseInt(val);
            if (temp < min || temp > max) {
                Console.error("Value out of range");
                return false;
            }
            value = temp;
            return true;
        } catch (NumberFormatException e) {
            Console.error("Invalid value format");
            return false;
        }
    }

    @Override
    public void reset() {
        value = def;
    }

    @Override
    public String toString() {
        return value + " (default: " + def + ", min: " + min + ", max: " + max + ")";
    }
}
