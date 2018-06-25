package JavaServer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUIController extends Application {
    private ServerController controller;
    private Logger logger;
    private TextField port;
    private ListView<String> logsView;
    private ObservableList<String> logs;
    private TextField commands;
    private ListView<User> userList;

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();
        HBox center = new HBox();
        root.setCenter(center);
        Scene scene =  new Scene(root, 800, 600);

        logs = FXCollections.observableArrayList();
        logger = new Logger("gui", logs);
        controller = new ServerController(logger);
        Server server = controller.getServer();

        logsView = new ListView(logs);
        logsView.setEditable(false);
        logsView.setMouseTransparent(true);
        logsView.setFocusTraversable(false);

        port = new TextField();
        commands = new TextField();
        Button start = new Button("Start Server");
        Button stop = new Button("Stop Server");

        userList = new ListView<User>(controller.getActiveUsers());




        commands.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {
                    controller.doCommand(commands.getText());
                }
            }
        });

        start.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                controller.startServer(port.getText());
            }
        });

        stop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                controller.stopServer();
            }
        });

        VBox sideBar = new VBox();
        VBox rightBar = new VBox();
        rightBar.getChildren().addAll(logsView, commands);
        sideBar.getChildren().addAll(start, stop, port, userList);
        VBox.setVgrow(logsView, Priority.ALWAYS);
        center.getChildren().addAll(sideBar, rightBar);


        primaryStage.setScene(scene);
        primaryStage.setTitle("Server");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }


}
