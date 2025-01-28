package tasks;

import utils.Task;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static helpers.Interfaces.*;
import static main.dTutIsland.accountCreationDone;

public class AccountCreation extends Task {
    private static final Rectangle lookUpNameRect = new Rectangle(322, 265, 89, 17);
    private static final Rectangle randomNameCheckArea = new Rectangle(212, 240, 309, 14);
    private static final Rectangle setAppearanceCheckArea = new Rectangle(183, 32, 173, 27);
    private static final Rectangle randomName1 = new Rectangle(222, 238, 67, 9);
    private static final Rectangle randomName2 = new Rectangle(340, 240, 51, 5);
    private static final Rectangle randomName3 = new Rectangle(442, 238, 71, 8);

    private static final Rectangle headLeft = new Rectangle(226, 236, 28, 17);
    private static final Rectangle headRight = new Rectangle(323, 236, 30, 18);
    private static final Rectangle jawLeft = new Rectangle(226, 269, 28, 19);
    private static final Rectangle jawRight = new Rectangle(325, 271, 27, 19);
    private static final Rectangle torsoLeft = new Rectangle(226, 307, 25, 18);
    private static final Rectangle torsoRight = new Rectangle(326, 307, 27, 17);
    private static final Rectangle armsLeft = new Rectangle(226, 342, 27, 19);
    private static final Rectangle armsRight = new Rectangle(324, 342, 30, 17);
    private static final Rectangle handsLeft = new Rectangle(227, 377, 27, 18);
    private static final Rectangle handsRight = new Rectangle(325, 376, 27, 17);
    private static final Rectangle legsLeft = new Rectangle(226, 411, 29, 18);
    private static final Rectangle legsRight = new Rectangle(324, 414, 26, 16);
    private static final Rectangle feetLeft = new Rectangle(225, 446, 28, 17);
    private static final Rectangle feetRight = new Rectangle(325, 446, 26, 20);
    private static final Rectangle hairColorLeft = new Rectangle(541, 237, 26, 17);
    private static final Rectangle hairColorRight = new Rectangle(639, 237, 26, 16);
    private static final Rectangle torsoColorLeft = new Rectangle(540, 272, 28, 20);
    private static final Rectangle torsoColorRight = new Rectangle(639, 272, 28, 19);
    private static final Rectangle legsColorLeft = new Rectangle(539, 306, 30, 19);
    private static final Rectangle legsColorRight = new Rectangle(639, 306, 26, 20);
    private static final Rectangle feetColorLeft = new Rectangle(541, 342, 27, 18);
    private static final Rectangle feetColorRight = new Rectangle(639, 342, 28, 16);
    private static final Rectangle skinColorLeft = new Rectangle(540, 377, 27, 17);
    private static final Rectangle skinColorRight = new Rectangle(640, 378, 27, 18);
    private static final Rectangle setWoman = new Rectangle(617, 448, 49, 17);
    private static final Rectangle pronounsDropDown = new Rectangle(381, 235, 132, 12);
    private static final Rectangle pronounsSheHer = new Rectangle(419, 282, 41, 12);
    private static final Rectangle pronounsTheyThem = new Rectangle(414, 308, 50, 10);
    private static final Rectangle confirmButton = new Rectangle(386, 446, 119, 18);

    public boolean activate() {
        return !accountCreationDone;
    }

