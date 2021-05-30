package undc.handlers;

import undc.controllers.Console;
import undc.objects.BooleanCVar;
import undc.objects.CVar;
import undc.objects.DoubleCVar;
import undc.objects.IntCVar;
import undc.objects.StringCVar;

/**
 * Class representing the collection of all CVars.
 */
public class Vars {
    private static final StringCVar[] STRING_VARS = new StringCVar[0];
    private static final BooleanCVar[] BOOLEAN_VARS = new BooleanCVar[0];
    private static final IntCVar[] INT_VARS = new IntCVar[25];
    private static final DoubleCVar[] DOUBLE_VARS = new DoubleCVar[16];

    public static boolean DEBUG = false;
    public static boolean CHEATS = false;

    /**
     * Loads all of the CVars and their default values. These will be overwritten by the Config for anyu custom values.
     */
    public static void load() {
        /* CVar Prefixes:
         * gc - Graphics: things to do with graphics, such as window size, quality, etc.
         * sv - Server: things to do with server settings, such as tickrate, player health, etc.
         * ai - AI: things related to ai, such as monsters
         */

        // Integer CVars
        INT_VARS[0] = new IntCVar("gc_screen_width", "screenWidth", 400, 7680, 1920, false);
        INT_VARS[1] = new IntCVar("gc_screen_height", "screenHeight", 200, 4320, 1080, false);
        INT_VARS[2] = new IntCVar("sv_tickrate", "fps", 10, 1000, 200, false);
        INT_VARS[3] = new IntCVar("sv_player_health", "playerHP", 1, 10000, 100);
        INT_VARS[4] = new IntCVar("sv_player_attack_range", "playerRange", 0, 1000, 20);
        INT_VARS[5] = new IntCVar("sv_inventory_rows", "invRows", 1, 100, 2);
        INT_VARS[6] = new IntCVar("sv_inventory_cols", "invCols", 1, 100, 5);
        INT_VARS[7] = new IntCVar("sv_monsters_min", "minMonsters", 0, 100, 1);
        INT_VARS[8] = new IntCVar("sv_monsters_max", "maxMonsters", 0, 100, 1);
        INT_VARS[9] = new IntCVar("sv_obstacles_min", "minObs", 0, 100, 2);
        INT_VARS[10] = new IntCVar("sv_obstacles_max", "maxObs", 0, 100, 5);
        INT_VARS[11] = new IntCVar("sv_itemdrop_min", "minDrop", 0, 100, 1);
        INT_VARS[12] = new IntCVar("sv_itemdrop_max", "maxDrop", 0, 100, 2);
        INT_VARS[13] = new IntCVar("ai_monster_move_range", "monsterMoveRange", 0, 100000, 300);
        INT_VARS[14] = new IntCVar("ai_boss_move_range", "bossMoveRange", 0, 100000, 600);
        INT_VARS[15] = new IntCVar("ai_monster_move_min", "monsterMoveMin", 0, 100000, 10);
        INT_VARS[16] = new IntCVar("ai_monster_attack_range", "monsterRange", 0, 1000, 10);
        INT_VARS[17] = new IntCVar("ai_monster_reaction_time", "monsterReact", 0, 10000, 300);
        INT_VARS[18] = new IntCVar("ai_boss_reaction_time", "bossReact", 0, 10000, 150);
        INT_VARS[19] = new IntCVar("gc_monster_fade_dur", "monsterFade", 0, 10000, 400);
        INT_VARS[19].setModifiable(false);
        INT_VARS[20] = new IntCVar("gc_healthbar_height", "healthbarHeight", 0, 1000, 5);
        INT_VARS[20].setModifiable(false);
        INT_VARS[21] = new IntCVar("gc_canvas_padding", "canvasPadding", 0, 1000, 150);
        INT_VARS[21].setModifiable(false);
        INT_VARS[22] = new IntCVar("sv_player_width", "playerWidth", 0, 100, 16);
        INT_VARS[23] = new IntCVar("sv_player_height", "playerHeight", 0, 100, 16);
        INT_VARS[24] = new IntCVar("sv_player_pickup_range", "playerPickup", 0, 1000, 10);

        // Double CVars
        DOUBLE_VARS[0] = new DoubleCVar("sv_acceleration", "accel", 0, 10000, 1000);
        DOUBLE_VARS[1] = new DoubleCVar("sv_max_velocity", "maxVel", 0, 1000, 100);
        DOUBLE_VARS[2] = new DoubleCVar("sv_friction", "friction", 0, 10000, 2000);
        DOUBLE_VARS[3] = new DoubleCVar("gc_ppu", "ppu", 0, 100, 2);
        DOUBLE_VARS[3].setModifiable(false);
        DOUBLE_VARS[4] = new DoubleCVar("sv_precision", "precision", 0, 1000000, 10000);
        DOUBLE_VARS[4].setModifiable(false);
        DOUBLE_VARS[5] = new DoubleCVar("sv_self_damage_modifier", "selfDamageMod", 0, 10, 0.75);
        DOUBLE_VARS[6] = new DoubleCVar("sv_fist_damage", "fistDamage", 0, 100, 1);
        DOUBLE_VARS[7] = new DoubleCVar("sv_fist_cooldown", "fistCooldown", 0, 10000, 500);
        DOUBLE_VARS[8] = new DoubleCVar("sv_obstacle_gendist", "obsDist", 0, 1000, 64);
        DOUBLE_VARS[9] = new DoubleCVar("sv_monster_gold", "monsterGold", 0, 1000, 20);
        DOUBLE_VARS[10] = new DoubleCVar("gc_dropitem_scale", "dropScale", 0, 10, 0.75);
        DOUBLE_VARS[10].setModifiable(false);
        DOUBLE_VARS[11] = new DoubleCVar("sv_dropitem_distance", "dropDist", 0, 1000, 25);
        DOUBLE_VARS[12] = new DoubleCVar("sv_modifier_medium", "modMed", 0, 10, 1.5);
        DOUBLE_VARS[12].setModifiable(false);
        DOUBLE_VARS[13] = new DoubleCVar("sv_modifier_hard", "modHard", 0, 10, 2);
        DOUBLE_VARS[13].setModifiable(false);
        DOUBLE_VARS[14] = new DoubleCVar("gc_explosion_maxwidth", "explosionWidth", 0, 1000, 75);
        DOUBLE_VARS[15] = new DoubleCVar("gc_effect_scale", "effectScale", 0, 10, 1.5);
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
        for (StringCVar v : STRING_VARS) {
            if (v == null) {
                break;
            } else if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
                return v;
            }
        }
        for (BooleanCVar v : BOOLEAN_VARS) {
            if (v == null) {
                break;
            } else if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
                return v;
            }
        }
        for (IntCVar v : INT_VARS) {
            if (v == null) {
                break;
            } else if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
                return v;
            }
        }
        for (DoubleCVar v : DOUBLE_VARS) {
            if (v == null) {
                break;
            } else if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
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
