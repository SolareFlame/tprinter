import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LancerDistributeur {
    public static void main(String[] args) throws RemoteException {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);

            ServiceDistributeur distributeur = new Distributeur();
            ServiceDistributeur stub = (ServiceDistributeur) UnicastRemoteObject.exportObject(distributeur, 0);

            registry.bind("distributeur", stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
