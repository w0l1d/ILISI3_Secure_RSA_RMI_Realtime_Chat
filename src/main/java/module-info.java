module org.ilisi.secure_rmi_chat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.rmi;

    // Define dependencies on other modules
    requires java.base; // This is typically automatic and doesn't need to be specified explicitly
    requires java.xml.crypto; // If your module uses cryptography
    requires java.logging; // If your module uses logging

    opens org.ilisi.secure_rmi_chat.client;
    opens org.ilisi.secure_rmi_chat.server;
    exports org.ilisi.secure_rmi_chat.client;

}