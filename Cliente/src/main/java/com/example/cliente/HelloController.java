package com.example.cliente;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HelloController {

    @FXML
    public TextField username;
    @FXML
    public TextField password;
    @FXML
    public Button btnlogin;
    @FXML
    public TextField phone;
    String Mayusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789¿'¡?=)(/&%$#!";
    String Minusculas = "abcdefghijklmnopqrstuvwxyz 0123456789¿'¡?=)(/&%$#!";
    cifrado obj = new cifrado(Mayusculas, Minusculas);

    @FXML
    VBox vbox;
    @FXML
    TextArea textArea;
    @FXML
    Button button;

    String temp;
    String usernameString = "";
    String passwordString = "";
    String telefonoString = "";
    Boolean SocketConnected = false;
    Socket socket;
    DataOutputStream salida;
    DataInputStream entrada;

    List<DataUsers> users = new ArrayList<>();

    public void MandarMensaje(MouseEvent mouseEvent) {
    }

    public  class DataUsers{
        private String username;
        private String phone;

        public DataUsers(String username, String phone) {
            this.username = username;
            this.phone = phone;
        }

        public String getUsername() {
            return username;
        }

        public String getPhone() {
            return phone;
        }
    }
    List<DataMensajes> HistorialMensajes = new ArrayList<>();

    public class DataMensajes{
        private String mensaje;
        private String username;

        public DataMensajes(String mensaje, String username, String message) {
            this.mensaje = mensaje;
            this.username = username;
        }

        public String getMensaje() {
            return mensaje;
        }

        public String getUsername() {
            return username;
        }
    }

    public void initialize() {


        Thread socketThread = new Thread(() -> {
            try {
                socket = new Socket("127.0.0.1", 12345);
                entrada = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                salida = new DataOutputStream(socket.getOutputStream());
                SocketConnected = true;
            } catch (IOException error) {
                System.out.println(error);
            }
        });
        socketThread.setDaemon(true);
        socketThread.start();
    }

    @FXML
    public void login(MouseEvent mouseEvent) throws IOException {
        usernameString = username.getText();
        passwordString = password.getText();
        telefonoString = phone.getText();
        if (usernameString.isEmpty() || passwordString.isEmpty() || telefonoString.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Por favor llene todos los campos");
            alert.showAndWait();
        } else {
            if (SocketConnected) {
                salida.writeUTF("login");
                salida.writeUTF(usernameString);
                salida.writeUTF(passwordString);
                salida.writeUTF(telefonoString);
            }
        }
        
        Stage stage = (Stage) btnlogin.getScene().getWindow();
        Object root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
    }

    @FXML
    void sendMessage(MouseEvent event) {
        try {
            String message = textArea.getText();
            DataUsers reciver = new DataUsers(usernameString, telefonoString); 

            String sender = getSenderName();

            DataMensajes sentMessage = new DataMensajes(sender, reciver.phone, message);
            addToMessageHistory(sentMessage);

            displayMessage(sender, message);

            String messageToSend = createMessageToSend(sender, reciver.phone, message);
            sendMessageToRecipient(messageToSend);

            clearMessageTextField();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getSenderName() {
        Stage stage = (Stage) textArea.getScene().getWindow();
        return stage.getTitle();
    }

    private void addToMessageHistory(DataMensajes message) {
        HistorialMensajes.add(message);
    }

    private void displayMessage(String sender, String message) {
        Platform.runLater(() -> {
            Label label = new Label(sender + ": " + message);
            vbox.getChildren().add(label);
        });
    }

    private String createMessageToSend(String sender, String recipientPhone, String message) {
        return "SENDER=" + sender + ",RECEIVER=" + recipientPhone + ",MESSAGE=" + message;
    }

    private void sendMessageToRecipient(String messageToSend) throws IOException {
        System.out.println("messageToSend: " + messageToSend);
        salida.writeUTF(messageToSend);
        salida.flush();
    }

    private void clearMessageTextField() {
        textArea.setText("");
    }

    

    public void goNext2(MouseEvent mouseEvent) {
    }
}