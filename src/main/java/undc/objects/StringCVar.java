package undc.objects;

import undc.controllers.*;

public class StringCVar extends CVar {
    private String def;
    private String value;

    public StringCVar(String name, String nick, String def, boolean rc) {
        super(name, nick, rc);
        if (def == null) {
            Console.error("CVar cannot be null");
            return;
        }
        this.def = def;
        this.value = def;
    }

    public StringCVar(String name, String nick, String def) {
        this(name, nick, def, true);
    }

    public String getVal() {
        return value;
    }

    @Override
    public void setVal(String val) {
        if (!checkSet()) return;
        if (val == null) {
            Console.error("CVar value cannot be null.");
            return;
        }
        value = val;
    }

    @Override
    public void reset() {
        value = def;
    }
}