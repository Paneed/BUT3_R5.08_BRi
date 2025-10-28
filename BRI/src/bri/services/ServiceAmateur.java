package bri.services;

import bri.appli.DynamicServiceLoader;

import java.io.*;
import java.net.Socket;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ServiceAmateur :
 * Permet à un client amateur de :
 *  - Lister les services disponibles
 *  - Exécuter un service enregistré dynamiquement
 *  - Quitter la session
 *
 * Conforme au cahier des charges BRi.
 */
public class ServiceAmateur implements BRIService {

    private final Socket socket;

    public ServiceAmateur(Socket socket) {
        this.socket = socket;
    }

    public static String toStringue() {
        return "ServiceAmateur : permet de lister et exécuter les services déclarés.";
    }

    @Override
    public String toString() {
        return toStringue();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            printMenu(out);

            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();

                if (line.equalsIgnoreCase("LIST")) {
                    Set<String> names = ServiceRegister.serviceNames();
                    if (names.isEmpty()) {
                        out.println("> Aucun service disponible pour le moment.");
                    } else {
                        out.println("> Services disponibles : " +
                                names.stream().collect(Collectors.joining(", ")));
                    }

                } else if (line.toUpperCase().startsWith("RUN ")) {
                    String name = line.substring(4).trim();
                    String fqcn = ServiceRegister.resolve(name);

                    if (fqcn == null) {
                        out.println("> ❌ Service inconnu : " + name);
                    } else {
                        out.println("> ⏳ Exécution du service : " + name);
                        out.flush();

                        try {
                            String ftpUrl = "file:///" + System.getProperty("user.dir").replace('\\', '/') + "/classes/";
                            Class<?> clazz = DynamicServiceLoader.loadClass(fqcn, ftpUrl);

                            if (!BRIService.class.isAssignableFrom(clazz)) {
                                out.println("> ❌ Classe invalide : " + fqcn + " n'implémente pas BRIService.");
                            } else {
                                BRIService service = (BRIService) clazz
                                        .getConstructor(Socket.class)
                                        .newInstance(socket);

                                service.run();

                                out.println();
                                out.println("> ✅ Fin du service \"" + name + "\".");
                            }

                        } catch (Exception e) {
                            out.println("> ⚠️ Erreur lors de l'exécution : " + e.getMessage());
                        }
                    }

                } else if (line.equalsIgnoreCase("QUIT")) {
                    out.println("> 👋 Au revoir !");
                    break;

                } else {
                    out.println("> ❓ Commande inconnue. Tapez LIST, RUN <nom> ou QUIT.");
                }

                printMenu(out);
            }

        } catch (IOException e) {
            System.err.println("❌ Erreur ServiceAmateur : " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

    private void printMenu(PrintWriter out) {
        out.println();
        out.println("=== BRi - Espace Amateur ===");
        out.println("Commandes disponibles :");
        out.println("  LIST           → Lister les services disponibles");
        out.println("  RUN <nom>      → Exécuter un service (ex: RUN Inversion)");
        out.println("  QUIT           → Quitter la session");
        out.print("> ");
        out.flush();
    }
}
