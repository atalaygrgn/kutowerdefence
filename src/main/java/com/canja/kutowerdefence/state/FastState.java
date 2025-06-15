package com.canja.kutowerdefence.state;

import com.canja.kutowerdefence.domain.GameSession;
public class FastState extends AbstractSpeedState{
    public FastState(GameSession gameSession) {
        super(gameSession);
    }

    @Override
    public void toggleSpeed() {
        speedMultiplier(2f);

        gameSession.setSpeedState(gameSession.getUltraFastState());
    }

    @Override
    public String getNextText() {
        return "x4";
    }
}
