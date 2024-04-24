package utils;


import java.awt.*;

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
        return false;
    }

    private boolean updateNeedsReburning(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        return false;
    }

    private boolean updateNeedsFixing(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        return false;
    }

    private boolean updateMageDead(WTStates state) {
        Rectangle checkRect = state.getRectangle();
        return false;
    }
}
