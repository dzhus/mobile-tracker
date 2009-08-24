package mobiletracker;

import java.util.*;
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class MobileTracker
    extends MIDlet
    implements CommandListener {

    /**
     * Current Location Area Code.
     */
    private String lac;

    /**
     * Current CellID.
     */
    private String cellid;

    /**
     * Timestamp of last update.
     */
    private Date timestamp = new Date();

    /**
     * Time interval between location updates, in ms.
     */
    private int updateTimeout = 300000;

    /**
     * Time interval between progress bar state updates, in ms.
     */
    private int progressTimeout = 1000;

    private Timer updateTimer = new Timer();
    private Timer progressTimer = new Timer();

    private Form form;
    private StringItem timeoutItem = new StringItem("Update timeout: ", null);
    private StringItem locationItem = new StringItem("Location: ", null);
    private WrappingGauge progressGauge = new WrappingGauge("Time until next update:", false, 100, 0);

    private FileConnection fileconn;
    private DataOutputStream filestream;

    /**
     * Create a form with elements.
     */
    public MobileTracker() {
        makeForm();
        setupLogfile();
        setupTimers();
    }

    /**
     * Show exception message in alert box and go back to form.
     */
    private void handleException(Exception e, String title) {
        Alert a = new Alert(title, e.getMessage(), null, AlertType.ERROR);
        a.setTimeout(Alert.FOREVER);
        Display.getDisplay(this).setCurrent(a, form);
    }
        
    /**
     * Update location, refresh form and log.
     */
    private TimerTask updateTask = new TimerTask() {
            public void run() {
                updateLocation();
                updateForm();
                logLocation();
            }
        };

    /**
     * Increment progressGauge or snap it to zero if maximum reached.
     */
    private TimerTask progressTask = new TimerTask() {
            public void run() {
                float factor = new Integer(progressTimeout).floatValue() / updateTimeout;
                progressGauge.setValue(progressGauge.getValue() +
                                       new Float(factor * progressGauge.getMaxValue()).intValue());
            }
        };

    private void updateLocation() {
        cellid = System.getProperty("com.sonyericsson.net.cellid");
        lac = System.getProperty("com.sonyericsson.net.lac");
        timestamp.setTime(System.currentTimeMillis());
    }

    private void updateForm() {
        locationItem.setText(cellid + "@" + lac);
    }

    /**
     * Return a String for writing current location to file.
     */
    public String currentLocationLine() {
        return new Long(timestamp.getTime()).toString() + ","
            + cellid + "," + lac + "\n";
    }

    /**
     * Make and display main form.
     */
    private void makeForm() {
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
     * Open FileConnection and DataOutputStream for track.txt log file.
     */
    private void setupLogfile() {
        try {
            fileconn = (FileConnection) Connector.open
                ("file:///e:/Other/track.txt");

            if (!fileconn.exists())
                fileconn.create();

            filestream = new DataOutputStream
                (fileconn.openOutputStream(fileconn.fileSize()));

            filestream.write
                (new String("# Logging started " +
                            timestamp.toString() + "\n").getBytes());
        }
        catch (IOException ioe) {
            handleException(ioe, "I/O error");
        }
    }

    private void logLocation() {
        try {
            filestream.write(currentLocationLine().getBytes());
        }
        catch (IOException ioe) {
            handleException(ioe, "I/O error");
        }
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
        try {
            filestream.close();
            fileconn.close();
        }
        catch (IOException ioe) {
            handleException(ioe, "I/O error");
        }
    }

    public final void commandAction(Command c, Displayable s) {
        notifyDestroyed();
    }
}
