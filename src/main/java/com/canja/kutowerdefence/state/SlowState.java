package com.canja.kutowerdefence.state;

import com.canja.kutowerdefence.domain.GameSession;
public class SlowState extends AbstractSpeedState{
    GameSession gameSession;

    public SlowState(GameSession gameSession) {
        super(gameSession);
    }

    @Override
    public void toggleSpeed() {
        speedMultiplier(2f);
        
        gameSession.setSpeedState(gameSession.getNormalState());
    }

    public String getNextText() {
        return "x1";
    }
}
