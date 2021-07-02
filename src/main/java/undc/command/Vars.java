package undc.command;

import java.util.ArrayList;

/**
 * Class representing the collection of all CVars.
 */
public class Vars {
    private static final ArrayList<StringCVar> STRING_VARS = new ArrayList<>();
    private static final ArrayList<BooleanCVar> BOOLEAN_VARS = new ArrayList<>();
    private static final ArrayList<IntCVar> INT_VARS = new ArrayList<>();
    private static final ArrayList<DoubleCVar> DOUBLE_VARS = new ArrayList<>();

    public static boolean DEBUG = false;
    public static boolean CHEATS = false;

    /**
     * Loads all of the CVars and their default values. These will be overwritten by the Config for any custom values.
     */
    public static void load() {
        /* CVar Prefixes:
         * gc - Graphics: things to do with graphics, such as window size, quality, etc.
         * sv - Server: things to do with server settings, such as tickrate, player health, etc.
         * ai - AI: things related to ai, such as monsters
         */

        // Boolean CVars
        BOOLEAN_VARS.add(new BooleanCVar("gm_god", "god", false, true, false));
        BOOLEAN_VARS.add(new BooleanCVar("sv_infinite_ammo", "infiniteAmmo", false, true, true));

        // Integer CVars
        INT_VARS.add(new IntCVar("gc_screen_width", "screenWidth", 400, 7680, 1366, false));
        INT_VARS.add(new IntCVar("gc_screen_height", "screenHeight", 200, 4320, 768, false));
        INT_VARS.add(new IntCVar("sv_tickrate", "fps", 10, 1000, 200, false));
        INT_VARS.add(new IntCVar("sv_player_health", "playerHP", 1, 10000, 100));
        INT_VARS.add(new IntCVar("sv_player_attack_range", "playerRange", 0, 1000, 20));
        INT_VARS.add(new IntCVar("sv_inventory_rows", "invRows", 1, 100, 2));
        INT_VARS.add(new IntCVar("sv_inventory_cols", "invCols", 1, 100, 5));
        INT_VARS.add(new IntCVar("sv_monsters_min", "minMonsters", 0, 100, 1));
        INT_VARS.add(new IntCVar("sv_monsters_max", "maxMonsters", 0, 100, 1));
        INT_VARS.add(new IntCVar("sv_obstacles_min", "minObs", 0, 100, 2));
        INT_VARS.add(new IntCVar("sv_obstacles_max", "maxObs", 0, 100, 5));
        INT_VARS.add(new IntCVar("sv_itemdrop_min", "minDrop", 0, 100, 1));
        INT_VARS.add(new IntCVar("sv_itemdrop_max", "maxDrop", 0, 100, 2));
        INT_VARS.add(new IntCVar("ai_monster_move_range", "monsterMoveRange", 0, 100000, 300));
        INT_VARS.add(new IntCVar("ai_boss_move_range", "bossMoveRange", 0, 100000, 600));
        INT_VARS.add(new IntCVar("ai_monster_move_min", "monsterMoveMin", 0, 100000, 10));
        INT_VARS.add(new IntCVar("ai_monster_attack_range", "monsterRange", 0, 1000, 10));
        INT_VARS.add(new IntCVar("ai_monster_reaction_time", "monsterReact", 0, 10000, 300));
        INT_VARS.add(new IntCVar("ai_boss_reaction_time", "bossReact", 0, 10000, 150));
        INT_VARS.add(new IntCVar("gc_monster_fade_dur", "monsterFade", 0, 10000, 400, true, false));
        INT_VARS.add(new IntCVar("gc_healthbar_height", "healthbarHeight", 0, 1000, 5, true, false));
        INT_VARS.add(new IntCVar("gc_canvas_padding", "canvasPadding", 0, 1000, 150, true, false));
        INT_VARS.add(new IntCVar("sv_player_width", "playerWidth", 0, 100, 16, true, false));
        INT_VARS.add(new IntCVar("sv_player_height", "playerHeight", 0, 100, 16, true, false));
        INT_VARS.add(new IntCVar("sv_player_pickup_range", "playerPickup", 0, 1000, 25));
        INT_VARS.add(new IntCVar("sv_precision", "precision", 0, 1000000, 10000, true, false));
        INT_VARS.add(new IntCVar("sv_obstacle_gendist", "obsDist", 0, 1000, 64));
        INT_VARS.add(new IntCVar("sv_acceleration", "accel", 0, 10000, 1000));
        INT_VARS.add(new IntCVar("sv_max_velocity", "maxVel", 0, 1000, 100));
        INT_VARS.add(new IntCVar("sv_friction", "friction", 0, 10000, 2000));
        INT_VARS.add(new IntCVar("sv_dropitem_distance", "dropDist", 0, 1000, 25));
        INT_VARS.add(new IntCVar("gc_explosion_maxwidth", "explosionWidth", 0, 1000, 75));
        INT_VARS.add(new IntCVar("sv_interact_distance", "interactDist", 0, 1000, 30, true, false));
        INT_VARS.add(new IntCVar("sv_monster_gold", "monsterGold", 0, 1000, 20));
        INT_VARS.add(new IntCVar("sv_monster_xp", "monsterXp", 0, 1000, 10));

        // Double CVars
        DOUBLE_VARS.add(new DoubleCVar("gc_ppu", "ppu", 0, 100, 1.5, true, false));
        DOUBLE_VARS.add(new DoubleCVar("sv_self_damage_modifier", "selfDamageMod", 0, 10, 0.75));
        DOUBLE_VARS.add(new DoubleCVar("sv_fist_damage", "fistDamage", 0, 100, 1));
        DOUBLE_VARS.add(new DoubleCVar("sv_fist_cooldown", "fistCooldown", 0, 10, 0.5));
        DOUBLE_VARS.add(new DoubleCVar("gc_dropitem_scale", "dropScale", 0, 10, 0.75, true, false));
        DOUBLE_VARS.add(new DoubleCVar("sv_modifier", "modifier", 0, 10, 1, true, false));
        DOUBLE_VARS.add(new DoubleCVar("sv_modifier_medium", "modMed", 0, 10, 1.5, true, false));
        DOUBLE_VARS.add(new DoubleCVar("sv_modifier_hard", "modHard", 0, 10, 2, true, false));
        DOUBLE_VARS.add(new DoubleCVar("gc_effect_scale", "effectScale", 0, 10, 1.5));
        DOUBLE_VARS.add(new DoubleCVar("volume", "volume", 0, 1, 0.5));
    }

