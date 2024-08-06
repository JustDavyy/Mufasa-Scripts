package dAnglerFisher.Tasks;

import dAnglerFisher.Task;
import dAnglerFisher.dAnglerFisher;
import helpers.utils.ItemList;
import java.util.Random;

import static helpers.Interfaces.*;

public class AntiAFK extends Task {
    dAnglerFisher main;
    private long lastActivationTime = 0;  // Track the last activation time
    private Random random = new Random();

    public AntiAFK(dAnglerFisher main){
        super();
        super.name = "AntiAFK";
        this.main = main;
    }

    @Override
    public boolean activate() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastActivation = currentTime - lastActivationTime;
        int randomInterval = 10000 + random.nextInt(5000); // Randomize between 10-15 seconds

        if (timeSinceLastActivation >= randomInterval || timeSinceLastActivation >= 15000) {
            lastActivationTime = currentTime; // Update last activation time
            Game.antiAFK();
            GameTabs.openInventoryTab();
            dAnglerFisher.updatePaint();
            dAnglerFisher.updateStatLabel();
        }
        return false; // No activation if conditions are not met
    }

    @Override // the code to execute if criteria met
    public boolean execute() {
        // We don't do anything here
        return false;
    }
}
