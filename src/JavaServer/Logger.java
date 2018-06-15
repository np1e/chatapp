package JavaServer;

import javafx.beans.property.SimpleStringProperty;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    boolean GUI;
    SimpleStringProperty log;


    public Logger(String mode) {
        log = new SimpleStringProperty();
        if (mode.equalsIgnoreCase("gui")){
            System.out.println("gui");
            GUI = true;
        } else if (mode.equalsIgnoreCase("console")) {
            GUI = false;
        }
    }

    public void log(String log) {
        if (GUI){
            this.log.set(getTimestamp() + log);
        } else {
            System.out.println(getTimestamp() + log);
        }

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

    public String getLog() {
        return log.get();
    }

    public SimpleStringProperty logProperty() {
        return log;
    }
}
