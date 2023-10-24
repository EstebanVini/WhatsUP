package com.example.cliente;

public class cifrado {
    private String lMayus;
    private String lMin;
    private String numeros;

    public cifrado(String lMayus, String lMin) {
        this.lMayus = lMayus;
        this.lMin = lMin;
    }

    public String cifrar(String texto, int desplazamiento) {
        StringBuilder salida = new StringBuilder();
        int lMayusLength = this.lMayus.length();
        int lMinLength = this.lMin.length();

        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);

            if ((this.lMayus.indexOf(caracter) != -1) || (this.lMin.indexOf(caracter) != -1)) {
                String alfabeto = (this.lMayus.indexOf(caracter) != -1) ? this.lMayus : this.lMin;
                int indice = alfabeto.indexOf(caracter);
                int nuevoIndice = (indice + desplazamiento) % alfabeto.length();
                char nuevoCaracter = alfabeto.charAt(nuevoIndice);
                salida.append(nuevoCaracter);
            } else {
                salida.append(caracter);
            }
        }

        return salida.toString();
    }

    public String descifrar(String texto, int desplazamiento) {
        StringBuilder salida = new StringBuilder();
        int lMayusLength = this.lMayus.length();
        int lMinLength = this.lMin.length();

        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);

            if ((this.lMayus.indexOf(caracter) != -1) || (this.lMin.indexOf(caracter) != -1)) {
                String alfabeto = (this.lMayus.indexOf(caracter) != -1) ? this.lMayus : this.lMin;
                int indice = alfabeto.indexOf(caracter);
                int nuevoIndice = (indice - desplazamiento) % alfabeto.length();
                if (nuevoIndice < 0) {
                    nuevoIndice += alfabeto.length();
                }
                char nuevoCaracter = alfabeto.charAt(nuevoIndice);
                salida.append(nuevoCaracter);
            } else {
                salida.append(caracter);
            }
        }

        return salida.toString();
    }
}

