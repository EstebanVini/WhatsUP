package com.example.cliente;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

import java.io.*;
import java.net.Socket;

public class HelloController {
    public TextField username;
    public TextField password;
    public Button btnlogin;
    public Button btnregistro;
    public TextField password1;
    @FXML
    VBox vbox;
    @FXML
    TextArea textArea;
    @FXML
    Button button;

    String temp;

    Socket socket;
    DataOutputStream salida;
    DataInputStream entrada;

    @FXML
    void MandarMensaje() throws Exception {


        try{
            String mensaje = textArea.getText();


            Platform.runLater(() -> {
                Label label = new Label(mensaje);
                vbox.getChildren().add(label);
            });


            salida.writeUTF(mensaje);
            textArea.setText("");
        } catch (IOException error) {
           System.out.println(error);
        }

    }

    public void initialize() {


        Thread socketThread = new Thread(() -> {
            try {
                socket = new Socket("127.0.0.1", 12345);
                entrada = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                salida = new DataOutputStream(socket.getOutputStream());

                try {
                    while (true) {
                        String mensajeRecibido = entrada.readUTF();

                        Platform.runLater(() -> {
                            Label label = new Label(mensajeRecibido);
                            label.setStyle("-fx-font-weight: bold;");
                            vbox.getChildren().add(label);
                        });
                    }
                } catch (IOException error) {
                    System.out.println("Error al recibir mensaje: " + error.getMessage());
                }
            } catch (IOException error) {
                System.out.println(error);
            }
        });
        socketThread.setDaemon(true);
        socketThread.start();



    }


    public void goNext(MouseEvent mouseEvent) {
    }

    public void goNext2(MouseEvent mouseEvent) {
    }
}