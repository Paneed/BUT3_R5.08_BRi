package service_utilisateurs.phuong;

import bri.services.BRIService;
import java.io.*;
import java.net.Socket;

/**
 * ServiceMajuscule : transforme le texte en majuscules.
 */
public class ServiceMajuscule implements BRIService {
    private final Socket socket;

    public ServiceMajuscule(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            out.println("=== Service Majuscule ===");
            out.println("Envoyez une phrase (END pour revenir au menu).");

            while (true) {
                String texte = in.readLine();
                if (texte == null || texte.equalsIgnoreCase("END")) {
                    break;
                }

                out.println(texte.toUpperCase());
                out.println("(Tapez une autre phrase ou END pour revenir au menu)");
            }

        } catch (IOException e) {
            System.err.println("Majuscule I/O: " + e.getMessage());
        }
        // ⚠️ on ne ferme pas la socket pour permettre le retour au menu
    }

    public static String toStringue() {
        return "ServiceMajuscule : renvoie le texte en majuscules";
    }

    @Override
    public String toString() {
        return toStringue();
    }
}
