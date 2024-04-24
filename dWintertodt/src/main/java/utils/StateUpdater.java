package utils;


import java.awt.*;

import static helpers.Interfaces.Client;

public class StateUpdater {
    public void updateStates(WTStates[] states) {
        for (WTStates state : states) {
            // Update each boolean based on some conditions or actions
            state.setFireAlive(updateFireAlive(state));
            state.setNeedsReburning(updateNeedsReburning(state));
            state.setNeedsFixing(updateNeedsFixing(state));
            state.setMageDead(updateMageDead(state));
        }
    }

    private boolean updateFireAlive(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.FIRE_ALIVE.getColor();

        return Client.isColorInRect(checkColor, checkRect, 10);
    }

    private boolean updateNeedsReburning(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.NEEDS_REBURNING.getColor();

        return Client.isColorInRect(checkColor, checkRect, 10);
    }

    private boolean updateNeedsFixing(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.NEEDS_FIXING.getColor();

        return Client.isColorInRect(checkColor, checkRect, 10);
    }

    private boolean updateMageDead(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        Color checkColor = StateColor.MAGE_DEAD.getColor();

        return Client.isColorInRect(checkColor, checkRect, 10);
    }
}
