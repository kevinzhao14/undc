package undc.objects;

import undc.controllers.*;

public class DoubleCVar extends CVar {
    //inclusive min and max
    private double min;
    private double max;
    private double def;
    private double value;

    public DoubleCVar(String name, String nick, double min, double max, double def, boolean rc) {
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

    public DoubleCVar(String name, String nick, double min, double max, double def) {
        this(name, nick, min, max, def, true);
    }

    public double getVal() {
        return value;
    }

    public double getDef() {
        return def;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    @Override
    public void setVal(String val) {
        if (!checkSet()) return;
        try {
            double temp = Double.parseDouble(val);
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
