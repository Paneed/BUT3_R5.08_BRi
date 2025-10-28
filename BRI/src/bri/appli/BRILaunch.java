package bri.appli;

import bri.programmeur.Programmeur;
import bri.programmeur.ProgrammeurRegister;
import bri.services.BRIService;
import bri.services.ServiceProgrammeur;
import bri.services.ServiceAmateur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe principale du serveur BRiLaunch :
 * - Démarre deux serveurs (Programmeur & Amateur)
 * - Chaque serveur crée un service correspondant sur chaque connexion
 * - Ne gère plus le registre (délégué à BRIRegister)
 *
 * Respecte le principe SOLID : une seule responsabilité.
 */
public class BRILaunch {

    // Ports utilisés
    public static final int PORT_PROG = 9000;
    public static final int PORT_AMA = 9001;

    public static void main(String[] args) {
        try {
            // === 1️⃣ Initialisation des programmeurs ===
            Programmeur p1 = new Programmeur("alice", "pass", "ftp://localhost");
            Programmeur p2 = new Programmeur("phuong", "1234", "ftp://localhost");
            ProgrammeurRegister.getInstance().addProg(p1);
            ProgrammeurRegister.getInstance().addProg(p2);

            // === 2️⃣ Lancement des serveurs ===
            new Thread(() -> lancerServeur(PORT_PROG, true)).start();  // Serveur Programmeur
            new Thread(() -> lancerServeur(PORT_AMA, false)).start();  // Serveur Amateur

            System.out.println("✅ Serveur BRi lancé avec succès !");
            System.out.println("   - Port Programmeur : " + PORT_PROG);
            System.out.println("   - Port Amateur     : " + PORT_AMA);

        } catch (Exception e) {
            System.err.println("❌ Erreur au démarrage du serveur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Démarre un serveur BRi sur le port spécifié.
     * Si programmeur == true → ServiceProgrammeur
     * Sinon → ServiceAmateur
     */
    private static void lancerServeur(int port, boolean programmeur) {
        String type = programmeur ? "Programmeur" : "Amateur";

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("🟢 Serveur " + type + " démarré sur le port " + port);

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("➡️  Nouvelle connexion " + type + " depuis " + client.getInetAddress());

                BRIService service = programmeur
                        ? new ServiceProgrammeur(client)
                        : new ServiceAmateur(client);

                new Thread(service).start();
            }

        } catch (IOException e) {
            System.err.println("❌ Erreur sur le serveur " + type + " (" + port + ") : " + e.getMessage());
        }
    }
}
