package undc.command;

/**
 * Abstract class for all CVars.
 */
public abstract class CVar {
    private String name;

    protected boolean requireCheats;
    protected boolean modifiable;

    /**
     * Standard constructor for a CVar.
     * @param name Name of the CVar
     * @param requireCheats Whether the CVar requires cheats to modify
     */
    public CVar(String name, boolean requireCheats) {
        if (name == null) {
            Console.error("CVar name cannot be null");
            return;
        }
        this.name = name;
        this.requireCheats = requireCheats;
        modifiable = true;
    }

    public boolean setVal(String val) {
        return setVal(val, false);
    }

    public abstract boolean setVal(String val, boolean override);

    public abstract void reset();

    public abstract String toString();

    public abstract String value();

    public abstract String defValue();

    public String getName() {
        return name;
    }

    public boolean isModifiable() {
        return modifiable;
    }

    public boolean requiresCheats() {
        return requireCheats;
    }

    /**
     * Common conditions to check for setting any CVar.
     * @return Returns true if the CVar passes its checks, false otherwise
     */
    protected boolean checkSet() {
        if (requireCheats && !Vars.CHEATS) {
            Console.error("Cheats are disabled.");
            return false;
        }
        return true;
    }
}
