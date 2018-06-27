package JavaClient;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientGUI extends Application{

    private Client client;

    public static void main (String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        client = new Client();

        // MainScene
        VBox leftBox = new VBox();
        ListView clientListView = new ListView(client.getUsers());
        VBox.setVgrow(clientListView, Priority.ALWAYS);
        leftBox.getChildren().add(clientListView);
        clientListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                client.sendChatRequest(clientListView.getSelectionModel().getSelectedItem().toString());
            }
        });

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

        Scene mainScene = new Scene(root, 800, 600);

        //LoginScene
        BorderPane loginRoot = new BorderPane();
        VBox center = new VBox();

        // Login
        TextField userNameLog = new TextField();
        TextField passWordLog = new PasswordField();
        Button submitLog = new Button("Login");
        submitLog.setOnAction(e-> {
            try {
                client.login(userNameLog.getText(), passWordLog.getText());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            primaryStage.setScene(mainScene);
        });
        VBox login = new VBox();
        login.getChildren().addAll(userNameLog, passWordLog, submitLog);

        // Reg
        TextField userNameReg = new TextField();
        TextField emailReg = new TextField();
        Button submitReg = new Button("Registrieren");
        submitReg.setOnAction(e->primaryStage.setScene(mainScene));
        VBox registration = new VBox();
        registration.getChildren().addAll(userNameReg, emailReg, submitReg);

        center.getChildren().addAll(login, registration);
        loginRoot.setCenter(center);

        Scene loginScene = new Scene(loginRoot, 200, 200);

        primaryStage.setTitle("JavaClient");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }
}
