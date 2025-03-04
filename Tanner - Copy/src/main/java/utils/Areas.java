package utils;

import helpers.utils.Area;

import helpers.utils.Tile;




public class Areas extends Task {

    
    public static Area GrandExhangeArea = new Area(
        new Tile(12712, 13566, 0), 
        new Tile(12596, 13730, 0)
    );

    public static Area CratingGuildArea = new Area(
        new Tile(11704, 12842, 0), 
        new Tile(11764, 12918, 0)
    );



    public boolean activate() {

        return false;
    }

    @Override
    public boolean execute() {
        
        return false;
    }
}
