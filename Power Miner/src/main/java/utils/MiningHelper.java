package utils;

import helpers.utils.Tile;

import java.awt.*;
import java.util.Random;

import static helpers.Interfaces.Client;
import static helpers.Interfaces.Condition;

public class MiningHelper {
    private final Random random = new Random();

    public void checkPositions(LocationInfo locationInfo, VeinColors veinColors) {
        // Check location 1
        if (isValidRect(locationInfo.getCheckLocation1()) && Client.isAnyColorInRect(veinColors.getActiveColor(), locationInfo.getCheckLocation1(), 10)) {
            clickPositions(locationInfo, 2, veinColors);
        }

        // Check location 2
        if (isValidRect(locationInfo.getCheckLocation2()) && Client.isAnyColorInRect(veinColors.getActiveColor(), locationInfo.getCheckLocation2(), 10)) {
            clickPositions(locationInfo, 2, veinColors);
        }

        // Check location 3
        if (isValidRect(locationInfo.getCheckLocation3()) && Client.isAnyColorInRect(veinColors.getActiveColor(), locationInfo.getCheckLocation3(), 10)) {
            clickPositions(locationInfo, 3, veinColors);
        }
    }

    public void clickPositions(LocationInfo locationInfo, int position, VeinColors veinColors) {
        // Assuming Client has a tap method
        switch (position) {
            case 1:
                if (isValidRect(locationInfo.getClickLocation1())) {
                    Client.tap(locationInfo.getClickLocation1());
                    Condition.wait(() -> Client.isAnyColorInRect(veinColors.getInactiveColor(), locationInfo.getCheckLocation1(), 10), 50, 10);
                }
                break;
            case 2:
                if (isValidRect(locationInfo.getClickLocation2())) {
                    Client.tap(locationInfo.getClickLocation2());
                    Condition.wait(() -> Client.isAnyColorInRect(veinColors.getInactiveColor(), locationInfo.getCheckLocation2(), 10), 50, 10);
                }
                break;
            case 3:
                if (isValidRect(locationInfo.getClickLocation3())) {
                    Client.tap(locationInfo.getClickLocation3());
                    Condition.wait(() -> Client.isAnyColorInRect(veinColors.getInactiveColor(), locationInfo.getCheckLocation3(), 10), 50, 10);
                }
                break;
        }
    }

    private boolean isValidRect(Rectangle rect) {
        return !(rect.width == 1 && rect.height == 1 && rect.x == 1 && rect.y == 1);
    }

    private Tile[] pickRandomPath(PathsToBanks pathsToBanks) {
        int pick = random.nextInt(3);
        switch (pick) {
            case 0:
                return pathsToBanks.Path1();
            case 1:
                return pathsToBanks.Path2();
            case 2:
                return pathsToBanks.Path3();
            default:
                // In case of an unexpected value, return null or handle appropriately
                return null;
        }
    }
}
