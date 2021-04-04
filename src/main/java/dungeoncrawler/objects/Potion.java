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

    public Potion(String name, String path, int stackSize,
                  boolean isDroppable, PotionType potionType, double potionModifier) {
        super(new Image(path), name, stackSize, isDroppable);
        type = potionType;
        modifier = potionModifier;
    }
    public Potion(String path, String itemName, PotionType potionType) {
        super(new Image(path), itemName);
        type = potionType;
    }

    public Potion copy() {
        return new Potion(getName(), getSprite().getUrl(), getMaxStackSize(),
                isDroppable(), type, modifier);
    }

    public PotionType getType() {
        return type;
    }
    public double getModifier() {
        return modifier;
    }
}
