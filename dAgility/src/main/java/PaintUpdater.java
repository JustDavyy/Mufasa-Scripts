import agi_sdk.interfaces.iAgiPaint;

import static helpers.Interfaces.Paint;
import agi_sdk.helpers.PaintType.*;
import static agi_sdk.helpers.PaintType.;

public class PaintUpdater implements iAgiPaint {
    @Override
    public void setStatistic(String value) {
        Paint.setStatistic(value);
    }

    @Override
    public void setStatus(String value) {
        Paint.setStatus(value);
    }

    @Override
    public void updatePaintBox(PaintType paintType, int value) {
        Paint.updateBox(paintType.getValue(), value);
    }
}
