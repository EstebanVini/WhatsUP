package com.example.cliente;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    public TextField username, password, phone, llavepublica, llavedescifrar, llaveprivada, claveDesplazamiento,
            clavesesion, llavedestinatario, llavepublicaFirma;
    public Button btnlogin, btnregistro, btnRevisarFirma, btnDescifrar, btndescifrarSobre;

    public CheckBox simetrico, asimetrico, sobredigital, plano, firmar;
    public Button btnRegistrar;

    @FXML
    private ListView<String> connectedUsersList;

    private ObservableList<String> connectedUsers = FXCollections.observableArrayList();

    @FXML
    VBox vbox;
    @FXML
    TextArea textArea;
    @FXML
    Button button;
    @FXML
    private Button btnSeleccionarArchivo;

    @FXML
    private Button btnRegresar;

    @FXML
    private Label lblRutaArchivo;
    @FXML
    private Button btnRegistro;

    private String ultimoMensajeRecibido = ""; // Variable para almacenar el último mensaje recibido
    Socket socket;

    Socket socketAC;

    Socket socketAR;
    DataOutputStream salida;

    DataOutputStream salidaAC;

    DataOutputStream salidaAR;
    DataInputStream entrada;

    DataInputStream entradaAC;

    DataInputStream entradaAR;

    String rutaCertificado = "";

    Boolean LoginExitoso = false;

    Boolean RegistroExitoso = false;

    String UsuarioActual = "";

    // lista de usuarios conectados
    private List<User> users = new ArrayList<>();

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
    void seleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo");

        // Muestra el diálogo y obtén el archivo seleccionado
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // Obtiene la ruta del archivo seleccionado y la muestra en la etiqueta
            rutaCertificado = selectedFile.getAbsolutePath();
            lblRutaArchivo.setText("Ruta del Archivo: " + rutaCertificado);

            // Puedes guardar la ruta del archivo en una variable si es necesario
            // Por ejemplo: String rutaArchivoSeleccionado = rutaArchivo;
        } else {
            // El usuario cerró el diálogo sin seleccionar un archivo
            lblRutaArchivo.setText("Ruta del Archivo: (Ningún archivo seleccionado)");
        }
    }

    @FXML
    void funcbtnDescifrar() {
        if (!ultimoMensajeRecibido.isEmpty()) {
            String llaveDescifrado = llavedescifrar.getText();

            try {
                // separar por comas y quitar el primer elemento
                String[] parts = ultimoMensajeRecibido.split(",");
                String mensaje = parts[1];
                // Descifra el último mensaje recibido
                String mensajeDescifrado = Cifrado.descifrar(mensaje, Integer.parseInt(llaveDescifrado));



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

    public void initializeConnectedUsers(String[] users) {
        connectedUsers.setAll(users);
        connectedUsersList.setItems(connectedUsers);
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


                        // Si el mensaje recibido comienza con la palabra Usuarios, entonces es una lista de usuarios conectados con este formato Usuarios: [Usuario:Esteban,Hola,00988989, Usuario:Andres,Hottla,00238989]
                        if (mensajeRecibido.startsWith("Usuarios:")) {
                            // Obtener la lista de usuarios conectados
                            String listaUsuarios = mensajeRecibido.substring(10);

                            // Convertir la lista de usuarios a un arreglo
                            String[] usuarios = listaUsuarios.split(", ");

                            // Limpiar la lista de usuarios
                            users.clear();

                            // Recorrer el arreglo de usuarios
                            for (String usuario : usuarios) {
                                // Convertir el usuario a un objeto User
                                String[] parts = usuario.split(",");
                                String username = parts[0].substring(8);
                                String password = parts[1];
                                String phone = parts[2];
                                User user = new User(username, password, phone);

                                // Agregar el usuario a la lista de usuarios
                                users.add(user);
                            }

                            // Actualizar la lista de usuarios en la interfaz y mostrarla
                            Platform.runLater(() -> {
                                initializeConnectedUsers(usuarios);
                            });
                        } else if (mensajeRecibido.startsWith("Login exitoso") && mensajeRecibido.contains(UsuarioActual)) {

                            LoginExitoso = true;
                            System.out.println("Usuario Autenticado Correctamente");

                        } else if (mensajeRecibido.startsWith("Registro exitoso") && mensajeRecibido.contains(UsuarioActual)) {

                            RegistroExitoso = true;
                            System.out.println("Registro exitoso");
                            if (RegistroExitoso) {
                                // Cambia automáticamente a la siguiente vista después de un inicio de sesión exitoso
                                abrirLogin();
                            }
                        }

                        else if(mensajeRecibido.startsWith("Nuevo mensaje")) {
                            //separar por comas y quitar el primer elemento
                            String[] parts = mensajeRecibido.split(",");
                            String mensaje = parts[1];
                            Platform.runLater(() -> {
                                Label label = new Label(mensaje);
                                label.setStyle("-fx-font-weight: bold;");
                                vbox.getChildren().add(label);
                            });
                        }

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

    // Función para inicializar la conexión con el servidor de certificados para hacer el registro
    public void initializeCertificado() {
        Thread socketThread = new Thread(() -> {
            try {
                socketAC = new Socket("127.0.0.1", 12346);
                entradaAC = new DataInputStream(new BufferedInputStream(socketAC.getInputStream()));
                salidaAC = new DataOutputStream(socketAC.getOutputStream());

            } catch (IOException error) {
                System.out.println(error);
            }
        });

        }

    @FXML
    void abrirRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("registro.fxml"));
            Parent root = loader.load();

            // Configura el controlador actual para la nueva vista
            HelloController registroController = loader.getController();
            // Puedes agregar configuraciones específicas para la vista de registro si es necesario

            Scene scene = new Scene(root);
            Stage stage = (Stage) btnRegistro.getScene().getWindow(); // Obtiene la ventana actual
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error al abrir la ventana de registro: " + e.getMessage());
        }
    }

    @FXML
    void abrirLogin(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            // Configura el controlador actual para la nueva vista
            HelloController loginController = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = (Stage) btnRegresar.getScene().getWindow(); // Obtiene la ventana actual
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error al abrir la ventana de registro: " + e.getMessage());
        }
    }

    void  abrirChat(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();

            // Configura el controlador actual para la nueva vista
            HelloController chatController = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = (Stage) btnlogin.getScene().getWindow(); // Obtiene la ventana actual
            stage.setScene(scene);
        } catch (Exception e) {
            System.out.println("Error al abrir la ventana de registro: " + e.getMessage());
        }
    }

    @FXML
    void Login() {
        String usernameText = username.getText();
        String passwordText = password.getText();
        String phoneText = phone.getText();


        // Valida los campos de nombre de usuario, contraseña y teléfono
        if (usernameText.isEmpty() || passwordText.isEmpty() || phoneText.isEmpty()) {
            // Muestra un mensaje de error al usuario
            System.out.println("Error: Debe llenar todos los campos");
            return;
        }

        String hashPassword = Cifrado.hash(passwordText);
        // Crea un objeto User con los datos ingresados
        User user = new User(usernameText, hashPassword, phoneText);

        // Envía el objeto User al servidor
        try {

            if (!LoginExitoso){
                // Envía el objeto User al servidor
                salida.writeUTF("Login Usuario" + "," + user.getUsername() + "," + user.getPassword() + "," + user.getPhone() + "," + rutaCertificado);
                salida.flush();

                // Muestra un mensaje de éxito al usuario
                System.out.println("Solicitando Login: " + user.getUsername());
            }
            else {
                // Cambia automáticamente a la siguiente vista después de un inicio de sesión exitoso
                abrirChat();
            }





        } catch (IOException e) {
            // Muestra un mensaje de error al usuario
            System.out.println("Error: No se pudo enviar el mensaje al servidor");
        }
    }

    @FXML
    void Registro() {
        String usernameText = username.getText();
        String passwordText = password.getText();
        String phoneText = phone.getText();


        // Valida los campos de nombre de usuario, contraseña y teléfono
        if (usernameText.isEmpty() || passwordText.isEmpty() || phoneText.isEmpty()) {
            // Muestra un mensaje de error al usuario
            System.out.println("Error: Debe llenar todos los campos");
            return;
        }

        String hashPassword = Cifrado.hash(passwordText);
        // Crea un objeto User con los datos ingresados
        User user = new User(usernameText, hashPassword, phoneText);

        // Envía el objeto User al servidor
        try {
            // inicializa la conexión con el servidor usando la función initialize()
            initialize();

            // Envía el objeto User al servidor
            salida.writeUTF("Nuevo usuario" + "," + user.getUsername() + "," + user.getPassword() + "," + user.getPhone());
            salida.flush();

            if (RegistroExitoso) {
                // Cambia automáticamente a la siguiente vista después de un inicio de sesión exitoso
                abrirLogin();
            }

        } catch (IOException e) {
            // Muestra un mensaje de error al usuario
            System.out.println("Error: No se pudo enviar el mensaje al servidor");
        }
    }



    @FXML
    private void startChat(ActionEvent event) {
        String selectedUser = connectedUsersList.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) username.getScene().getWindow(); // Obtiene la ventana actual
                stage.setScene(scene);
            } catch (IOException e) {
                System.out.println("Error al abrir la ventana de chat: " + e.getMessage());
            }
        } else {
            System.out.println("Error: Debe seleccionar un usuario");
        }
        }
}

