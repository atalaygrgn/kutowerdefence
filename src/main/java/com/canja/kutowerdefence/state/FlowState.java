package com.canja.kutowerdefence.state;

import com.canja.kutowerdefence.controller.GamePlayController;
import com.canja.kutowerdefence.controller.WaveController;

public interface FlowState {
    void setWaveController(WaveController controller);
    void setGamePlayController(GamePlayController controller);
    void togglePauseState();
    String getNextIcon();
}
