package com.opengg.core.render.window.awt.input;

import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.io.input.mouse.MouseScrollHandler;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class AWTMouseScrollHandler implements MouseWheelListener, MouseScrollHandler {
    double pos = 0;

    @Override
    public double getWheelX() {
        return 0;
    }

    @Override
    public double getWheelY() {
        return pos;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        pos += e.getPreciseWheelRotation();

        if(e.getPreciseWheelRotation() > 0) MouseController.scrolledUp();
        if(e.getPreciseWheelRotation() < 0) MouseController.scrolledDown();
    }
}
