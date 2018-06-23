package JavaClient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
    private static String portUDP;
    private static String portTCP;
    private Server server;
    private ObservableList<User> activeusers;
    private ObservableList<Message> activechat;

    public static void main (String[] args) {
        portUDP = args[0];
        portTCP = args[1];
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Platform.setImplicitExit(false);

        activeusers = FXCollections.observableArrayList();
        activechat = FXCollections.observableArrayList();
        client = new Client(portUDP, portTCP, activeusers, activechat);
        server = new Server(portTCP, activeusers);

        final Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                server.start();
            }
        });
        serverThread.start();

        // MainScene
        VBox leftBox = new VBox();
        ListView clientListView = new ListView(activeusers);
        VBox.setVgrow(clientListView, Priority.ALWAYS);
        Label username = new Label("");
        Button logout = new Button("Logout");
        leftBox.getChildren().addAll(clientListView, username, logout);


        client.getUsername().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                username.setText(newValue);
            }
        });
        clientListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    client.sendChatRequest(clientListView.getSelectionModel().getSelectedItem().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        VBox rightBox = new VBox();
        ListView activeChat = new ListView(activechat);
        activechat.addListener(new ListChangeListener<Message>() {
            @Override
            public void onChanged(Change<? extends Message> c) {
                System.out.println("changed!");
            }
        });
        VBox.setVgrow(activeChat, Priority.ALWAYS);

        HBox sendMessageBox = new HBox();
        TextField messageField = new TextField();
        Button sendButton = new Button("Send");

        HBox.setHgrow(messageField, Priority.ALWAYS);
        sendMessageBox.getChildren().addAll(messageField, sendButton);

        rightBox.getChildren().addAll(activeChat, sendMessageBox);

        BorderPane root = new BorderPane();
        root.setLeft(leftBox);
        root.setCenter(rightBox);

        Scene mainScene = new Scene(root, 300, 300);


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

        logout.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    System.out.println("logout requested");
                    client.logout();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                primaryStage.setScene(loginScene);
            }
        });

        primaryStage.setTitle(portUDP);
        primaryStage.setScene(loginScene);
        primaryStage.show();
        primaryStage.setOnHidden(event -> {
            try {
                client.exit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
