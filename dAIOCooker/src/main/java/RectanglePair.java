import java.awt.*;

public class RectanglePair {
    private Rectangle bank;
    private Rectangle range;

    public RectanglePair(Rectangle bank, Rectangle range) {
        this.bank = bank;
        this.range = range;
    }

    public Rectangle getBank() {
        return bank;
    }

    public Rectangle getRange() {
        return range;
    }
}
