package com.canja.kutowerdefence.state;

import com.canja.kutowerdefence.controller.WaveController;
import com.canja.kutowerdefence.domain.GameSession;

public class PausedState implements FlowState {
    GameSession gameSession;
    WaveController controller;

    public PausedState(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public void setWaveController(WaveController controller) {
        this.controller = controller;
    }

    public void togglePauseState() {
        controller.resumeWaves();

        gameSession.setFlowState(gameSession.getPlayingState());
    }

    public String getNextIcon() {
        return "file:src/main/resources/assets/ui/button/button_6.png";
    }
}
