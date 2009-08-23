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

    private StringItem timeoutItem = new StringItem("Update timeout: ", null);
    private StringItem locationItem = new StringItem("Location: ", null);

    /**
     * TextBox which contains locations history over the last time.
     */
    private TextField historyBox = new TextField("History:", null, 1000, TextField.UNEDITABLE);

    private Timer timer = new Timer();

    /**
     * Create a form with elements.
     */
    public MobileTracker() {
        setupTimer();
        form = new Form("Mobile Tracker");
        form.append(timeoutItem);
        form.append(locationItem);
        
        form.append(historyBox);

        timeoutItem.setText(String.valueOf(updateTimeout / 1000) + " seconds");

        timeoutItem.setLayout(Item.LAYOUT_NEWLINE_AFTER);
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
        historyBox.insert(makeLocationMark(cellid, lac) + "\n", historyBox.size());
    }

    private void setupTimer() {
        timer.schedule(updateTask, 0, updateTimeout);
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
