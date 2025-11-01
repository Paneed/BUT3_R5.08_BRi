package bri.services;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BRIRegister
 * ------------------------------
 * Classe responsable du registre global des services BRi.
 * - Gère l’enregistrement, la mise à jour et la suppression de services.
 * - Conserve les URLs FTP associées aux programmeurs.
 */
public final class ServiceRegister {
        private static final Map<String, String> SERVICES = new ConcurrentHashMap<>();

        private static final Map<String, String> FTP_BY_USER = new ConcurrentHashMap<>();

        private ServiceRegister() {}

        public static void register(String logicalName, String fqcn) {
            if (logicalName == null || logicalName.isBlank() ||
                    fqcn == null || fqcn.isBlank()) {
                throw new IllegalArgumentException("Nom logique ou FQCN vide !");
            }
            SERVICES.put(logicalName.trim(), fqcn.trim());
        }

        public static boolean unregister(String logicalName) {
            if (logicalName == null || logicalName.isBlank()) return false;
            return SERVICES.remove(logicalName.trim()) != null;
        }

        public static String resolve(String logicalName) {
            if (logicalName == null || logicalName.isBlank()) return null;
            return SERVICES.get(logicalName.trim());
        }

        public static Set<String> serviceNames() {
            return new TreeSet<>(SERVICES.keySet());
        }

        public static void setFTP(String login, String ftpUrl) {
            if (login == null || ftpUrl == null || ftpUrl.isBlank()) return;
            FTP_BY_USER.put(login.trim(), ftpUrl.trim());
        }

        public static String getFTP(String login) {
            return FTP_BY_USER.get(login.trim());
        }

        public static Map<String, String> getAllFTP() {
            return Map.copyOf(FTP_BY_USER);
        }

        public static void clearAll() {
            SERVICES.clear();
            FTP_BY_USER.clear();
        }
    }