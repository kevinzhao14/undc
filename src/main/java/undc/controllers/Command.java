package undc.controllers;

import undc.gamestates.GameScreen;
import undc.gamestates.HomeScreen;
import undc.handlers.Controls;
import undc.handlers.Vars;
import undc.objects.CVar;
import undc.objects.Item;
import undc.objects.Monster;
import undc.objects.Obstacle;
import undc.objects.Player;

import java.util.ArrayList;

class Command {
    String name;
    String desc;
    String format;

    private final CommandObj obj;

    /**
     * Constructor for a Command.
     * @param name Name of the command, used to run the command
     * @param format - Format of the command after the name. &lt;foo&gt; for required, [foo] for optional param
     * @param desc - Description of the command & what it does
     * @param runner Code to run when the command is called
     */
    public Command(String name, String format, String desc, CommandObj runner) {
        this.name = name;
        this.format = format;
        this.desc = desc;
        this.obj = runner;
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

    /**
     * Returns a list of all of the commands.
     * @return Returns a list of all of the commands available
     */
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
        commands.add(new Command("spawn", "<id> [x] [y]", "Spawns an entity. "
                + "Defaults to the player's coordinates.", Command::spawn));
        commands.add(new Command("help", "<command>", "Provides information about the command.", Command::help));
        commands.add(new Command("disconnect", "", "Disconnects from an active game.", Command::disconnect));
        commands.add(new Command("god", "", "Toggles god mode. Requires cheats.", Command::god));
        commands.add(new Command("place", "<id> <x> <y>", "Places an obstacle", Command::place));

        // player commands
        commands.add(new Command("gm_player_health", "[value]", "Returns or sets the value of the "
                + "player's health.", Command::playerHealth));

        // add cvars
        for (CVar v : Vars.all()) {
            if (!v.isModifiable()) {
                continue;
            }
            String desc = "Returns or sets the value of " + v.getName() + "."
                    + (v.requiresCheats() ? " Requires cheats." : "");
            Command c = new Command(v.getName(), "[value]", desc, e -> cvar(arrAddToFront(e, v.getName())));
            commands.add(c);
        }

        return commands;
    }

