package JavaServer;

public class Main {

    public static void main(String[] args) {

        if(args[0].equalsIgnoreCase("-m")) {
            if(args[1].equalsIgnoreCase("gui")) {
                new Thread() {
                    @Override
                    public void run() {
                        javafx.application.Application.launch(GUIController.class);
                    }
                }.start();
            } else if(args[1].equalsIgnoreCase("console")) {
                KeyboardController key = new KeyboardController();
                key.start();
            }
        }
    }
}
