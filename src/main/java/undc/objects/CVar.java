package undc.objects;

import undc.controllers.*;
import undc.handlers.*;

public abstract class CVar {
    private String name;
    private String nick;
    protected boolean requireCheats;
    protected boolean modifiable;

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

    public String getName() {
        return name;
    }

    public String getNick() {
        return nick;
    }

    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }

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
