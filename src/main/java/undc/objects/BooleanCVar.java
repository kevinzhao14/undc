package undc.objects;

import undc.controllers.*;

public class BooleanCVar extends CVar {
    private boolean def;
    private boolean value;

    public BooleanCVar(String name, String nick, boolean def, boolean rc) {
        super(name, nick, rc);
        this.def = def;
        this.value = def;
    }

    public BooleanCVar(String name, String nick, boolean def) {
        this(name, nick, def, true);
    }

    public boolean getVal() {
        return value;
    }

    public boolean getDef() {
        return def;
    }

    @Override
    public void setVal(String val) {
        if (!checkSet()) return;
        if (val == null) {
            Console.error("CVar value cannot be null");
            return;
        }
        if (val.equalsIgnoreCase("true")) value = true;
        else if (val.equalsIgnoreCase("false")) value = false;
        else Console.error("Invalid value");
    }

    @Override
    public void reset() {
        value = def;
    }
}
