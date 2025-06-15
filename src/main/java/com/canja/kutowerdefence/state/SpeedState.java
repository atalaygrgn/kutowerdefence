package com.canja.kutowerdefence.state;

import com.canja.kutowerdefence.controller.WaveController;

public interface SpeedState {
    void setWaveController(WaveController controller);
    void toggleSpeed();
    String getNextText();
}
