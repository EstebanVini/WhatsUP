import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class AC {

    public static boolean GenerarCertificado(String texto, String nombreArchivo) {
        try {
            // Crear un objeto File que representa el archivo
            File archivo = new File("Certificados", nombreArchivo);

            // Crear un objeto FileWriter que permitirá escribir en el archivo
            FileWriter escritor = new FileWriter(archivo);

            // Crear un objeto BufferedWriter para escribir de manera eficiente
            BufferedWriter bufferEscritura = new BufferedWriter(escritor);

            // Dividir el texto en líneas
            String[] lineas = texto.split(",");

            // Escribir cada línea en el archivo
            for (String linea : lineas) {
                bufferEscritura.write(linea);
                bufferEscritura.newLine(); // Agregar una nueva línea después de cada línea
            }

            // Cerrar el BufferedWriter y FileWriter para liberar recursos
            bufferEscritura.close();
            escritor.close();

            System.out.println("Certificado generado Exitosamente.");

            return true;



        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {
        try {
            Socket socket = null;
            while (socket == null) {
                try {
                    socket = new Socket("127.0.0.1", 12345);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());


            // Enviar mensajes desde la consola al servidor
            String temp = "";

            while (true) {
                temp = entrada.readUTF();



                if (temp.startsWith("Nuevo usuario")) {
                    System.out.println(temp);
                    String[] parts = temp.split(",");

                    String username = parts[1];
                    String password = parts[2];

                    String phone = parts[3];

                    int LlavePublica = Integer.parseInt(phone.substring(phone.length() - 2));

                    int LLavePrivada = Cifrado.inverso(LlavePublica);

                    String LLavePrivadaString = Integer.toString(LLavePrivada);

                    String NuevoCertificado = "Usuario: "+ username + ",Contraseña: " + password + ",LLave Privada: " + LLavePrivadaString;

                    if (GenerarCertificado(NuevoCertificado, username + ".txt")){
                        System.out.println("Registro exitoso" + username);
                    } else {
                        System.out.println("Error al generar certificado");
                    }

                }

            }
        } catch (IOException i) {
            System.out.println(i);
        }
    }
}