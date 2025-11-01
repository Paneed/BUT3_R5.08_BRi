package service_utilisateurs.phuong;

import bri.services.BRIService;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.Socket;
import java.net.URL;

public class ServiceAnalyse implements BRIService {
    private final Socket socket;

    public ServiceAnalyse(Socket socket) {
        this.socket = socket;
    }

    public static String toStringue() {
        return "ServiceAnalyse : analyse un fichier XML à partir d'une URL";
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            out.println("=== Service Analyse 👀 XML ===");
            out.println("Envoyez l'URL d’un fichier XML à analyser.");
            out.println("Tapez END pour revenir au menu.");

            while (true) {
                out.print("URL > "); out.flush();
                String ftpUrl = in.readLine();

                if (ftpUrl == null || ftpUrl.equalsIgnoreCase("END")) {
                    out.println("--- Fin du service Analyse ---");
                    break;
                }

                if (ftpUrl.isBlank()) {
                    out.println("❗ URL vide. Réessayez ou tapez END pour quitter.");
                    continue;
                }

                try {
                    URL url = new URL(ftpUrl);
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(url.openStream());
                    doc.getDocumentElement().normalize();

                    String racine = doc.getDocumentElement().getNodeName();
                    int nbBalises = doc.getElementsByTagName("*").getLength();

                    out.println("=== Rapport XML ===");
                    out.println("✅ Racine : " + racine);
                    out.println("✅ Nombre total de balises : " + nbBalises);
                    out.println("(Envoyez une autre URL ou END pour quitter)");
                } catch (Exception e) {
                    out.println("❌ Erreur lors de l’analyse du fichier XML : " + e.getMessage());
                    out.println("(Vérifiez l’URL ou tapez END pour quitter)");
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur ServiceAnalyse : " + e.getMessage());
        }
        // ⚠️ Ne pas fermer la socket : ServiceAmateur la réutilise
    }

    @Override
    public String toString() {
        return toStringue();
    }
}
