import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Distributeur implements ServiceDistributeur {

    private List<ServiceTableauBlanc> clients;
    private TableauBlanc tb;

    public Distributeur() {
        clients = new ArrayList<>();
        tb = new TableauBlanc();
    }

    @Override
    public void distribuerMessage(Dessin dessin) throws RemoteException {
        tb.afficherMessage(dessin);

        for (ServiceTableauBlanc client : clients) {
            try {
                client.afficherMessage(dessin);
            } catch (RemoteException e) {
                clients.remove(client);
                System.err.println("Erreur lors de l'envoi du message au client : " + e.getMessage());
            }
        }
    }

    @Override
    public void enregistrerClient(ServiceTableauBlanc client) throws RemoteException {
        if (!clients.contains(client)) clients.add(client);
    }
}

