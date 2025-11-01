package bri.appli;

import bri.services.BRIService;
import bri.services.ServiceAmateur;
import bri.services.ServiceProgrammeur;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Serveur BRi dynamique :
 * - écoute les connexions entrantes sur un port donné
 * - demande le mode (PROG ou AMA)
 * - lance le service correspondant sans fermer la socket
 */
public class ServeurBRIDyn implements Runnable {
    private final ServerSocket serverSocket;
    private volatile boolean running = true;

    public ServeurBRIDyn(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        System.out.println("✅ Serveur dynamique démarré sur le port " + serverSocket.getLocalPort());

        while (running) {
            try {
                Socket client = serverSocket.accept();
                System.out.println("➡️  Connexion détectée depuis " + client.getInetAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

                out.println("=== Serveur BRi ===");
                out.println("Choisissez un mode :");
                out.println("  PROG → Espace Programmeur");
                out.println("  AMA  → Espace Amateur");
                out.print("> "); out.flush();

                String mode = in.readLine();
                if (mode == null) {
                    client.close();
                    continue;
                }

                mode = mode.trim().toUpperCase();
                BRIService service;

                if (mode.equals("PROG")) {
                    service = new ServiceProgrammeur(client);
                } else if (mode.equals("AMA")) {
                    service = new ServiceAmateur(client);
                } else {
                    out.println("Mode inconnu. Utilisez PROG ou AMA.");
                    client.close();
                    continue;
                }

                new Thread(service).start();

            } catch (IOException e) {
                if (running)
                    System.err.println("Erreur réseau : " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("🛑 Serveur arrêté.");
    }

    public void lancer() {
        new Thread(this).start();
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException ignored) {}
    }
}
