package com.canja.kutowerdefence.state;

import com.canja.kutowerdefence.domain.GameSession;
public class UltraFastState extends AbstractSpeedState{
    public UltraFastState(GameSession gameSession) {
        super(gameSession);
    }

    @Override
    public void toggleSpeed() {
        speedMultiplier(.125f);

        gameSession.setSpeedState(gameSession.getSlowState());
    }
    
    public String getNextText() {
        return "x0.5";
    }
}
