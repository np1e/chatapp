package JavaClient8010;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.InetAddress;

public class ClientMain extends Application {
    private Client client;

    @Override
    public void start(Stage primaryStage) throws Exception {

        client = new Client();

        BorderPane root = new BorderPane();
        VBox center = new VBox();
        root.setCenter(center);
        Scene scene =  new Scene(root, 200, 200);

        // Send message
        TextField goalIP = new TextField();
        TextField message = new TextField();
        Button submitSend = new Button("Absenden");
        VBox sendmessage = new VBox();
        sendmessage.getChildren().addAll(goalIP, message, submitSend);

        center.getChildren().addAll(sendmessage);

        submitSend.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    client.sendmessage(message.getText(), "localhost", 8010);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        primaryStage.setScene(scene);
        primaryStage.setTitle("javaClient");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

    }
}
