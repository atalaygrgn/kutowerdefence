package com.canja.kutowerdefence.state;

import com.canja.kutowerdefence.domain.GameSession;
public class NormalState extends AbstractSpeedState{
    public NormalState(GameSession gameSession) {
        super(gameSession);
    }

    @Override
    public void toggleSpeed() {
        speedMultiplier(2f);
        
        gameSession.setSpeedState(gameSession.getFastState());
    }

    public String getNextText() {
        return "x2";
    }
}