    @Override
    public boolean execute() {
        Logger.log("Running account creation task");

        Logger.log("Send random keystroke");
        Client.sendKeystroke(String.valueOf(getRandomCharacter()));
        Condition.sleep(750, 1000);

        Logger.log("Tap look up name");
        Client.tap(lookUpNameRect);
        Condition.wait(() -> Client.isColorInRect(Color.decode("#ff981f"), randomNameCheckArea, 5), 100, 100);

        if (Client.isColorInRect(Color.decode("#ff981f"), randomNameCheckArea, 5)) {
            Logger.log("Random names are present");
        } else {
            Logger.log("Something went wrong, stopping script!");
            Script.stop();
        }

        Logger.log("Select one of three random generated names");
        Client.tap(getRandomRectangle());
        Condition.sleep(1750, 3000);

        Logger.log("Set name");
        Client.tap(lookUpNameRect);
        Condition.wait(() -> Client.isColorInRect(Color.decode("#0000ff"), setAppearanceCheckArea, 5), 100, 100);

        if (Client.isColorInRect(Color.decode("#0000ff"), setAppearanceCheckArea, 5)) {
            Logger.log("We are now at the appearance state");
        } else {
            Logger.log("Something went wrong, stopping script!");
            Script.stop();
        }

        Logger.log("Randomizing character");
        doRandomizeCharacter();

        Logger.log("Turning us into a woman");
        Client.tap(setWoman);
        Condition.sleep(1500, 1800);

        Logger.log("Setting our pronouns, fucking gay shit man...");
        doSetPronouns();

        Logger.log("Confirming!");
        Client.tap(confirmButton);
        Condition.sleep(1200, 1500);

        // SET TO TRUE HERE AS WE ARE DONE
        accountCreationDone = true;

        return false;
    }

    private char getRandomCharacter() {
        Random random = new Random();
        int randomType = random.nextInt(3); // 0 for a-z, 1 for A-Z, 2 for 0-9

        switch (randomType) {
            case 0: // a-z
                return (char) ('a' + random.nextInt(26));
            case 1: // A-Z
                return (char) ('A' + random.nextInt(26));
            case 2: // 0-9
                return (char) ('0' + random.nextInt(10));
            default: // Shouldn't reach here
                throw new IllegalStateException("Unexpected random type: " + randomType);
        }
    }

    private Rectangle getRandomRectangle() {
        Random random = new Random();
        int choice = random.nextInt(3); // Random number: 0, 1, or 2

        switch (choice) {
            case 0:
                return randomName1;
            case 1:
                return randomName2;
            case 2:
                return randomName3;
            default:
                throw new IllegalStateException("Unexpected random choice: " + choice);
        }
    }

    private void doRandomizeCharacter() {
        // List of pairs for left and right rectangles
        Rectangle[][] rectanglePairs = {
                {headLeft, headRight}, {jawLeft, jawRight},
                {torsoLeft, torsoRight}, {armsLeft, armsRight},
                {handsLeft, handsRight}, {legsLeft, legsRight},
                {feetLeft, feetRight}, {hairColorLeft, hairColorRight},
                {torsoColorLeft, torsoColorRight}, {legsColorLeft, legsColorRight},
                {feetColorLeft, feetColorRight}, {skinColorLeft, skinColorRight}
        };

        // Convert array to list and shuffle for random order
        java.util.List<Rectangle[]> shuffledPairs = new ArrayList<>();
        Collections.addAll(shuffledPairs, rectanglePairs);
        Collections.shuffle(shuffledPairs);

        Random random = new Random();

        // Process each pair in a randomized order
        for (Rectangle[] pair : shuffledPairs) {
            // Randomly pick either the left or the right rectangle
            Rectangle selectedRectangle = pair[random.nextInt(2)];

            // Determine a random number of taps (between 2 and 6)
            int tapCount = 2 + random.nextInt(5); // Random number between 2 and 6

            // Tap the selected rectangle the determined number of times
            for (int i = 0; i < tapCount; i++) {
                Client.tap(selectedRectangle);
                Condition.sleep(250, 400); // Random sleep between taps
            }
        }
    }

    private void doSetPronouns() {
        Client.tap(pronounsDropDown);
        Condition.sleep(900, 1300);
        // Define the pronoun rectangles
        Rectangle[] pronounOptions = {pronounsSheHer, pronounsTheyThem};

        // Randomly select one of the rectangles
        Random random = new Random();
        Rectangle selectedPronoun = pronounOptions[random.nextInt(pronounOptions.length)];

        Client.tap(selectedPronoun);
    }
}
