package com.canja.kutowerdefence.domain;
import java.util.LinkedList;

public class Enemy {
    private float x,y;
    private float speed;
    private int hitpoint;
    private EnemyDescription description;
    private LinkedList<Point> path;
    private int currentPathIndex;


    public Enemy(EnemyDescription description, LinkedList<Point> path) {
        this.description = description;
        this.path = path;
        this.speed= description.getSpeed();
        this.hitpoint = description.getHitpoints();

        Point start = path.get(0);
        this.x= start.getX();
        this.y= start.getY();
    }
    public void update(float deltaTime){
        if(currentPathIndex >= path.size()){
            return;
        }
        Point target = path.get(currentPathIndex);
        float targetX = target.getX();
        float targetY = target.getY();

        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float)Math.sqrt(dx**2+dy**2);

        if(distance< speed * deltaTime){
            x= targetX;
            y= targetY;
            currentPathIndex++;
        } else {
            x += (dx/distance) * speed * deltaTime;
            y += (dy/distance) * speed * deltaTime;
        }
    }

    public void takeDamage(int damage, String attackType){
        float multiplier = 1.0f;
        if (attackType.equals("arrow")){
            multiplier = 0.5f;
        } else if (attackType.equals("spell")) {
            multiplier = 0.5f;
        }
        hitpoint -= (int)(damage * multiplier);
        //damage in arrow: knight<goblin
        //damage in spell: knight>goblin
    }

//    Enemy goblin= new Enemy(EnemyFactory.GOBLIN, path);
//    Enemy knight= new Enemy(EnemyFactory.KNIGHT, path);

}
