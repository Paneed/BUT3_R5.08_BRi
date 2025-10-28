package client;

import java.io.*;
import java.net.Socket;

public class ClientAmateur {

    public static void main(String[] args) {
        int PORT_SERVICE = 9001;
        String HOST = "localhost";

        try (
                Socket s = new Socket(HOST, PORT_SERVICE);
                BufferedReader sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter sout = new PrintWriter(s.getOutputStream(), true);
                BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in))
        ) {

            // --- Thread de lecture : affiche tout ce que le serveur envoie ---
            Thread readerThread = new Thread(() -> {
                try {
                    String in;
                    while ((in = sin.readLine()) != null) {
                        // Remplace __ par un vrai retour à la ligne si nécessaire
                        in = in.replaceAll("__", "\n");
                        System.out.println(in);
                    }
                } catch (IOException e) {
                    System.out.println("Connexion terminée par le serveur.");
                }
            });
            readerThread.setDaemon(true);
            readerThread.start();

            String message;
            while ((message = clavier.readLine()) != null) {
                sout.println(message);
            }

        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
