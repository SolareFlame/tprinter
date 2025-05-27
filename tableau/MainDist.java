import java.awt.*;
import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class MainDist {
    public static void main (String[] args) throws Exception {

        // shh charlemagne.iutnc.univ-lorraine.fr
        String ip = "localhost";
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


        Scanner scanner = new Scanner(System.in);

        File imageDir = new File("img");
        File[] images = imageDir.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".png") ||
                        name.toLowerCase().endsWith(".jpg") ||
                        name.toLowerCase().endsWith(".jpeg")
        );

        if (images == null || images.length == 0) {
            System.err.println("Aucune image trouvée dans le dossier 'img'.");
            System.exit(1);
        }


        while (true) {
            System.out.println("Images trouvées :");
            for (int i = 0; i < images.length; i++) {
                System.out.println(i + " - " + images[i].getName());
            }

            System.out.print("Numéro de l'image à utiliser (ou 'exit' pour quitter) : ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;

            int index;
            try {
                index = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide.");
                continue;
            }

            if (index < 0 || index >= images.length) {
                System.err.println("Numéro invalide.");
                continue;
            }

            File selectedImage = images[index];

            try {
                System.out.print("Taille X souhaitée : ");
                int tailleX = Integer.parseInt(scanner.nextLine());

                System.out.print("Taille Y (laisser vide pour conserver le ratio) : ");
                String tailleYStr = scanner.nextLine();
                int tailleY = tailleYStr.isEmpty() ? 0 : Integer.parseInt(tailleYStr);

                System.out.print("Position X du coin supérieur gauche : ");
                String posXStr = scanner.nextLine();
                int posX = posXStr.isEmpty() ? 0 : Integer.parseInt(posXStr);

                System.out.print("Position Y du coin supérieur gauche : ");
                String posYStr = scanner.nextLine();
                int posY = posYStr.isEmpty() ? 0 : Integer.parseInt(posYStr);

                System.out.print("Qualité (espacement entre pixels, base: 10) : ");
                int qualite = Integer.parseInt(scanner.nextLine());

                System.out.println("Génération de l'image...");
                ImageTableau it = new ImageTableau(selectedImage, tailleX, tailleY);

                int packetsCount = it.getHeight() * it.getWidth();
                System.out.println("Nombre de paquets : " + packetsCount);

                System.out.println("Etes-vous sûr de vouloir continuer ? (Y/N)");
                String confirmation = scanner.nextLine().toUpperCase();
                if (!confirmation.equals("Y")) {
                    System.out.println("Opération annulée.");
                    continue;
                }

                for (int y = 0; y < it.getHeight(); y++) {
                    for (int x = 0; x < it.getWidth(); x++) {
                        Dessin d = new Dessin();
                        int[] pixel = it.getPixel(x, y);

                        d.x = posX * 10 + x * qualite;
                        d.y = posY * 10 + y * qualite;

                        d.c = new Color(pixel[0], pixel[1], pixel[2]);

                        tb.afficherMessage(d);
                    }
                }

                System.out.println("Image print en local, voulez-vous l'imprimer sur le service global ? (Y/N)");
                String confirmation2 = scanner.nextLine().toUpperCase();

                if (confirmation2.equals("Y")) {
                    System.out.println("Envoi de l'image au service global...");

                    for (int y = 0; y < it.getHeight(); y++) {
                        for (int x = 0; x < it.getWidth(); x++) {
                            Dessin d = new Dessin();
                            int[] pixel = it.getPixel(x, y);

                            d.x = posX * 10 + x * qualite;
                            d.y = posY * 10 + y * qualite;

                            d.c = new Color(pixel[0], pixel[1], pixel[2]);

                            sc.distribuerMessage(d);
                        }
                    }
                } else {
                    System.out.println("Opération annulée.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide. Veuillez réessayer.");
            } catch (Exception e) {
                System.out.println("Erreur pendant l'opération : " + e.getMessage());
            }

            System.out.println("Voulez-vous afficher une autre image ? (Y/N)");
            String loop = scanner.nextLine().toUpperCase();
            if (!loop.equals("Y")) System.exit(0);
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

