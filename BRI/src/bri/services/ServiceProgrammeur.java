package bri.services;

import bri.programmeur.ProgrammeurRegister;
import bri.services.ServiceRegister;
import bri.appli.DynamicServiceLoader;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.util.Set;
import java.util.stream.Collectors;

import static bri.services.ServiceRegister.serviceNames;

/**
 * ServiceProgrammeur :
 * Permet à un programmeur authentifié de :
 *  - Ajouter / Mettre à jour un service
 *  - Supprimer un service
 *  - Changer son adresse FTP
 *  - Lister les services déclarés
 *
 * Conforme à la norme BRi (TP4 / projet 2025-26).
 */
public class ServiceProgrammeur implements BRIService {

    private final Socket socket;

    public ServiceProgrammeur(Socket socket) {
        this.socket = socket;
    }

    public static String toStringue() {
        return "ServiceProgrammeur : gérer les services côté programmeur";
    }

    @SuppressWarnings("unchecked")
    private Class<? extends BRIService> loadAndValidate(String fqcn, String ftpUrl) throws Exception {
        Class<?> clazz = DynamicServiceLoader.loadClass(fqcn, ftpUrl);

        if (!BRIService.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("La classe " + fqcn + " n'implémente pas BRIService.");
        }

        try {
            Constructor<?> c = clazz.getConstructor(Socket.class);
            if (!java.lang.reflect.Modifier.isPublic(c.getModifiers())) {
                throw new IllegalArgumentException("Le constructeur (Socket) de " + fqcn + " doit être public.");
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Le service " + fqcn + " doit avoir un constructeur public (Socket).");
        }

        return (Class<? extends BRIService>) clazz;
    }

    /** Ajoute ou met à jour un service (update = false → ajout / true → mise à jour) */
    private void addOrUpdate(String login, BufferedReader in, PrintWriter out, boolean update) throws IOException {
        out.println("Nom logique (ex: Inversion): "); out.flush();
        String logical = in.readLine();
        out.println("FQCN (ex: bri.phuong.ServiceInversion): "); out.flush();
        String fqcn = in.readLine();

        if (logical == null || logical.isBlank() || fqcn == null || fqcn.isBlank()) {
            out.println("❌ Paramètres manquants.");
            return;
        }

        logical = logical.trim();
        fqcn = fqcn.trim();

        boolean exists = ServiceRegister.serviceNames().contains(logical);

        String ftpUrl = ServiceRegister.getFTP(login);
        if (ftpUrl == null || ftpUrl.isBlank()) {
            ftpUrl = "file:///" + System.getProperty("user.dir").replace('\\', '/') + "/classes/";
        }

        try {
            Class<? extends BRIService> loaded = loadAndValidate(fqcn, ftpUrl);

            if (update) {
                if (!exists) {
                    out.println("⚠️ Le service '" + logical + "' n'existe pas, impossible de le mettre à jour.");
                    return;
                }
                ServiceRegister.register(logical, fqcn);
                out.println("✅ Mise à jour dynamique OK : " + logical + " -> " + loaded.getName());
            } else {
                if (exists) {
                    out.println("⚠️ Le service '" + logical + "' existe déjà. Utilisez l’option 2 pour mise à jour.");
                    return;
                }
                ServiceRegister.register(logical, fqcn);
                out.println("✅ Ajout dynamique OK : " + logical + " -> " + loaded.getName());
            }

        } catch (Exception e) {
            out.println("❌ Erreur lors du chargement dynamique : " + e.getMessage());
        }
    }

    /** Supprime un service du registre */
    private void removeService(BufferedReader in, PrintWriter out) throws IOException {
        out.println("Nom logique du service à supprimer : "); out.flush();
        String logical = in.readLine();

        if (logical == null || logical.isBlank()) {
            out.println("❌ Nom de service invalide.");
            return;
        }

        boolean removed = ServiceRegister.unregister(logical.trim());
        if (removed) {
            out.println("🟥 Service '" + logical + "' supprimé avec succès !");
        } else {
            out.println("⚠️ Aucun service nommé '" + logical + "' trouvé.");
        }
    }

    @Override
    public String toString() {
        return toStringue();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            out.println("=== BRi - Espace Programmeur ===");
            out.println("Login : "); out.flush();
            String login = in.readLine();
            out.println("Mot de passe : "); out.flush();
            String pass = in.readLine();

            if (login == null || pass == null || login.isBlank() || pass.isBlank()) {
                out.println("❌ Login ou mot de passe manquant.");
                return;
            }

            // Vérification des identifiants
            int numProg = ProgrammeurRegister.getInstance()
                    .getNumProg(login.trim(), pass.trim());
            if (numProg == -1) {
                out.println("❌ Identifiants invalides !");
                return;
            }

            // Ajout URL FTP par défaut si absente
            ServiceRegister.setFTP(login.trim(),
                    "file:///" + System.getProperty("user.dir").replace('\\', '/') + "/classes/");

            out.println("✅ Authentification réussie. Bonjour " + login + " !");
            out.println();

            while (true) {
                out.println("------ MENU PROGRAMMEUR ------");
                out.println("1) Ajouter un service");
                out.println("2) Mettre à jour un service");
                out.println("3) Changer adresse FTP");
                out.println("4) Lister les services");
                out.println("5) Supprimer un service");
                out.println("0) Quitter");
                out.print("Votre choix : 👉 "); out.flush();

                String choix = in.readLine();
                if (choix == null) break;

                switch (choix.trim()) {
                    case "1":
                        addOrUpdate(login.trim(), in, out, false);
                        break;
                    case "2":
                        addOrUpdate(login.trim(), in, out, true);
                        break;
                    case "3":
                        out.println("Nouvelle URL FTP (ex: file:///D:/BRI/classes/ ou ftp://host/path/) : "); out.flush();
                        String url = in.readLine();
                        if (url == null || url.isBlank()) {
                            out.println("⚠️ FTP pas changé.");
                        } else {
                            ServiceRegister.setFTP(login.trim(), url.trim());
                            out.println("✅ FTP mis à jour : " + url.trim());
                        }
                        break;
                    case "4":
                        Set<String> names = serviceNames();
                        String listed = names.isEmpty()
                                ? "(aucun service enregistré)"
                                : names.stream().sorted().collect(Collectors.joining(", "));
                        out.println("📜 Services déclarés : " + listed);
                        break;
                    case "5":
                        removeService(in, out);
                        break;
                    case "0":
                        out.println("👋 Au revoir " + login + " !");
                        return;
                    default:
                        out.println("❌ Choix invalide. Veuillez entrer 0 à 5.");
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Erreur ServiceProgrammeur : " + e.getMessage());
        }
    }
}
