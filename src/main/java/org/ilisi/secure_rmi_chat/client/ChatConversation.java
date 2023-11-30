package org.ilisi.secure_rmi_chat.client;

import javafx.geometry.Pos;
import javafx.scene.control.Label;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * This class is used to store the conversation between current user and another user (user)
 * It contains the list of messages sent and received
 * It also contains the user with whom the conversation is held
 */
public class ChatConversation {
    // user with whom the conversation is held
    private final User user;
    // list of messages sent and received
    private final List<Message> messages;

    public ChatConversation(User user) {
        this.user = user;
        this.messages = new ArrayList<>();
    }

    public User getUser() {
        return user;
    }

    // add a message to the conversation
    public Message addMessage(String message, boolean isSent) {
        Message e = new Message(message, isSent, Instant.now());
        messages.add(e);
        return e;
    }

    // get all messages
    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }


    // a message is composed of the message content, the sender and the timestamp
    // record is used to create a record class (immutable class)
    public record Message(
            // message content
            String message,
            // true if the message is sent by the current user, false if it is received
            boolean isSent,
            // timestamp of message reception or sending
            Instant timestamp) {

        // create a label with the message content and set max width to 95% of parent width
        public Label toLabel(double parentWidth) {
            Label label = toLabel();
            // set max width to 95% of parent width
            label.setMaxWidth(0.95 * parentWidth);
            return label;
        }

        public Label toLabel() {
            // create a label with the message content
            Label label = new Label(message);
            // set style  of label depending on the sender (sent or received)
            label.setStyle("-fx-background-color: " + (isSent ? "#d4e6f1" : "#ecf0f1") + ";" +
                    " -fx-padding: 5px;" +
                    " -fx-background-radius: 5px;" +
                    " -fx-border-radius: 5px;" +
                    " -fx-border-color: #d9d9d9;" +
                    " -fx-border-width: 1px;" +
                    " -fx-font-size: 14px;" +
                    " -fx-font-family: 'Segoe UI';" +
                    " -fx-text-fill: #333333;");
            // set min width to 200px
            label.setMinWidth(200);
            label.setWrapText(true);
            // set alignment of label depending on the sender (sent or received)
            label.setAlignment(isSent ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
            return label;
        }

    }

}
