import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class AR {

    public static String readAndJoinLines(String filePath) {
        StringBuilder result = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line).append(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Eliminar la última coma si hay alguna línea leída
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }


    public static void main(String[] args) {
        try {
            Socket socket = null;
            socket = new Socket("127.0.0.1", 12345);

            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());


            // Enviar mensajes desde la consola al servidor
            while (true) {
                String temp = entrada.readUTF();

                System.out.println(temp);

                if (temp.startsWith("Login Usuario")) {
                    String[] parts = temp.split(",");
                    String username = parts[1];
                    String password = parts[2];
                    String phone = parts[3];


                    String dataCertificado = "";
                    try{
                        String RutaCertificado = parts[4];
                        dataCertificado = readAndJoinLines(RutaCertificado);
                    } catch (Exception e){
                        System.out.println("Error al leer certificado");
                    }


                    if (dataCertificado.contains(username) && dataCertificado.contains(password)) {
                        System.out.println("Usuario encontrado");
                        salida.writeUTF("Login exitoso " + username );
                    } else {
                        System.out.println("Usuario no encontrado");
                        salida.writeUTF("Usuario no encontrado");
                    }


                }

            }
        } catch (IOException i) {
            System.out.println(i);
        }

    }
}