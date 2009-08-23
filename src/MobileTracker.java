import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class MobileTracker
    extends MIDlet
    implements CommandListener {

    private Form form;
    private String lac;
    private String cellid;

    /**
     * Time interval between location updates, in ms.
     */
    private int updateTimeout = 5000;

    /**
     * Time interval between progress bar state updates, in ms.
     */
    private int progressTimeout = 1000;

    private Timer updateTimer = new Timer();
    private Timer progressTimer = new Timer();


    private StringItem timeoutItem = new StringItem("Update timeout: ", null);
    private StringItem locationItem = new StringItem("Location: ", null);
    private Gauge progressGauge = new Gauge("Time until next update:", false, 100, 0);

    /**
     * Create a form with elements.
     */
    public MobileTracker() {
        setupTimers();
        form = new Form("Mobile Tracker");
        form.append(timeoutItem);
        form.append(progressGauge);
        form.append(locationItem);

        timeoutItem.setText(String.valueOf(updateTimeout / 1000) + " seconds");

        timeoutItem.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        progressGauge.setLayout(Item.LAYOUT_NEWLINE_AFTER);
        locationItem.setLayout(Item.LAYOUT_NEWLINE_AFTER);

        form.addCommand(new Command("Exit", Command.EXIT, 0));
        form.setCommandListener(this);
    }

    /**
     * Update location, refresh form and log.
     */
    private TimerTask updateTask = new TimerTask() {
            public void run() {
                logLocation();
                updateLocation();
                updateForm();
            }
        };

    /**
     * Increment progressGauge or snap it to zero if maximum reached.
     */
    private TimerTask progressTask = new TimerTask() {
            public void run() {
                if (progressGauge.getValue() != progressGauge.getMaxValue())
                    {
                        float factor = new Integer(progressTimeout).floatValue() / updateTimeout;
                        progressGauge.setValue(progressGauge.getValue() +
                                               new Float(factor * progressGauge.getMaxValue()).intValue());
                    }
                else
                    progressGauge.setValue(0);
            }
        };

    private void updateLocation() {
        cellid = System.getProperty("com.sonyericsson.net.cellid");
        lac = System.getProperty("com.sonyericsson.net.lac");
    }

    private String makeLocationMark(String cellid, String lac) {
        return cellid + "@" + lac;
    }

    private void updateForm() {
        locationItem.setText(makeLocationMark(cellid, lac));
    }

    private void logLocation() {
    }

    /**
     * Schedule location update and progress bar update tasks.
     */
    private void setupTimers() {
        updateTimer.schedule(updateTask, 0, updateTimeout);
        progressTimer.schedule(progressTask, 0, progressTimeout);
    }

    public final void startApp() {
        Display.getDisplay(this).setCurrent(form);
    }

    public final void pauseApp() {
    }

    public final void destroyApp(boolean unconditional) {
    }

    public final void commandAction(Command c, Displayable s) {
        notifyDestroyed();
    }
}
