import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;



public class AR {
    ServerSocket servidor;
    List<ManejadorDeClientes> clientes;

    List<String> UsuariosConectados;

    public AR(int port) {
        clientes = new ArrayList<>();
        UsuariosConectados = new ArrayList<>();

        try {
            servidor = new ServerSocket(port);
            System.out.println("Servidor Corriendo");
            while (true) {
                Socket socket = servidor.accept();
                System.out.println("Nuevo Cliente aceptado");

                ManejadorDeClientes clientHandler = new ManejadorDeClientes(socket);
                clientes.add(clientHandler);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void GenerarCertificado(String texto, String nombreArchivo) {
        try {
            // Crear un objeto File que representa el archivo
            File archivo = new File(nombreArchivo);

            // Crear un objeto FileWriter que permitirá escribir en el archivo
            FileWriter escritor = new FileWriter(archivo);

            // Crear un objeto BufferedWriter para escribir de manera eficiente
            BufferedWriter bufferEscritura = new BufferedWriter(escritor);

            // Dividir el texto en líneas
            String[] lineas = texto.split("\n");

            // Escribir cada línea en el archivo
            for (String linea : lineas) {
                bufferEscritura.write(linea);
                bufferEscritura.newLine(); // Agregar una nueva línea después de cada línea
            }

            // Cerrar el BufferedWriter y FileWriter para liberar recursos
            bufferEscritura.close();
            escritor.close();

            System.out.println("Texto escrito exitosamente en el archivo.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        AR server = new AR(12346);
    }

    private class ManejadorDeClientes implements Runnable {
        private Socket socket;
        private DataInputStream entrada;
        private DataOutputStream salida;

        public ManejadorDeClientes(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                entrada = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                salida = new DataOutputStream(socket.getOutputStream());

                String temp = "";

                while (true) {
                    temp = entrada.readUTF();

                    System.out.println(temp);

                    if (temp.startsWith("Nuevo usuario")) {
                        String[] parts = temp.split(",");

                        String username = parts[1];
                        String password = parts[2];
                        String phone = parts[3];

                        String NuevoUsuario = " Usuario:"+ username + "," + password + "," + phone;

                        UsuariosConectados.add(NuevoUsuario);
                        System.out.println("Nuevo usuario creado");

                        System.out.println("Usuarios: " + UsuariosConectados.toString());

                        // Mandar Lista de usuarios conectados a todos los clientes
                        String listaUsuarios = "Usuarios: " + UsuariosConectados.toString();

                    }

                }
            } catch (IOException i) {
                System.out.println(i);
            }
        }
    }
}