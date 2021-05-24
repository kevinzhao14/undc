package undc.handlers;

import undc.controllers.*;
import undc.objects.*;

public class Vars {
    private static final StringCVar[] STRING_VARS = new StringCVar[0];
    private static final BooleanCVar[] BOOLEAN_VARS = new BooleanCVar[0];
    private static final IntCVar[] INT_VARS = new IntCVar[25];
    private static final DoubleCVar[] DOUBLE_VARS = new DoubleCVar[16];

    public static boolean DEBUG = true;
    public static boolean CHEATS = false;

    public static void load() {
        /* CVar Prefixes:
         * gc - Graphics: things to do with graphics, such as window size, quality, etc.
         * sv - Server: things to do with server settings, such as tickrate, player health, etc.
         * ai - AI: things related to ai, such as monsters
         */

        INT_VARS[0] = new IntCVar("gc_screen_width", "screenWidth", 400, 7680, 1366, false);
        INT_VARS[1] = new IntCVar("gc_screen_height", "screenHeight", 200, 4320, 768, false);
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

    public static String s(String var) {
        for (StringCVar v : STRING_VARS) {
            if (v == null) continue;
            if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
                return v.getVal();
            }
        }
        Console.error("String CVar '" + var + "' not found.");
        return "";
    }
    public static boolean b(String var) {
        for (BooleanCVar v : BOOLEAN_VARS) {
            if (v == null) continue;
            if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
                return v.getVal();
            }
        }
        Console.error("Boolean CVar '" + var + "' not found.");
        return false;
    }
    public static int i(String var) {
        for (IntCVar v : INT_VARS) {
            if (v == null) continue;
            if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
                return v.getVal();
            }
        }
        Console.error("Integer CVar '" + var + "' not found.");
        return 0;
    }
    public static double d(String var) {
        for (DoubleCVar v : DOUBLE_VARS) {
            if (v == null) continue;
            if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
                return v.getVal();
            }
        }
        Console.error("Double CVar '" + var + "' not found.");
        return 0;
    }

    private static CVar find(String var) {
        int lens = STRING_VARS.length;
        int lenb = BOOLEAN_VARS.length;
        int leni = INT_VARS.length;
        int lend = DOUBLE_VARS.length;
        for (int i = 0; i < lens + lenb + leni + lend; i++) {
            CVar v;
            if (i >= lens + lenb + leni) {
                v = DOUBLE_VARS[i - (lens + lenb + leni)];
            } else if (i >= lens + lenb) {
                v = INT_VARS[i - (lens + lenb)];
            } else if (i >= lens) {
                v = BOOLEAN_VARS[i - lens];
            } else {
                v = STRING_VARS[i];
            }
            if (v == null) continue;
            if (v.getName().equalsIgnoreCase(var) || v.getNick().equalsIgnoreCase(var)) {
                return v;
            }
        }
        Console.error("CVar does not exist.");
        return null;
    }

    public static void set(String var, String val) {
        CVar v = find(var);
        if (var == null) return;
        v.setVal(val);
    }

    public static void reset(String var) {
        CVar v = find(var);
        if (var == null) return;
        v.reset();
    }
}
