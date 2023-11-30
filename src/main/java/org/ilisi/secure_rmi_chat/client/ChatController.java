package org.ilisi.secure_rmi_chat.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.ilisi.secure_rmi_chat.server.IChatServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

public class ChatController implements ChatControllable {
    private final IChatServer chatServer;
    private final ObservableList<Label> messages;
    public TextArea txtMsg;
    public Button btnSend;
    public ListView<User> lstActiveUsers;
    public ListView<Label> lstConv;
    public Label lblConvTitle;
    public Label lblActiveUsers;
    public Label lblChatTitle;
    private List<ChatConversation> conversations;
    private ChatClient chatClient;
    private User user;
    private User selectedUser;
    private MessageCryptoClient cryptoClient;


    public ChatController() throws MalformedURLException, NotBoundException, RemoteException {
        chatServer = (IChatServer) Naming.lookup("rmi://localhost:1099/GroupChatService");
        conversations = new Vector<>();
        messages = FXCollections.observableList(new Vector<>());
        Platform.runLater(() -> {
            lstConv.setItems(messages);
            // make list unselectable
            lstConv.setMouseTransparent(true);
            lstConv.setFocusTraversable(false);
        });
    }

    private static void alertError(String errorTitle, String errorHeader, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(errorTitle);
        alert.setHeaderText(errorHeader);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    public void onStartConversation(ActionEvent event) {
        System.out.println("onStartConversation");
        String username = txtMsg.getText().trim();
        if (username.isEmpty()) {
            alertError(null, "Error", "Please enter a username");
            return;
        }

        cryptoClient = new MessageCryptoClient();

        try {
            user = new User(username, cryptoClient.getPublicKeyAsString());
            chatClient = new ChatClient(user.getId(), this);
            chatServer.registerUser(user);
        } catch (RemoteException e) {
            alertError("Connection problem", "The server seems to be unavailable\nPlease try later", e.getMessage());
            e.printStackTrace();
            return;
        }

        lblChatTitle.setText("Logged in as [" + username + "]");
        lblConvTitle.setText("Select a user to start a conversation");
        lblConvTitle.setWrapText(true);


        btnSend.setText("Send");
        txtMsg.setPromptText("Type your message here");
        btnSend.setOnAction(this::onSendMessage);

        txtMsg.clear();
        txtMsg.setPrefRowCount(3);

    }

    private void onSendMessage(ActionEvent actionEvent) {
        System.out.println("onSendMessage");
        if (selectedUser == null) {
            alertError("Error", null, "Please select a user to send the message to");
            return;
        }
        String message = txtMsg.getText().trim();
        if (message.isEmpty()) {
            alertError("Error", null, "Please enter a message");
            return;
        }

        try {
            //encrypt message with selected user's public key
            String encyptedMessage = cryptoClient.encryptMessage(message, selectedUser.getPublicKey());
            chatServer.sendMessage(encyptedMessage, selectedUser.getId(), user.getId());
            messages.add(conversations.stream()
                    .filter(chat -> chat.getUser().equals(selectedUser))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("SEND_MSG [%s]No conversation with user %s%n conversations: %s",
                            user.getUsername(), selectedUser.getUsername(), conversations.stream().map(chat -> chat.getUser().getUsername()).toList())))
                    .addMessage(message, true).toLabel(lstConv.getWidth()));
        } catch (RemoteException e) {
            alertError("Connection problem", "The server seems to be unavailable\nPlease try later", e.getMessage());
            e.printStackTrace();
            return;
        }

