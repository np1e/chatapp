package JavaServer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class ServerMain extends Application {
    private Server server;
    private TextField port;
    private TextArea logs;
    private TextField commands;
    private ListView<User> userList;

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();
        HBox center = new HBox();
        root.setCenter(center);
        Scene scene =  new Scene(root, 800, 600);

        logs = new TextArea();
        logs.setEditable(false);
        logs.setMouseTransparent(true);
        logs.setFocusTraversable(false);
        logs.setScrollTop(0);

        port = new TextField();
        commands = new TextField();
        Button start = new Button("Start Server");
        Button stop = new Button("Stop Server");
        server = new Server();

        userList = new ListView<User>(server.getActiveUsers());

        server.logsProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println(newValue);
                logs.appendText(newValue + "\n");
            }
        });



        commands.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {
                    server.doCommand(commands.getText());
                }
            }
        });

        start.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                server.start(port.getText());
            }
        });

        stop.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                server.stop();
            }
        });

        getIps();
        VBox sideBar = new VBox();
        VBox rightBar = new VBox();
        rightBar.getChildren().addAll(logs, commands);
        sideBar.getChildren().addAll(start, stop, port, userList);
        VBox.setVgrow(logs, Priority.ALWAYS);
        center.getChildren().addAll(sideBar, rightBar);


        primaryStage.setScene(scene);
        primaryStage.setTitle("Server");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

    }

    public void getIps() {
        Enumeration e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
                InetAddress i = (InetAddress) ee.nextElement();
                System.out.println(i.getHostAddress());
            }
        }
    }
}
