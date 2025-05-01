package com.canja.kutowerdefence.domain;

public class EnemyDescription {
    private String name;
    private int hitpoints;
    private int spellResistance;
    private int arrowResistance;
    private float speed;
    private int gold;


    public EnemyDescription(String name, int hitpoints, int spellResistance,int arrowResistance, float speed, int gold) {
        this.name = name;
        this.hitpoints = hitpoints;
        this.spellResistance = spellResistance;
        this.arrowResistance = arrowResistance;
        this.speed = speed;
        this.gold = gold;
    }

    public String getName() {
        return name;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public void setHitpoints(int val) {
        hitpoints = val;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float val) {
        speed = val;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int val) {
        gold = val;
    }

    public int getArrowResistance() {
        return arrowResistance;
    }

    public int getSpellResistance() {
        return spellResistance;
    }

}