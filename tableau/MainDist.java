import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MainDist {
    public static void main (String[] args) throws RemoteException, NotBoundException {

        // shh charlemagne.iutnc.univ-lorraine.fr
        String ip = "100.64.80.224";
        String port = "1099";
        String[] host = {ip, port};

        String[] services_args = getReg(host).list();
        for (String s : services_args) {
            System.out.println(s);
        }

        TableauBlanc tb = new TableauBlanc();
        ServiceTableauBlanc stbl = (ServiceTableauBlanc) UnicastRemoteObject.exportObject(tb, 0);

        ServiceDistributeur sc = (ServiceDistributeur) getReg(host).lookup("distributeur");
        sc.enregistrerClient(stbl);

        tb.setServiceDistributeur(sc);

        for(int i = 0; i < 75; i++) {
            for(int j = 0; j < 75; j++) {
                Dessin d = new Dessin();
                d.x = i*10;
                d.y = j*10;
                d.c = Color.WHITE;
                sc.distribuerMessage(d);
            }
        }



    }

    /**
     * @param args IP + HOST (optional)
     * @return REG
     * @throws RemoteException
     */
    public static Registry getReg(String[] args) throws RemoteException {
        if (args.length < 1 || args[0].isEmpty()) {
            System.err.println("Host non fourni");
            System.exit(1);
        }

        String host = args[0];

        String port = "1099";
        if (args.length > 1 && args[1] != null && !args[1].isEmpty()) {
            port = args[1];
        }

        try {
            int portNumber = Integer.parseInt(port);
            Registry reg = LocateRegistry.getRegistry(host, portNumber);
            return reg;
        } catch (NumberFormatException e) {
            System.err.println("Port invalide: " + port);
            System.exit(1);
        }

        return null;
    }

}