    /**
     * Retriever method for finding the value of a String CVar.
     * @param var Name of the CVar
     * @return Returns the value
     */
    public static String s(String var) {
        for (StringCVar v : STRING_VARS) {
            if (v == null) {
                continue;
            }
            if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
                return v.getVal();
            }
        }
        Console.error("String CVar '" + var + "' not found.");
        return "";
    }

    /**
     * Retriever method for finding the value of a boolean CVar.
     * @param var Name of the CVar
     * @return Returns the value
     */
    public static boolean b(String var) {
        if (var.equals("debug")) {
            return DEBUG;
        } else if (var.equalsIgnoreCase("cheats")) {
            return CHEATS;
        }
        for (BooleanCVar v : BOOLEAN_VARS) {
            if (v == null) {
                continue;
            }
            if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
                return v.getVal();
            }
        }
        Console.error("Boolean CVar '" + var + "' not found.");
        return false;
    }

    /**
     * Retriever method for finding the value of an integer CVar.
     * @param var Name of the CVar
     * @return Returns the value
     */
    public static int i(String var) {
        for (IntCVar v : INT_VARS) {
            if (v == null) {
                continue;
            }
            if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
                return v.getVal();
            }
        }
        Console.error("Integer CVar '" + var + "' not found.");
        return 0;
    }

    /**
     * Retriever method for finding the value of a double CVar.
     * @param var Name of the CVar
     * @return Returns the value
     */
    public static double d(String var) {
        for (DoubleCVar v : DOUBLE_VARS) {
            if (v == null) {
                continue;
            }
            if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
                return v.getVal();
            }
        }
        Console.error("Double CVar '" + var + "' not found.");
        return 0;
    }

    /**
     * Returns a list of all the CVars.
     * @return List of all CVars
     */
    public static ArrayList<CVar> all() {
        ArrayList<CVar> list = new ArrayList<>();
        list.addAll(STRING_VARS);
        list.addAll(BOOLEAN_VARS);
        list.addAll(INT_VARS);
        list.addAll(DOUBLE_VARS);
        list.add(find("cheats"));
        list.add(find("debug"));
        return list;
    }

    /**
     * Retriever method for finding a generalized CVar (or more specifically, if it exists).
     * @param var Name of the CVar
     * @return Returns the CVar
     */
    public static CVar find(String var) {
        // check for static variables
        if (var.equalsIgnoreCase("debug")) {
            BooleanCVar temp = new BooleanCVar("debug", "debug", false, false);
            temp.setVal("" + DEBUG);
            return temp;
        } else if (var.equalsIgnoreCase("cheats")) {
            BooleanCVar temp = new BooleanCVar("cheats", "cheats", false, false);
            temp.setVal("" + CHEATS);
            return temp;
        }
        for (CVar v : all()) {
            if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Sets the value of a CVar.
     * @param var Name of the CVar
     * @param val New value of the CVar
     * @return Returns true if successful, false otherwise
     */
    public static boolean set(String var, String val) {
        if (var == null) {
            return false;
        }
        if (var.equalsIgnoreCase("debug")) {
            if (val.equalsIgnoreCase("true")) {
                DEBUG = true;
            } else if (val.equalsIgnoreCase("false")) {
                DEBUG = false;
            } else {
                Console.error("Invalid Value.");
                return false;
            }
            return true;
        } else if (var.equalsIgnoreCase("cheats")) {
            if (val.equalsIgnoreCase("true")) {
                CHEATS = true;
            } else if (val.equalsIgnoreCase("false")) {
                CHEATS = false;
            } else {
                Console.error("Invalid Value.");
                return false;
            }
            // reset all changed values
            for (CVar v : all()) {
                if (v.requiresCheats()) {
                    v.reset();
                }
            }
            return true;
        }
        CVar v = find(var);
        if (v == null) {
            Console.error("Could not set " + var);
            return false;
        }
        return v.setVal(val);
    }

    /**
     * Resets the value of a CVar to the default.
     * @param var Name of the CVar
     * @return Returns true if successful, false otherwise
     */
    public static boolean reset(String var) {
        if (var == null) {
            return false;
        }
        CVar v = find(var);
        if (v == null) {
            Console.error("Could not reset " + var);
            return false;
        }
        v.reset();
        return true;
    }
}
