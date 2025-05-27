import java.awt.*;
import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Scanner;

public class MainLoc {
    public static void main (String[] args) throws Exception {

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

        System.out.println("Images trouvées :");
        for (int i = 0; i < images.length; i++) {
            System.out.println(i + " - " + images[i].getName());
        }

        System.out.print("Numéro de l'image à utiliser : ");
        int index = Integer.parseInt(scanner.nextLine());

        if (index < 0 || index >= images.length) {
            System.err.println("Numéro invalide.");
            System.exit(1);
        }

        File selectedImage = images[index];

        System.out.print("Taille X souhaitée : ");
        int tailleX = Integer.parseInt(scanner.nextLine());

        System.out.print("Taille Y (laisser vide pour conserver le ratio) : ");
        String tailleYStr = scanner.nextLine();
        int tailleY = tailleYStr.isEmpty() ? 0 : Integer.parseInt(tailleYStr);

        System.out.print("Position X du coin supérieur gauche : ");
        int posX = Integer.parseInt(scanner.nextLine());

        System.out.print("Position Y du coin supérieur gauche : ");
        int posY = Integer.parseInt(scanner.nextLine());

        System.out.print("Qualité (espacement entre pixels, ex: 10) : ");
        int qualite = Integer.parseInt(scanner.nextLine());

        System.out.println("Génération de l'image...");
        ImageTableau it = new ImageTableau(selectedImage, tailleX, tailleY);

        int packetsCount = it.getHeight() * it.getWidth();
        System.out.println("Nombre de paquets : " + packetsCount);

        System.out.println("Etes-vous sûr de vouloir continuer ? (Y/N)");
        String confirmation = scanner.nextLine().toUpperCase();
        if (!confirmation.equals("Y")) {
            System.out.println("Opération annulée.");
            System.exit(0);
        }

        TableauBlanc tb = new TableauBlanc();

        for (int y = 0; y < it.getHeight(); y++) {
            for (int x = 0; x < it.getWidth(); x++) {
                Dessin d = new Dessin();
                int[] pixel = it.getPixel(x, y);

                d.x = posX*10 + x*qualite;
                d.y = posY*10 + y*qualite;

                d.c = new Color(pixel[0], pixel[1], pixel[2]);

                tb.afficherMessage(d);
            }
        }
    }
}

