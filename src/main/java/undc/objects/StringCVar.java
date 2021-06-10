package undc.objects;

import undc.controllers.Console;

/**
 * Class representing a CVar with String values.
 */
public class StringCVar extends CVar {
    private String def;
    private String value;

    /**
     * Constructor for a StringCVar.
     * @param name Name of the CVar
     * @param nick Nickname of the CVar
     * @param def Default value of the CVar
     * @param rc Whether the CVar requires cheats to modify. Defaults to true
     */
    public StringCVar(String name, String nick, String def, boolean rc) {
        super(name, nick, rc);
        if (def == null) {
            Console.error("CVar cannot be null");
            return;
        }
        this.def = def;
        this.value = def;
    }

    public String getVal() {
        return value;
    }

    @Override
    public boolean setVal(String val, boolean override) {
        if (!override && !checkSet()) {
            return false;
        }
        if (val == null) {
            Console.error("CVar value cannot be null.");
            return false;
        }
        value = val;
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
