package bri.programmeur;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProgrammeurRegister {

    private static final ProgrammeurRegister INSTANCE = new ProgrammeurRegister();
    private final List<Programmeur> listeProgs;

    private ProgrammeurRegister() {
        listeProgs = new CopyOnWriteArrayList<>();
    }

    public static ProgrammeurRegister getInstance() {
        return INSTANCE;
    }

    public void addProg(Programmeur prog) {
        listeProgs.add(prog);
    }

    public int getNumProg(String login, String password){
        for (int i = 0; i < listeProgs.size(); i++){
            Programmeur p = listeProgs.get(i);
            if(p.getLogin().equals(login) && p.getPassword().equals(password)){
                return i;
            }
        }
        return -1;
    }

    public List<Programmeur> getAllProgs() {
        return List.copyOf(listeProgs);
    }
}
