import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    ServerSocket servidor;
    List<ManejadorDeClientes> clientes;

    List<String> UsuariosConectados;

    public Server(int port) {
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

    public static void main(String args[]) {
        Server server = new Server(12345);
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

                    if (temp.startsWith("Nuevo usuario") || temp.startsWith("Login Usuario") || temp.startsWith("Login exitoso") || temp.startsWith("Registro exitoso") || temp.startsWith("Usuario no encontrado")) {
                        DataOutputStream clienteSalida = this.salida;
                        for (ManejadorDeClientes client : clientes) {
                            if (client.salida != clienteSalida) {
                                try {
                                    client.salida.writeUTF(temp);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } else  {
                        // Broadcast the message to all connected clients except the sender
                        DataOutputStream clienteSalida = this.salida;
                        for (ManejadorDeClientes client : clientes) {
                            if (client.salida != clienteSalida) {
                                try {
                                    client.salida.writeUTF("Nuevo mensaje," + temp);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                }
            } catch (IOException i) {
                System.out.println(i);
            }
        }
    }
}
