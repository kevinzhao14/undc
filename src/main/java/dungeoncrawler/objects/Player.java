package dungeoncrawler.objects;

import dungeoncrawler.handlers.GameSettings;

public class Player extends Entity {
    private Weapon weapon;
    private int gold;
    public Player(int maxHealth, double attack, Weapon weapon) {
        super(maxHealth, attack, GameSettings.PLAYER_HEIGHT, GameSettings.PLAYER_WIDTH, null);
        this.weapon = weapon;
        this.gold = 0;
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
