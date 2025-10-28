package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientProgrammeur {

    public static void main(String[] args) {
        int PORT_SERVICE = 9000;
        String HOST = "localhost";

        try (Socket socket = new Socket(HOST, PORT_SERVICE);
             BufferedReader sin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in))) {

            Thread readerThread = new Thread(() -> {
                try {
                    String in;
                    while ((in = sin.readLine()) != null) {
                        System.out.println(in);
                    }
                } catch (IOException e) {
                    System.out.println("Connexion terminée par le serveur.");
                }
            });
            readerThread.start();

            System.out.println("Connecté au serveur BRi. Tapez 'END' pour quitter.");
            String message;
            while ((message = clavier.readLine()) != null) {
                if (message.equalsIgnoreCase("END")) {
                    System.out.println(" === Fin de la session programmeur 👋 === ");
                    break;
                }
                out.println(message);
            }

        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
