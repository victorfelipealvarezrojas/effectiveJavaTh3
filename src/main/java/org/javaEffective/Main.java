package org.javaEffective;

public class Main {
    public static void main(String[] args) {

        String cadenaOriginal = "Hola";
        System.out.println("Cadena original: " + cadenaOriginal);

        cadenaOriginal = ", mundo edit!";
        System.out.println("Nueva cadena: " + cadenaOriginal);

        // La cadena original no se modifica
        System.out.println("Cadena original después de la concatenación: " + cadenaOriginal);
    }
}