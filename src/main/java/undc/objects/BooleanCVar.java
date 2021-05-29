package undc.objects;

import undc.controllers.Console;

/**
 * Class representing a CVar with boolean values.
 */
public class BooleanCVar extends CVar {
    private final boolean def;
    private boolean value;

    /**
     * Constructor for a BooleanCVar.
     * @param name Name of the CVar
     * @param nick Nickname of the CVar
     * @param def Default value of the CVar
     * @param rc Whether the CVar requires cheats to modify. Defaults to true
     */
    public BooleanCVar(String name, String nick, boolean def, boolean rc) {
        super(name, nick, rc);
        this.def = def;
        this.value = def;
    }

    public boolean getVal() {
        return value;
    }

    @Override
    public boolean setVal(String val) {
        if (!checkSet()) {
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
    public void reset() {
        value = def;
    }

    @Override
    public String toString() {
        return value + " (default: " + def + ")";
    }
}
