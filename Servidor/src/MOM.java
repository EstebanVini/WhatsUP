import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MOM {
    ServerSocket servidor;
    List<ManejadorDeClientes> clientes;

    List<String> UsuariosConectados;

    public MOM(int port) {
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
        MOM server = new MOM(12345);
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

                        for (ManejadorDeClientes client : clientes) {
                            try {
                                client.salida.writeUTF(listaUsuarios);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else  {
                        // Broadcast the message to all connected clients except the sender
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

                    }

                }
            } catch (IOException i) {
                System.out.println(i);
            }
        }
    }
}
