package JavaClientNetwork;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        goalIP.setPromptText("Ziel IPv4-Adresse");
        TextField message = new TextField();
        message.setPromptText("Nachricht");
        Button submitSend = new Button("Absenden");
        VBox sendmessage = new VBox();
        sendmessage.getChildren().addAll(goalIP, message, submitSend);

        center.getChildren().addAll(sendmessage);

        submitSend.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    client.sendmessage(message.getText(), goalIP.getText(), 9010);
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
