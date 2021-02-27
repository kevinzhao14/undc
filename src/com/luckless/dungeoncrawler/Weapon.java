package com.luckless.dungeoncrawler;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Weapon {
    private String name;
    private ImageView sprite;
    private double damage;
    private double attackSpeed;

    public Weapon(String name, ImageView sprite, double damage, double attackSpeed) {
        this.name = name;
        this.sprite = sprite;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
    }
    public Weapon() {
        this("", null, 0, 0);
    }
}
