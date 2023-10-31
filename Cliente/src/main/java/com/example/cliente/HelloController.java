package com.example.cliente;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


import java.io.*;
import java.net.Socket;

public class HelloController {
    public TextField username;
    public TextField password;
    public Button btnlogin;
    public Button btnregistro;
    public TextField phone;
    public CheckBox simetrico;
    public CheckBox asimetrico;
    public CheckBox sobredigital;
    public CheckBox plano;
    public CheckBox firmar;
    public TextField llavepublica;
    public TextField llavedescifrar;
    public TextField llaveprivada;
    public TextField claveDesplazamiento;
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

            if (plano.isSelected()) {
                try {
                    String mensaje = textArea.getText();


                    Platform.runLater(() -> {
                        Label label = new Label(mensaje);
                        vbox.getChildren().add(label);
                    });

                    salida.writeUTF(mensaje);
                    textArea.setText("");
                }catch (IOException error) {
                        System.out.println(error);
                    }

            } if (simetrico.isSelected()) {
            try {
                String mensaje = textArea.getText();
                String llave = claveDesplazamiento.getText();
                String mensajeCifrado = Cifrado.cifrar(mensaje, Integer.parseInt(llave));

                Platform.runLater(() -> {
                    Label label = new Label(mensajeCifrado);
                    vbox.getChildren().add(label);
                });

                salida.writeUTF(mensajeCifrado);
                textArea.setText("");
            } catch (IOException error) {
                System.out.println(error);
            }
        } else if (asimetrico.isSelected()) {
            try {
                String mensaje = textArea.getText();
                String llave = claveDesplazamiento.getText();
                String mensajeCifrado = Cifrado.cifrar(mensaje, Integer.parseInt(llave));
                String llavePublica = String.valueOf(Cifrado.inverso(Integer.parseInt(llave)));


                Platform.runLater(() -> {
                    Label label = new Label(mensajeCifrado);
                    vbox.getChildren().add(label);
                });

                salida.writeUTF(mensajeCifrado);
                textArea.setText("");

                // Muestra una alerta con la llave pública
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Llave Pública");
                alert.setHeaderText("Tu llave pública es:");
                alert.setContentText(llavePublica);
                alert.showAndWait();
            } catch (Exception error) {
                System.out.println(error);
            }
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

    @FXML
    void login() {
        String usernameText = username.getText();
        String passwordText = password.getText();
        String phoneText = phone.getText();

        // Valida los campos de nombre de usuario, contraseña y teléfono
        if (usernameText.isEmpty() || passwordText.isEmpty() || phoneText.isEmpty()) {
            // Muestra un mensaje de error al usuario
            System.out.println("Error: Debe llenar todos los campos");

            return;
        }

        // Crea un objeto User con los datos ingresados
        User user = new User(usernameText, passwordText, phoneText);

        // Envía el objeto User al servidor
        try {
            // inicializa la conexión con el servidor usando la función initializeConnection()
            initialize();
            
            // Envía el objeto User al servidor
            salida.writeUTF(user.toString());
            salida.flush();
        } catch (IOException e) {

            // Muestra un mensaje de error al usuario
            System.out.println("Error: No se pudo enviar el mensaje al servidor");

        }


    }

}