    /**
     * Adds a String to the front of an array.
     * @param arr Array to add to
     * @param elem String to add
     * @return Returns a new array with the string at index 0
     */
    private static String[] arrAddToFront(String[] arr, String elem) {
        String[] newArr = new String[arr.length + 1];
        newArr[0] = elem;
        System.arraycopy(arr, 0, newArr, 1, arr.length);
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

    /**
     * Binds a command to a key or gets what command is bound to a key.
     * @param args Arguments
     */
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

    /**
     * Unbinds a key.
     * @param args Arguments
     */
    private static void unbind(String[] args) {
        if (args.length != 1) {
            Console.error("Invalid arguments for unbind.");
            return;
        }
        String key = clean(args[0]);
        Controls.getInstance().removeKey(key);
    }

    /**
     * Gets or sets the value of a CVar.
     * @param args Arguments
     */
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

    /**
     * Resets the value of a CVar to its default.
     * @param args Arguments
     */
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

    /**
     * Finds all commands that have a given string.
     * @param args Arguments
     */
    private static void find(String[] args) {
        if (args.length != 1) {
            Console.run("find \"\"", false);
            return;
        }
        String search = clean(args[0]);
        StringBuilder res = new StringBuilder();
        for (Command c : Console.COMMANDS) {
            if (c.getName().contains(search)) {
                res.append(c.getName());
                res.append(c.getFormat().length() > 0 ? " " + c.getFormat() : "");
                res.append(" - ");
                res.append(c.getDesc());
                res.append("<br>");
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

    /**
     * Gives the player an item with a specified quantity or 1.
     * @param args Arguments
     */
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
            }
        }
    }

    /**
     * Spawns an entity at the specified location.
     * @param args Arguments
     */
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
            }
        }
    }

    /**
     * Prints details about a specified command.
     * @param args Arguments
     */
    private static void help(String[] args) {
        if (args.length != 1) {
            Console.error("Invalid arguments for help.");
            return;
        }
        String cmd = clean(args[0]);

        // find the command
        for (Command c : Console.COMMANDS) {
            if (c.getName().equalsIgnoreCase(cmd)) {
                String res = c.getName();
                res += (c.getFormat().length() > 0 ? " " + c.getFormat() : "");
                res += " - " + c.getDesc();
                Console.print(res);
                return;
            }
        }
        Console.warn("Could not find the command '" + cmd + "'.");
    }

    /**
     * Disconnects the player from a game.
     * @param args Arguments
     */
    private static void disconnect(String[] args) {
        if (args.length != 0) {
            Console.error("Invalid arguments for disconnect.");
            return;
        }
        // verify current screen/state
        if (!(Controller.getState() instanceof GameScreen)) {
            Console.warn("Could not disconnect, no active game.");
            return;
        }
        GameController.getInstance().stop();
        HomeScreen.resetInstance();
        Controller.setState(HomeScreen.getInstance());
    }

    /**
     * Sets or gets the player's health.
     * @param args Arguments
     */
    private static void playerHealth(String[] args) {
        if (!(args.length == 0 || args.length == 1)) {
            Console.error("Invalid arguments for player health.");
            return;
        }
        if (!(Controller.getState() instanceof GameScreen)) {
            Console.error("Cannot get player health, no active game.");
            return;
        }
        Player player = GameScreen.getInstance().getPlayer();
        if (player == null) {
            Console.error("Player does not exist.");
            return;
        }
        if (args.length == 0) {
            Console.print(player.getHealth() + " HP");
        } else {
            try {
                int health = Integer.parseInt(args[0]);
                if (health <= 0 || health > player.getMaxHealth()) {
                    Console.error("Invalid value for player health.");
                    return;
                }
                player.setHealth(health);
                GameScreen.getInstance().updateHud();
                Console.print("Player health set to " + health);
            } catch (NumberFormatException e) {
                Console.error("Invalid format for player health.");
            }
        }
    }

    /**
     * Toggles god mode/invulnerability.
     * @param args Arguments
     */
    private static void god(String[] args) {
        if (args.length != 0) {
            Console.error("Invalid arguments for god.");
            return;
        }
        if (!(Controller.getState() instanceof GameScreen)) {
            Console.error("Cannot get player health, no active game.");
            return;
        }
        Player player = GameScreen.getInstance().getPlayer();
        if (player == null) {
            Console.error("Player does not exist.");
            return;
        }
        if (!Vars.CHEATS) {
            Console.error("Cheats are disabled.");
            return;
        }
        CVar v = Vars.find("gm_god");
        if (v == null) {
            Console.error("Something went wrong.");
            return;
        }
        if (v.setVal("" + !Vars.b("gm_god"), true)) {
            Console.print("God mode " + (Vars.b("gm_god") ? "on." : "off."));
        } else {
            Console.error("Failed to change god mode.");
        }
    }

    /**
     * Places an obstacle.
     * @param args Arguments
     */
    private static void place(String[] args) {
        if (!Vars.CHEATS) {
            Console.error("Cheats are disabled.");
            return;
        }
        if (args.length != 3) {
            Console.error("Invalid arguments for place.");
            return;
        }
        try {
            int id = Integer.parseInt(args[0]);
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            if (!(Controller.getState() instanceof GameScreen)) {
                Console.error("Cannot spawn because there is no game.");
                return;
            }
            Obstacle o = DataManager.OBSTACLES.get(id);
            if (o == null) {
                Console.error("Invalid entity id.");
                return;
            }
            GameController.getInstance().spawn(o, x, y);
        } catch (NumberFormatException e) {
            Console.error("Invalid argument values for spawn.");
        }
    }
}
