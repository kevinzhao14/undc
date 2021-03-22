package dungeoncrawler.objects;

public class Player extends Entity{
    private Weapon weapon;
    private int gold;
    public Player(int maxHealth, int health, int attack,
                  int height, int width, int posX, int posY, Weapon weapon) {
        super(maxHealth, health, attack, height, width, posX, posY);
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
