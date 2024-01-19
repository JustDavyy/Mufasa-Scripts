package utils;

import javax.xml.stream.Location;

import java.awt.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import static helpers.Interfaces.Client;
import static helpers.Interfaces.Condition;

public class MiningHelper {
    public void checkPositions(LocationInfo locationInfo, VeinColors veinColors) {
        // You need to define Client or a similar class that has the isColorInRect method
        if (isValidRect(locationInfo.getCheckLocation1()) && Client.isColorInRect(veinColors.getActiveColor(), locationInfo.getCheckLocation1(), 10)) { // Assuming tolerance of 10
            clickPositions(locationInfo, 1, veinColors);
        }

        if (isValidRect(locationInfo.getCheckLocation2()) && Client.isColorInRect(veinColors.getActiveColor(), locationInfo.getCheckLocation2(),  10)) {
            clickPositions(locationInfo, 2, veinColors);
        }

        if (isValidRect(locationInfo.getCheckLocation3()) && Client.isColorInRect(veinColors.getActiveColor(), locationInfo.getCheckLocation3(), 10)) {
            clickPositions(locationInfo, 3, veinColors);
        }
    }

    public void clickPositions(LocationInfo locationInfo, int position, VeinColors veinColors) {
        // Assuming Client has a tap method
        switch (position) {
            case 1:
                if (isValidRect(locationInfo.getClickLocation1())) {
                    Client.tap(locationInfo.getClickLocation1());
                    Condition.wait(() -> Client.isColorInRect(veinColors.getInactiveColor(), locationInfo.getCheckLocation1(), 10), 50, 10);
                }
                break;
            case 2:
                if (isValidRect(locationInfo.getClickLocation2())) {
                    Client.tap(locationInfo.getClickLocation2());
                    Condition.wait(() -> Client.isColorInRect(veinColors.getInactiveColor(), locationInfo.getCheckLocation2(), 10), 50, 10);
                }
                break;
            case 3:
                if (isValidRect(locationInfo.getClickLocation3())) {
                    Client.tap(locationInfo.getClickLocation3());
                    Condition.wait(() -> Client.isColorInRect(veinColors.getInactiveColor(), locationInfo.getCheckLocation3(), 10), 50, 10);
                }
                break;
        }
    }

    private boolean isValidRect(Rectangle rect) {
        return !(rect.width == 1 && rect.height == 1 && rect.x == 1 && rect.y == 1);
    }
}
