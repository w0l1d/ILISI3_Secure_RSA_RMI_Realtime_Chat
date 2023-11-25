package org.ilisi.secure_rmi_chat.client;

import javafx.geometry.Pos;
import javafx.scene.control.Label;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * This class is used to store the conversation between the client and the server
 *
 */
public class ChatConversation {
    private final User user;
    private final List<Message> messages;

    public ChatConversation(User user) {
        this.user = user;
        this.messages = new ArrayList<>();
    }

    public User getUser() {
        return user;
    }

    public Message addMessage(String message, boolean isSent) {
        Message e = new Message(message, isSent, Instant.now());
        messages.add(e);
        return e;
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }


    public static record Message(String message,
                                 boolean isSent,
                                 Instant timestamp) {
        //change style (bg color) and alignement of label depending on the sender
        public Label toLabel(double parentWidth ) {
            Label label = toLabel();
            label.setMaxWidth(0.95 * parentWidth);
            return label;
        }

        public Label toLabel() {
            Label label = new Label(message);
            // mak ea more clear colors
            label.setStyle("-fx-background-color: " + (isSent ? "#d4e6f1" : "#ecf0f1") + ";" +
                    " -fx-padding: 5px;" +
                    " -fx-background-radius: 5px;" +
                    " -fx-border-radius: 5px;" +
                    " -fx-border-color: #d9d9d9;" +
                    " -fx-border-width: 1px;" +
                    " -fx-font-size: 14px;" +
                    " -fx-font-family: 'Segoe UI';" +
                    " -fx-text-fill: #333333;");
            label.setMinWidth(200);
            label.setWrapText(true);
            //max width is set to 80% of the parent
            label.setAlignment(isSent ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
            return label;
        }

    }

}
