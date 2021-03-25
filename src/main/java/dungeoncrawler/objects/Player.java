package dungeoncrawler.objects;

import javafx.scene.image.ImageView;

public class Player extends Entity{
    private Weapon weapon;
    private int gold;
    public Player(int maxHealth, double attack, int height, int width, int posX, int posY,
                  Weapon weapon, ImageView node) {
        super(maxHealth, attack, height, width, node);
        this.weapon = weapon;
    }

    public Weapon getWeapon() {
        return this.weapon;
    }
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public int getGold() {
        return this.gold;
    }
    public void setGold(int gold) {
        this.gold = gold;
    }
}
