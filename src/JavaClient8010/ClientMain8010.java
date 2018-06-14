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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.InetAddress;

public class ClientMain8010 extends Application {
    private Client client;

    @Override
    public void start(Stage primaryStage) throws Exception {

        client = new Client();

        BorderPane root = new BorderPane();
        VBox center = new VBox();
        root.setCenter(center);
        Scene scene =  new Scene(root, 400, 100);

        // Send message
        Text title = new Text("Dieser Client hört auf Port 8010 und sendet an Port 9010");
        TextField message = new TextField();
        message.setPromptText("Deine Nachricht");
        Button submitSend = new Button("Absenden");
        VBox sendmessage = new VBox();
        sendmessage.getChildren().addAll(title, message, submitSend);

        center.getChildren().addAll(sendmessage);

        submitSend.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    client.clientSend(message.getText(), "localhost", 9010);
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
