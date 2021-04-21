package dungeoncrawler.objects;
import javafx.scene.image.Image;

public class Key extends Item {
    public Key(String name, String path, boolean isDroppable) {
        //note: keys are not stackable
        super(new Image(path), name, 0, isDroppable);
    }

    public Item copy() {
        return new Key(getName(), getSprite().getUrl(), isDroppable());
    }

    //does nothing
    public void use() {
        return;
    }
}
