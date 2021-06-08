package undc.controllers;

import undc.gamestates.GameScreen;
import undc.handlers.Controls;
import undc.handlers.Vars;
import undc.objects.CVar;
import undc.objects.Item;
import undc.objects.Monster;
import undc.objects.Obstacle;
import undc.objects.Projectile;

import java.util.ArrayList;
import java.util.HashMap;

class Command {
    String name;
    String desc;
    String format;
    private CommandObj obj;

    public Command(String name, String format, String desc, CommandObj obj) {
        this.name = name;
        this.format = format;
        this.desc = desc;
        this.obj = obj;
    }

    public void run(String[] args) {
        obj.run(args);
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getFormat() {
        return format;
    }

    public interface CommandObj {
        void run(String[] args);
    }

    public static ArrayList<Command> load() {
        ArrayList<Command> commands = new ArrayList<>();
        commands.add(new Command("test", "", "TEST", Command::test));
        commands.add(new Command("bind", "<key> <command>", "Binds a key to a command.", Command::bind));
        commands.add(new Command("unbind", "<key>", "Unbinds a key.", Command::unbind));
        commands.add(new Command("find", "<query>", "Finds commands with a specified query.", Command::find));
        commands.add(new Command("reset", "<cvar>", "Resets a cvar.", Command::reset));
        commands.add(new Command("clear", "", "Clears the console.", Command::clear));
        commands.add(new Command("quit", "", "Quits the game.", Command::quit));
        commands.add(new Command("give", "<id> [quantity]", "Gives the player an item(s)", Command::give));
        commands.add(new Command("spawn", "<id> [x] [y]", "Spawns an entity. Defaults to the player's coordinates.", Command::spawn));

        //add cvars
        for (CVar v : Vars.all()) {
            if (!v.isModifiable()) {
                continue;
            }
            String desc = "Returns or sets the value of " + v.getName() + "." + (v.requiresCheats() ? " Requires cheats." : "");
            Command c = new Command(v.getName(), "[value]", desc, e -> cvar(strArrAdd(e, 0, v.getName())));
            commands.add(c);
        }

        return commands;
    }

    private static String[] strArrAdd(String[] arr, int index, String elem) {
        String[] newArr = new String[arr.length + 1];
        if (arr.length == 0) {
            newArr[0] = elem;
        }
        for (int i = 0; i < arr.length; i++) {
            if (i == index) {
                newArr[i] = elem;
            }
            if (i >= index) {
                newArr[i + 1] = arr[i];
            } else {
                newArr[i] = arr[i];
            }
        }
        return newArr;
    }

    private static String clean(String s) {
        return s.toLowerCase().replaceAll("[\"']", "");
    }

    /*
     *
     *      COMMAND METHODS
     *
     *      Must take as parameters in order to work with CommandObj:
     *      - String[] args: arguments passed after the command, eg {key, command} in "bind <key> <command>"
     *
     */

    private static void test(String[] args) {

    }

    private static void echo(String[] args) {
        if (args.length != 1) {
            Console.error("Invalid arguments for echo.");
        }
        Console.print(args[0]);
    }

    private static void bind(String[] args) {
        if (args.length == 2) { // New bind. Format: bind <key> <command>
            String key = clean(args[0]);
            String control = clean(args[1]);
            Controls.getInstance().setKey(key, control);
            Console.print("Key bound.");
        } else if (args.length == 1) { // Retrieval. Format: bind <key>
            String key = clean(args[0]);
            String control = Controls.getInstance().getControl(key);
            if (control.equals("")) {
                Console.error("Key is not bound.");
            } else {
                Console.print(control);
            }
        } else {
            Console.error("Invalid arguments for bind.");
        }
    }

    private static void unbind(String[] args) {
        if (args.length != 1) {
            Console.error("Invalid arguments for unbind.");
            return;
        }
        String key = clean(args[0]);
        Controls.getInstance().removeKey(key);
    }

    private static void cvar(String[] args) {
        if (!(args.length == 1 || args.length == 2)) {
            Console.error("Invalid arguments for the cvar.");
            return;
        }
        String var = clean(args[0]);
        //get the cvar
        if (args.length == 1) {
            CVar cvar = Vars.find(var);
            if (cvar == null) {
                Console.error("CVar could not be found.");
            } else {
                Console.print(cvar.toString());
            }
        } else {
            String val = clean(args[1]);
            if (Vars.set(var, val)) {
                Console.print(var + " has been set to " + val);
            } else {
                Console.error("Failed to set " + var + ".");
            }
        }
    }

    private static void reset(String[] args) {
        if (args.length != 1) {
            Console.error("Invalid arguments for reset.");
            return;
        }
        String var = clean(args[0]);
        CVar cvar = Vars.find(var);
        if (cvar == null) {
            Console.error("Could not find " + var);
            return;
        }
        cvar.reset();
        Console.print(var + " was reset.");
    }

    private static void find(String[] args) {
        if (args.length != 1) {
            Console.run("find \"\"", false);
            return;
        }
        String search = clean(args[0]);
        StringBuilder res = new StringBuilder();
        for (Command c : Console.commands) {
            if (c.getName().contains(search)) {
                res.append(c.getName());
                res.append(c.getFormat().length() > 0 ? " " + c.getFormat() : "");
                res.append(" - ");
                res.append(c.getDesc());
                res.append("\n");
            }
        }
        if (res.toString().equals("")) {
            res.append("No results found.");
        }
        Console.print(res.toString());
    }

    private static void clear(String[] args) {
        Console.clear();
    }

    private static void quit(String[] args) {
        Controller.quit();
    }

    private static void give(String[] args) {
        if (!Vars.CHEATS) {
            Console.error("Cheats are disabled.");
            return;
        }
        if (!(args.length == 1 || args.length == 2)) {
            Console.error("Invalid arguments for give.");
            return;
        }
        // Give 1 quantity
        if (args.length == 1) {
            Console.run("give " + args[0] + " 1", false);
            return;
        } else {
            try {
                int id = Integer.parseInt(args[0]);
                int quantity = Integer.parseInt(args[1]);
                if (!(Controller.getState() instanceof GameScreen)) {
                    Console.error("Cannot give because there is no game.");
                    return;
                }
                Item item = DataManager.ITEMS.get(id);
                if (item == null) {
                    Console.error("Invalid item id.");
                    return;
                }
                GameController.getInstance().give(item, quantity);
            } catch (NumberFormatException e) {
                Console.error("Invalid argument values for give.");
                return;
            }
        }
    }

    private static void spawn(String[] args) {
        if (!Vars.CHEATS) {
            Console.error("Cheats are disabled.");
            return;
        }
        if (!(args.length == 1 || args.length == 3)) {
            Console.error("Invalid arguments for spawn.");
            return;
        }
        //spawn at 0,0
        if (args.length == 1) {
            Console.run("spawn " + args[0] + " 0 0", false);
            return;
        } else {
            try {
                int id = Integer.parseInt(args[0]);
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                if (!(Controller.getState() instanceof GameScreen)) {
                    Console.error("Cannot spawn because there is no game.");
                    return;
                }
                Monster m = DataManager.MONSTERS.get(id);
                if (m == null) {
                    Console.error("Invalid entity id.");
                    return;
                }
                GameController.getInstance().spawn(m, x, y);
            } catch (NumberFormatException e) {
                Console.error("Invalid argument values for spawn.");
                return;
            }
        }
    }

}
