package org.ilisi.secure_rmi_chat.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 450);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        // close chat when window is closed
        stage.setOnCloseRequest(event -> {
            // close application
            if (ChatClient.getInstance() != null) {
                try {
                    // leave chat
                    ChatClient.getInstance().leaveChat();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}