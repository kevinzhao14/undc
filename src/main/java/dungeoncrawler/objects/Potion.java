package dungeoncrawler.objects;
import javafx.scene.image.Image;

/**
 * Implementation of the Potion data class
 *
 * @author Manas Harbola
 */
public class Potion extends Item {
    private PotionType type;
    private double modifier;

    public Potion(String path, String itemName, int stackSize,
                  boolean isDroppable, PotionType potionType, double potionModifier) {
        super(new Image(path), itemName, stackSize, isDroppable);
        type = potionType;
        modifier = potionModifier;
    }

    public Potion(String path, String itemName, PotionType potionType) {
        super(new Image(path), itemName);
        type = potionType;
    }

    public PotionType getType() {
        return type;
    }
    public double getModifier() {
        return modifier;
    }
}
