package com.canja.kutowerdefence.state;

import com.canja.kutowerdefence.domain.GameSession;
public class SlowState extends AbstractSpeedState{
    public SlowState(GameSession gameSession) {
        super(gameSession);
    }

    @Override
    public void toggleSpeed() {
        speedMultiplier(2f);

        gameSession.setSpeedState(gameSession.getNormalState());
    }

    @Override
    public String getNextText() {
        return "x1";
    }
}
