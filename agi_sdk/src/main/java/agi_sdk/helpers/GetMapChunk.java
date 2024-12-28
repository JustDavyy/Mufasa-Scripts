package agi_sdk.helpers;

import helpers.utils.MapChunk;

import static helpers.Interfaces.*;

public class GetMapChunk {
    public static MapChunk get(Course course) {
        switch (course) {
            case GNOME:
                return new MapChunk(new String[]{"38-53", "39-53"}, "0", "1", "2");
            case AL_KHARID:
                return new MapChunk(new String[]{"51-49"}, "0", "1", "2", "3");
            case VARROCK:
                return new MapChunk(new String[]{"50-53"}, "0", "1", "3");
            case CANIFIS:
                return new MapChunk(new String[]{"54-54"}, "0", "2", "3");
            case FALADOR:
                return new MapChunk(new String[]{"47-52"}, "0", "3");
            case RELLEKKA:
                return new MapChunk(new String[]{"41-57"}, "0", "3");
            case ARDOUGNE:
                return new MapChunk(new String[]{"41-51"}, "0", "3");
            case DRAYNOR:
                return new MapChunk(new String[]{"48-51"}, "0", "3");
            case POLLNIVNEACH:
                return new MapChunk(new String[]{"52-46"}, "0", "1", "2");
            case SEERS:
            case SEERS_TELEPORT:
                return new MapChunk(new String[]{"42-54"}, "0", "2", "3");
            case ADVANCED_COLOSSAL_WYRM:
            case BASIC_COLOSSAL_WYRM:
                return new MapChunk(new String[]{"25-45"}, "0", "1", "2");
            default:
                Logger.log("This is a unknown course, no chunks to set up.");
                Script.stop();
        }
        return null;
    }
}
