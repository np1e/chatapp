package JavaServer;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    boolean GUI;
    private ObservableList<String> logs;

    public Logger(String mode) {
        this.logs = logs;
        if (mode.equalsIgnoreCase("gui")){
            System.out.println("gui");
            GUI = true;
        } else if (mode.equalsIgnoreCase("console")) {
            GUI = false;
        }
    }

    public Logger(String mode, ObservableList logs) {
        this(mode);
        this.logs = logs;
    }

    public void log(String log) {
        if (GUI) {
            //Platform.runLater(() -> {
            this.logs.add(getTimestamp() + log);
            //});
        }
        System.out.println(getTimestamp() + log);

    }

    public void logWarning(String warning) {
        log(getTimestamp() + "warning: " + warning);
    }

    private String getTimestamp() {
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyy");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss.SSS");
        Date now = new Date();
        String timeStamp = date.format(now) + "\n\t[" + time.format(now) + "]" ;

        return timeStamp;
    }


}
