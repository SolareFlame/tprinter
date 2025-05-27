import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageTableau {

    private int[][][] pixels; // pixels[x][y][0=R, 1=G, 2=B]
    private int width;
    private int height;

    public ImageTableau(File imageFile, int targetWidth, int targetHeight) throws Exception {
        BufferedImage original = ImageIO.read(imageFile);

        if (targetHeight == 0) {
            double ratio = (double) original.getHeight() / original.getWidth();
            targetHeight = (int) (targetWidth * ratio);
        }

        this.width = targetWidth;
        this.height = targetHeight;

        BufferedImage resized = resizeImage(original, width, height);
        this.pixels = extractPixels(resized);
    }

    private BufferedImage resizeImage(BufferedImage original, int width, int height) {
        Image tmp = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resized;
    }

    private int[][][] extractPixels(BufferedImage img) {
        int[][][] data = new int[width][height][3];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = img.getRGB(x, y);
                data[x][y][0] = (rgb >> 16) & 0xFF;
                data[x][y][1] = (rgb >> 8) & 0xFF;
                data[x][y][2] = rgb & 0xFF;
            }
        }
        return data;
    }

    public int[][][] getPixels() {
        return pixels;
    }

    public int[] getPixel(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds");
        }
        return pixels[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
