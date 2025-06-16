package com.canja.kutowerdefence.state;

import com.canja.kutowerdefence.controller.GamePlayController;
import com.canja.kutowerdefence.controller.WaveController;
import com.canja.kutowerdefence.domain.GameSession;

public class PlayingState implements FlowState {
    GameSession gameSession;
    WaveController controller;
    GamePlayController gamePlayController;

    public PlayingState(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public void setWaveController(WaveController controller) {
        this.controller = controller;
    }

    public void setGamePlayController(GamePlayController controller) {
        this.gamePlayController = controller;
    }

    public void togglePauseState() {
        controller.pauseWaves();

        gameSession.setFlowState(gameSession.getPausedState());
        gamePlayController.getView().activateSaveButton();
    }

    public String getNextIcon() {
        return "file:src/main/resources/assets/ui/button/button_4.png";
    }
}
