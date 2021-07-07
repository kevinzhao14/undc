package undc.entity;

/**
 * Represents dialogue options for an entity.
 */
public class Dialogue {
    private final String[] dialogue;
    private final boolean loops;

    private int pos;

    /**
     * Constructor.
     * @param dialogue Lines that compose the dialogue
     * @param loops Whether the dialogue should loop
     */
    public Dialogue(String[] dialogue, boolean loops) {
        this.dialogue = dialogue;
        this.loops = loops;
        this.pos = 0;
    }

    /**
     * Used to get the next line in the dialogue.
     * @return Returns the next line
     */
    public String next() {
        if (pos >= dialogue.length) {
            return "";
        }
        String res = dialogue[pos++];
        if (loops) {
            pos %= dialogue.length;
        }
        return res;
    }
}
