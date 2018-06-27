package JavaServer;
import java.util.Scanner;

public class KeyboardController {

    private ServerController controller;
    private Logger logger;

    public KeyboardController() {
        logger = new Logger("console");
        controller = new ServerController(logger);

    }

    public void start() {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Controller started\nType \"help\" for list of available commands");
        String input = keyboard.nextLine();
        while(controller.isServerRunning()) {
            // process input
        }
    }
}
