package bri.appli;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Charge dynamiquement des classes de service BRi à partir d'une URL (FTP ou file://).
 * Utilisé pour permettre le rechargement à chaud (mise à jour dynamique d'un service).
 */
public class DynamicServiceLoader {

    /**
     * Charge dynamiquement une classe à partir d'une URL donnée (FTP ou dossier local).
     *
     * @param fqcn   Nom complet de la classe à charger (Fully Qualified Class Name)
     *               ex: "phuong.ServiceInversion"
     * @param ftpUrl URL du dossier où se trouvent les .class
     *               ex: "ftp://localhost/classes/" ou "file:///D:/BUT3/BRI/classes/"
     * @return La classe chargée dynamiquement
     * @throws Exception si le chargement échoue
     */
    public static Class<?> loadClass(String fqcn, String ftpUrl) throws Exception {
        if (fqcn == null || fqcn.isBlank()) {
            throw new IllegalArgumentException("Nom de classe vide");
        }
        if (ftpUrl == null || ftpUrl.isBlank()) {
            throw new IllegalArgumentException("URL FTP vide");
        }

        if (ftpUrl.startsWith("ftp://")) {
            ftpUrl = ftpUrl.replace("ftp://", "file://");
        }

        if (!ftpUrl.endsWith("/")) {
            ftpUrl += "/";
        }

        URL[] urls = { new URL(ftpUrl) };

        URLClassLoader loader = new URLClassLoader(urls);

        Class<?> clazz = Class.forName(fqcn, true, loader);

        System.out.println("🔄 Classe chargée dynamiquement : " + fqcn + " depuis " + ftpUrl);
        return clazz;
    }
}

