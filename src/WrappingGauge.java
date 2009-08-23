package mobiletracker;

import javax.microedition.lcdui.*;

/**
 * This class implements a gauge which sets current value modulo
 * maximum.
 */
public class WrappingGauge
    extends Gauge {

    public WrappingGauge(String label, boolean interactive, int maxValue, int initialValue) {
        super(label, interactive, maxValue, initialValue);
    }

    public void setValue(int value) {
        super.setValue((value > getMaxValue()) ? (value % getMaxValue()) : value);
    }
}
