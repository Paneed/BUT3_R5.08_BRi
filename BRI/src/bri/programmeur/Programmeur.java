package bri.programmeur;

import java.util.*;

public class Programmeur {
    private final String login;
    private final String password;
    private String adresseFtp;
    private final List<Class<?>> servicesClasses;

    public Programmeur(String login, String password, String adresse) {
        this.login = login;
        this.password = password;
        this.adresseFtp = adresse;
        this.servicesClasses = Collections.synchronizedList(new ArrayList<>());
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void addServiceClass(Class<?> serviceClass) {
        servicesClasses.add(serviceClass);
    }

    @Override
    public String toString() {
        synchronized (servicesClasses) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < servicesClasses.size(); i++) {
                str.append(i + 1)
                        .append(" ")
                        .append(servicesClasses.get(i).getName())
                        .append(",\n");
            }
            return str.toString();
        }
    }
}
