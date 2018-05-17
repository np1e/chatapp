package JavaServer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class ServerMain extends Application {
    private Server server;
    private TextField port;
    private TextArea logs;

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
        Button start = new Button("Start");

        server = new Server();
        server.logsProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println(newValue);
                logs.appendText(newValue + "\n");
            }
        });

        start.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("started");
                server.start(port.getText());
            }
        });

        center.getChildren().addAll(start, port, logs);


        primaryStage.setScene(scene);
        primaryStage.setTitle("Server");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

    }
}
