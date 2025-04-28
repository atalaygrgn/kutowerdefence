package com.canja.kutowerdefence.domain;

public class EnemyDescription {
    private String name;
    private int hitpoints;
    private int weakness;
    private int spellResistance;
    private int arrowResistance;
    private float speed;
    private int gold;


    public EnemyDescription(String name, int hitpoints, int weakness, float speed, int gold) {
        this.name = name;
        this.hitpoints = hitpoints;
        this.weakness = weakness;
        this.speed = speed;
        this.gold = gold;
    }

    public String getName() {
        return name;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public int getWeakness() {
        return weakness;
    }

    public float getSpeed() {
        return speed;
    }

    public int getGold() {
        return gold;
    }
}
