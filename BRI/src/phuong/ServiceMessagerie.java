package phuong;

import bri.services.BRIService;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * ServiceMessagerie : permet d’envoyer et de lire des messages entre utilisateurs.
 * Conforme à la norme BRi.
 */
public class ServiceMessagerie implements BRIService {
    private final Socket socket;

    // 🔁 Ressource partagée entre toutes les instances du service
    private static final Map<String, List<String>> messages = new HashMap<>();

    public ServiceMessagerie(Socket socket) {
        this.socket = socket;
    }

    public static String toStringue() {
        return "ServiceMessagerie : messagerie interne entre utilisateurs (partagée)";
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            out.println("=== 💬 Service Messagerie ===");
            afficherMenu(out);

            while (true) {
                out.print("> ");
                out.flush();

                String action = in.readLine();

                if (action == null || action.equalsIgnoreCase("END")) {
                    out.println("--- Fin du service Messagerie ---");
                    break; // retour à ServiceAmateur
                }

                switch (action.trim().toUpperCase()) {
                    case "ENVOI" -> handleEnvoi(in, out);
                    case "LECTURE" -> handleLecture(in, out);
                    default -> out.println("❌ Action inconnue. Utilisez 'ENVOI', 'LECTURE' ou 'END'.");
                }

                // 🌀 Réaffiche le menu après chaque action
                afficherMenu(out);
            }

        } catch (IOException e) {
            System.err.println("Erreur ServiceMessagerie : " + e.getMessage());
        }
        // ⚠️ NE PAS fermer la socket ici : ServiceAmateur la gère
    }

    /**
     * Affiche le menu principal des options
     */
    private void afficherMenu(PrintWriter out) {
        out.println();
        out.println("Tapez :");
        out.println("  ENVOI   → pour envoyer un message");
        out.println("  LECTURE → pour lire vos messages");
        out.println("  END     → pour revenir au menu principal");
    }

    /**
     * ENVOI → l'utilisateur envoie un message à un autre
     */
    private void handleEnvoi(BufferedReader in, PrintWriter out) throws IOException {
        out.println("👤 Entrez le login du destinataire :");
        String destinataire = in.readLine();

        if (destinataire == null || destinataire.isBlank()) {
            out.println("❗ Pseudo invalide. Envoi annulé.");
            return;
        }

        out.println("💌 Entrez votre message :");
        String message = in.readLine();

        if (message == null || message.isBlank()) {
            out.println("❗ Message vide. Envoi annulé.");
            return;
        }

        synchronized (messages) {
            messages.computeIfAbsent(destinataire.trim(), k -> new ArrayList<>()).add(message);
        }

        out.println("✅ Message envoyé à " + destinataire.trim());
    }

    /**
     * LECTURE → l'utilisateur lit ses messages
     */
    private void handleLecture(BufferedReader in, PrintWriter out) throws IOException {
        out.println("👤 Entrez votre login pour lire vos messages :");
        String pseudo = in.readLine();

        if (pseudo == null || pseudo.isBlank()) {
            out.println("❗ Login manquant.");
            return;
        }

        List<String> messagesRecus;
        synchronized (messages) {
            messagesRecus = messages.getOrDefault(pseudo.trim(), new ArrayList<>());
        }

        if (messagesRecus.isEmpty()) {
            out.println("📭 Aucun message pour " + pseudo.trim());
        } else {
            out.println("=== 📬 Messages reçus pour " + pseudo.trim() + " ===");
            int i = 1;
            for (String msg : messagesRecus) {
                out.println("[" + i++ + "] " + msg);
            }
            out.println("==============================");

            // Suppression après lecture
            synchronized (messages) {
                messages.remove(pseudo.trim());
            }

            out.println("✅ Messages supprimés après lecture.");
        }
    }

    @Override
    public String toString() {
        return toStringue();
    }
}
