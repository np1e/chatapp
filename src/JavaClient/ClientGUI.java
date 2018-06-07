package JavaClient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientGUI extends Application{

    public static void main (String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox leftBox = new VBox();
        ListView clientListView = new ListView();
        VBox.setVgrow(clientListView, Priority.ALWAYS);
        leftBox.getChildren().add(clientListView);

        VBox rightBox = new VBox();
        TextArea messageArea = new TextArea();
        messageArea.setEditable(false);
        VBox.setVgrow(messageArea, Priority.ALWAYS);

        HBox sendMessageBox = new HBox();
        TextField messageField = new TextField();
        Button sendButton = new Button("Send");
        HBox.setHgrow(messageField, Priority.ALWAYS);
        sendMessageBox.getChildren().addAll(messageField, sendButton);

        rightBox.getChildren().addAll(messageArea, sendMessageBox);


        BorderPane root = new BorderPane();
        root.setLeft(leftBox);
        root.setCenter(rightBox);




        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("JavaClient");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
