package org.ilisi.secure_rmi_chat.server;

import org.ilisi.secure_rmi_chat.client.IChatClient;
import org.ilisi.secure_rmi_chat.client.User;

public record Chatter(
        // user
        User user,
        // client stub
        IChatClient client) {
}
