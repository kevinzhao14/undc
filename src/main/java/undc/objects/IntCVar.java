package undc.objects;

import undc.controllers.*;

public class IntCVar extends CVar {
    //inclusive min and max
    private int min;
    private int max;
    private int def;
    private int value;

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

    public int getVal() {
        return value;
    }

    @Override
    public void setVal(String val) {
        if (!checkSet()) return;
        try {
            int temp = Integer.parseInt(val);
            if (temp < min || temp > max) {
                Console.error("Value out of range");
                return;
            }
            value = temp;
        } catch (NumberFormatException e) {
            Console.error("Invalid value format");
        }
    }

    @Override
    public void reset() {
        value = def;
    }
}
