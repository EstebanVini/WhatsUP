package com.example.cliente;

import java.util.Arrays;

public class Cifrado {

    private static final String ALFABETO = "abcdefghijklmnñopqrstuvwxyz";
    private static final String ALFABETO_MAYUSCULA = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ";

    private static final String NUMEROS = "0123456789";

    private static final String SIMBOLOS = ".,;:!?¡¿()[]{}+-*=<>@#$%&'/";

    public static String cifrar(String texto, int desplazamiento) {
        StringBuilder cifrado = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            int indice = -1;

            if (ALFABETO.indexOf(caracter) != -1) {
                indice = (ALFABETO.indexOf(caracter) + desplazamiento) % ALFABETO.length();
                if (indice < 0) {
                    indice += ALFABETO.length();
                }
                cifrado.append(ALFABETO.charAt(indice));
            } else if (ALFABETO_MAYUSCULA.indexOf(caracter) != -1) {
                indice = (ALFABETO_MAYUSCULA.indexOf(caracter) + desplazamiento) % ALFABETO_MAYUSCULA.length();
                if (indice < 0) {
                    indice += ALFABETO_MAYUSCULA.length();
                }
                cifrado.append(ALFABETO_MAYUSCULA.charAt(indice));
            } else if (NUMEROS.indexOf(caracter) != -1) {
                indice = (NUMEROS.indexOf(caracter) + desplazamiento) % NUMEROS.length();
                if (indice < 0) {
                    indice += NUMEROS.length();
                }
                cifrado.append(NUMEROS.charAt(indice));
            } else if (SIMBOLOS.indexOf(caracter) != -1) {
                indice = (SIMBOLOS.indexOf(caracter) + desplazamiento) % SIMBOLOS.length();
                if (indice < 0) {
                    indice += SIMBOLOS.length();
                }
                cifrado.append(SIMBOLOS.charAt(indice));
            } else {
                cifrado.append(caracter);
            }
        }
        return cifrado.toString();
    }



    public static String descifrar(String texto, int desplazamiento) {
        return cifrar(texto, -desplazamiento);
    }

    public static int inverso(int desplazamiento) {
        return 27 - desplazamiento * -1;
    }


    public static void main(String[] args) {
        String texto = "Hola, mundo! ¿Cómo estás? 123";
        int desplazamiento = 67;

        String cifrado = cifrar(texto, desplazamiento);
        System.out.println("Texto cifrado: " + cifrado);

        String descifrado = descifrar(cifrado, desplazamiento);
        System.out.println("Texto descifrado: " + descifrado);

        int inverso = inverso(desplazamiento);
        System.out.println("Inverso del desplazamiento: " + inverso);

        String descifrado2 = descifrar(cifrado, inverso);
        System.out.println("Texto descifrado con el inverso: " + descifrado2);
    }
}
