package undc.general;

/**
 * Represents an object that can be interacted with.
 */
public interface Interactable {
    /**
     * Handles interaction.
     * @return Returns true on success, false on failure
     */
    boolean interact();
}
