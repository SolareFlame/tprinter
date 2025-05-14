import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MainLoc {
    public static void main (String[] args) throws RemoteException, NotBoundException {
        TableauBlanc tb = new TableauBlanc();
    }
}