        txtMsg.clear();
    }

    @Override
    public void receiveMessage(String cryptedMessage, User sender) {
        String message = cryptoClient.decryptMessage(cryptedMessage);
        System.out.println("Decrypted message received : " + message);
        ChatConversation.Message message1 = conversations.stream()
                .filter(chat -> chat.getUser().equals(sender))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("[%s]No conversation with user %s%n conversations: %s",
                        user.getId(), sender.getId(), conversations.stream().map(chat -> chat.getUser().getId()).toList())))
                .addMessage(message, false);
        if (sender.equals(selectedUser)) {
            Platform.runLater(() -> {
                messages.add(message1.toLabel(lstConv.getWidth()));
                lstConv.refresh();
            });
        } else {
            Platform.runLater(() -> {
                // highlight the user in the list of active users with unread messages
                lstActiveUsers.getItems().stream()
                        .filter(user -> user.equals(sender))
                        .findFirst()
                        .ifPresent(user -> {
                            if (!user.getUsername().endsWith(" (new messages)")) {
                                user.setUsername(user.getUsername() + " (new messages)");
                                lstActiveUsers.refresh();
                            }
                        });
            });
        }
    }


    @Override
    public void updateActiveUsersList(List<User> activeUsers) {
        Platform.runLater(() -> {
            conversations.clear();
            conversations.addAll(activeUsers.stream().map(ChatConversation::new).toList());
            lstActiveUsers.getItems().clear();
            lstActiveUsers.getItems().addAll(activeUsers);
            lstActiveUsers.refresh();
            lblActiveUsers.setText("Connected users : " + activeUsers.size());
        });

    }


    @Override
    public void addActiveUser(User user) {
        Platform.runLater(() -> {
            if (lstActiveUsers.getItems().contains(user)) {
                return;
            }
            lstActiveUsers.getItems().add(user);
            if (conversations.stream().noneMatch(chat -> chat.getUser().equals(user))) {
                conversations.add(new ChatConversation(user));
            }
            lstActiveUsers.refresh();
            lblActiveUsers.setText("Connected users : " + lstActiveUsers.getItems().size());
        });
    }

    @Override
    public void removeActiveUser(User user) {
        Platform.runLater(() -> {
            if (selectedUser != null && selectedUser.equals(user)) {
                selectedUser = null;
                lblConvTitle.setText("Select a user to start a conversation");
                messages.clear();
                lstConv.refresh();
            }
            lstActiveUsers.getItems().remove(user);
            lstActiveUsers.refresh();
            lblActiveUsers.setText("Connected users : " + lstActiveUsers.getItems().size());
        });
    }

    @Override
    public void leaveChat() {
        try {
            chatServer.leaveChat(user);
            Platform.exit();
            System.exit(0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onUserSelected(MouseEvent mouseEvent) {
        System.out.println("onUserSelected");
        // if the user double clicked, do nothing
        if (mouseEvent.getClickCount() != 1) {
            return;
        }
        // if no user is selected, do nothing
        if (lstActiveUsers.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        // if the user is already selected, do nothing
        if (lstActiveUsers.getSelectionModel().getSelectedItem().equals(selectedUser)) {
            return;
        }
        // get the selected user
        selectedUser = lstActiveUsers.getSelectionModel().getSelectedItem();
        // set the style of the conversation title
        lblConvTitle.setStyle("-fx-font-style: normal;" +
                " -fx-font-size: 14px;  -fx-text-fill: #5e5e5e;" +
                " -fx-background-color: #9bff84;");
        // set the title of the conversation
        lblConvTitle.setText("Conversation with " + selectedUser.getUsername());
        // clear the messages list
        messages.clear();
        // add the messages of the selected user to the messages list
        conversations.stream()
                .filter(chat -> chat.getUser().equals(selectedUser))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("SELECT_CONV [%s]No conversation with user %s%n conversations: %s",
                        user.getUsername(), selectedUser.getUsername(), conversations.stream().map(chat -> chat.getUser().getUsername()).toList())))
                .getMessages()
                .forEach(message -> {
                    messages.add(message.toLabel(lstConv.getWidth()));
                });
        // refresh the messages list
        lstConv.refresh();
        // remove the highlight from the user in the list of active users
        lstActiveUsers.getItems().stream()
                .filter(user -> user.equals(selectedUser))
                .findFirst()
                .ifPresent(user -> {
                    // remove the highlight from the user in the list of active users
                    if (user.getUsername().endsWith(" (new messages)")) {
                        user.setUsername(user.getUsername().replace(" (new messages)", ""));
                        lstActiveUsers.refresh();
                    }
                });
    }
}