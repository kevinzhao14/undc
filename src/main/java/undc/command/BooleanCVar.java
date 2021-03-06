package undc.command;

/**
 * Class representing a CVar with boolean values.
 */
public class BooleanCVar extends CVar {
    private final boolean def;

    private boolean value;

    /**
     * Constructor for a BooleanCVar.
     * @param name Name of the CVar
     * @param def Default value of the CVar
     * @param rc Whether the CVar requires cheats to modify. Defaults to true
     * @param modifiable Whether the CVar is modifiable. Defaults to true
     */
    public BooleanCVar(String name, boolean def, boolean rc, boolean modifiable) {
        super(name, rc);
        this.def = def;
        this.value = def;
        this.modifiable = modifiable;
    }

    public BooleanCVar(String name, boolean def, boolean rc) {
        this(name, def, rc, true);
    }

    public boolean getVal() {
        return value;
    }

    @Override
    public boolean setVal(String val, boolean override) {
        if (!override && !checkSet()) {
            return false;
        }
        if (val == null) {
            Console.error("CVar value cannot be null");
            return false;
        }
        if (val.equalsIgnoreCase("true")) {
            value = true;
        } else if (val.equalsIgnoreCase("false")) {
            value = false;
        } else {
            Console.error("Invalid value");
            return false;
        }
        return true;
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
    public void reset() {
        value = def;
    }

    @Override
    public String toString() {
        return value + " (default: " + def + ")";
    }
}
