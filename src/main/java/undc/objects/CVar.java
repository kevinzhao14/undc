package undc.objects;

import undc.controllers.Console;
import undc.handlers.Vars;

/**
 * Abstract class for all CVars.
 */
public abstract class CVar {
    private String name;
    private String nick;
    protected boolean requireCheats;
    protected boolean modifiable;

    /**
     * Standard constructor for a CVar.
     * @param name Name of the CVar
     * @param nick Nickname of the CVar
     * @param requireCheats Whether the CVar requires cheats to modify
     */
    public CVar(String name, String nick, boolean requireCheats) {
        if (name == null || nick == null) {
            Console.error("CVar name cannot be null");
            return;
        }
        this.name = name;
        this.nick = nick;
        this.requireCheats = requireCheats;
        modifiable = true;
    }

    public abstract boolean setVal(String val);

    public abstract void reset();

    public abstract String toString();

    public String getName() {
        return name;
    }

    public String getNick() {
        return nick;
    }

    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }

    /**
     * Common conditions to check for setting any CVar.
     * @return Returns true if the CVar passes its checks, false otherwise
     */
    protected boolean checkSet() {
        if (!modifiable) {
            Console.error("You cannot modify this variable.");
            return false;
        }
        if (requireCheats && !Vars.CHEATS) {
            Console.error("Cheats are disabled.");
            return false;
        }
        return true;
    }
}
