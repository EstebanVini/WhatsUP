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
    public TextField clavesesion;
    public TextField llavedestinatario;
    public TextField llavepublicaFirma;
    public Button btnRevisarFirma;
    public Button btnDescifrar;
    public Button btndescifrarSobre;
    @FXML
    VBox vbox;
    @FXML
    TextArea textArea;
    @FXML
    Button button;

    private String ultimoMensajeRecibido = ""; // Variable para almacenar el último mensaje recibido
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
            } catch (IOException error) {
                System.out.println(error);
            }
        } else if (simetrico.isSelected()) {
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
        } else if (sobredigital.isSelected()) {
            try {
                String mensaje = textArea.getText();
                String llavesesion = clavesesion.getText();
                String llavepublicaReceptor = llavedestinatario.getText();
                String mensajeCifrado = Cifrado.cifrar(mensaje, Integer.parseInt(llavesesion));
                String mensajeyllave = mensajeCifrado + " Llave sesión: " + llavesesion;
                String sobre = Cifrado.cifrar(mensajeyllave, Integer.parseInt(llavepublicaReceptor));

                Platform.runLater(() -> {
                    Label label = new Label(sobre);
                    vbox.getChildren().add(label);
                });

                salida.writeUTF(sobre);
                textArea.setText("");
            } catch (IOException error) {
                System.out.println(error);
            }
        } else if (firmar.isSelected()) {
            try {
                String mensaje = textArea.getText();
                String llavePrivada = llaveprivada.getText();
                String hashMensaje = Cifrado.hash(mensaje);
                String mensajeCifrado = Cifrado.cifrar(hashMensaje, Integer.parseInt(llavePrivada));

                String llavePublica = llavepublica.getText();
                String mensajeFirmado = mensaje + " Firma: " + mensajeCifrado + " Llave pública: " + llavePublica;

                Platform.runLater(() -> {
                    Label label = new Label(mensajeFirmado);
                    vbox.getChildren().add(label);
                });

                salida.writeUTF(mensajeFirmado);
                textArea.setText("");
            } catch (Exception error) {
                System.out.println(error);
            }
        }
    }

    @FXML
    void funcbtnDescifrar() {
        if (!ultimoMensajeRecibido.isEmpty()) {
            String llaveDescifrado = llavedescifrar.getText();

            try {
                // Descifra el último mensaje recibido
                String mensajeDescifrado = Cifrado.descifrar(ultimoMensajeRecibido, Integer.parseInt(llaveDescifrado));

                // Muestra el mensaje descifrado en la interfaz
                Platform.runLater(() -> {
                    Label label = new Label("Mensaje Descifrado: " + mensajeDescifrado);
                    vbox.getChildren().add(label);
                });
            } catch (Exception e) {
                System.out.println("Error al descifrar el mensaje: " + e.getMessage());
            }
        }
    }

    @FXML
    void funcdescifrarsobre() {
        if (!ultimoMensajeRecibido.isEmpty()) {
            String llaveDescifrado = llavedescifrar.getText();

            try {
                // Descifra el último mensaje recibido
                String mensajeDescifrado = Cifrado.descifrar(ultimoMensajeRecibido, Integer.parseInt(llaveDescifrado));

                // si es un sobre, el mensaje descifrado es el mensaje aún cifrado y la llave de sesion, volver a descifrar con la llave de sesión que está en el mensaje
                String[] mensajeYllave = mensajeDescifrado.split(" Llave sesión: ");
                String mensaje = mensajeYllave[0];
                String llaveSesion = mensajeYllave[1];
                String mensajeDescifrado2 = Cifrado.descifrar(mensaje, Integer.parseInt(llaveSesion));

                // Muestra el mensaje descifrado en la interfaz
                Platform.runLater(() -> {
                    Label label = new Label("Mensaje del sobre : " + mensajeDescifrado2);
                    vbox.getChildren().add(label);
                });
            } catch (Exception e) {
                System.out.println("Error al descifrar el mensaje: " + e.getMessage());
            }
        }
    }

    @FXML
    void funcbtnComprobarFirma() {
        if (!ultimoMensajeRecibido.isEmpty()) {
            String llavePublica = llavepublicaFirma.getText();

            try {
                // Descifra el último mensaje recibido
                String[] mensajeYFirma = ultimoMensajeRecibido.split(" Firma: ");
                String mensaje = mensajeYFirma[0];
                String firma = mensajeYFirma[1];
                String[] firmaYllave = firma.split(" Llave pública: ");
                String firmaDescifrada = Cifrado.descifrar(firmaYllave[0], Integer.parseInt(llavePublica));
                String hashMensaje = Cifrado.hash(mensaje);

                // Muestra el mensaje descifrado en la interfaz
                Platform.runLater(() -> {
                    Label label = new Label("Mensaje: " + mensaje);
                    vbox.getChildren().add(label);
                    Label label2 = new Label("Firma: " + firmaDescifrada);
                    vbox.getChildren().add(label2);
                    Label label3 = new Label("Hash del mensaje: " + hashMensaje);
                    vbox.getChildren().add(label3);
                    if (firmaDescifrada.equals(hashMensaje)) {
                        Label label4 = new Label("La firma es válida");
                        vbox.getChildren().add(label4);
                    } else {
                        Label label4 = new Label("La firma no es válida");
                        vbox.getChildren().add(label4);
                    }
                });
            } catch (Exception e) {
                System.out.println("Error al descifrar el mensaje: " + e.getMessage());
            }
        }
    }



    private void descifrarUltimoMensaje() {
        if (ultimoMensajeRecibido.isEmpty()) {
            // Si no hay ningún mensaje, no se puede descifrar nada
            return;
        }

        // Obtén la llave para descifrar (puedes pedirla al usuario o usar una llave predefinida)
        String llaveDescifrado = llavedescifrar.getText();

        try {
            // Descifra el último mensaje recibido
            String mensajeDescifrado = Cifrado.descifrar(ultimoMensajeRecibido, Integer.parseInt(llaveDescifrado));

            // Muestra el mensaje descifrado en la interfaz
            Platform.runLater(() -> {
                Label label = new Label("Mensaje Descifrado: " + mensajeDescifrado);
                vbox.getChildren().add(label);
            });
        } catch (Exception e) {
            System.out.println("Error al descifrar el mensaje: " + e.getMessage());
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

                        // Actualiza el último mensaje recibido
                        ultimoMensajeRecibido = mensajeRecibido;
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
