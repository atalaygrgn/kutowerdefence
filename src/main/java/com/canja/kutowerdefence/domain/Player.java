package com.canja.kutowerdefence.domain;

public class Player {
    private int goldAmount = 0;
    private int currentHealth = -1;

    public Player(int goldAmount, int totalHealth) {
        this.goldAmount = goldAmount;
        this.currentHealth = totalHealth;
    }

    public int getGoldAmount() {
        return goldAmount;
    }

    public void deductGold(int val) {
        if (goldAmount < val) {
            System.out.println("Not enough gold!");
            return;
        }
        
        goldAmount -= val;
    }

    public void gainGold(int val) {
        goldAmount += val;
    }

    public int getHealth() {
        return currentHealth;
    }

    public void loseHealth() {
        currentHealth--;
    }

    public boolean isPlayerDead() {
        return currentHealth <= 0;
    }
}
