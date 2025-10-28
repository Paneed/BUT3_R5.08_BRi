package phuong;

import bri.services.BRIService;
import java.io.*;
import java.net.Socket;

/**
 * ServiceMinuscule : convertit le texte en minuscules.
 */
public class ServiceMinuscule implements BRIService {
    private final Socket socket;

    public ServiceMinuscule(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            out.println("=== Service Minuscule ===");
            out.println("Envoyez une phrase (END pour revenir au menu).");

            while (true) {
                String s = in.readLine();
                if (s == null || s.equalsIgnoreCase("END")) {
                    break; // on sort de la boucle mais on NE FERME PAS la socket
                }

                out.println(s.toLowerCase());
                out.println("(Tapez une autre phrase ou END pour revenir au menu)");
            }

        } catch (IOException e) {
            System.err.println("Minuscule I/O: " + e.getMessage());
        }
        // ⚠️ Ne pas fermer la socket ici, pour permettre le retour au menu
    }

    public static String toStringue() {
        return "ServiceMinuscule : renvoie le texte en minuscules";
    }

    @Override
    public String toString() {
        return toStringue();
    }
}
