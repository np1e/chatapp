package JavaClient;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class ClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();
        VBox center = new VBox();
        root.setCenter(center);
        Scene scene =  new Scene(root, 800, 600);

        // Login
        TextField userNameLog = new TextField();
        TextField passWordLog = new PasswordField();
        Button submitLog = new Button("Login");
        VBox login = new VBox();
        login.getChildren().addAll(userNameLog, passWordLog, submitLog);

        // Reg
        TextField userNameReg = new TextField();
        TextField emailReg = new TextField();
        Button submitReg = new Button("Registrieren");
        VBox registrieren = new VBox();
        registrieren.getChildren().addAll(userNameReg, emailReg, submitReg);

        center.getChildren().addAll(login, registrieren);

        /*
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
        */



        primaryStage.setScene(scene);
        primaryStage.setTitle("javaClient");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

    }
}
