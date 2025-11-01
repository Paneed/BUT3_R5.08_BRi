package alexis;

import bri.services.BRIService;

import java.io.*;
import java.net.Socket;

/**
 * ServiceInversion : renvoie le texte inversé.
 * Conforme à la norme BRi (TP4)
 */
public class ServiceInversionV2 implements BRIService {
    private final Socket socket;

    public ServiceInversionV2(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            out.println("=== Service Inversion 🙃 ===");
            out.println("Envoyez une phrase à inverser (ou tapez END pour revenir au menu).");

            while (true) {
                String texte = in.readLine();
                if (texte == null || texte.equalsIgnoreCase("END")) {
                    break; // On revient à ServiceAmateur
                }

                String resultat = new StringBuilder(texte).reverse().toString();
                out.println("Résultat : " + resultat);
                out.println("(Envoyez une autre phrase ou END)");
            }

        } catch (IOException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
        }
        // On NE ferme PAS la socket ici pour que ServiceAmateur reprenne la main
    }

    public static String toStringue() {
        return "ServiceInversion : renvoie le texte inversé";
    }

    @Override
    public String toString() {
        return toStringue();
    }
}
