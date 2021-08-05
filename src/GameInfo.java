import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Objects;

public class GameInfo {
    public static class GraphicsInfo{
        public static final int BACKGROUND_LAYER = 0;
        public static final int TILE_LAYER = 1;
        public static final int SPRITE_LAYER = 2;
        public static final int LIGHTING_LAYER = 3;

        public static class TileInfo{
            public static final int BACKGROUND_LAYER = 0;
            public static final int DIMMER_LAYER = 1;
            public static final int FOREGROUND_LAYER = 2;
        }
    }

    public static BufferedImage mergeImages(int width, int height, BufferedImage[] imagesList){
        BufferedImage finalImage = new BufferedImage(width, height,6);

        double r = 0;
        double g = 0;
        double b = 0;
        double a = 0;

        for (int x = 0; x < finalImage.getWidth(); x++) {
            for (int y = 0; y < finalImage.getHeight(); y++) {
                for (BufferedImage bufferedImage : imagesList) {
                    Color pixelColor = new Color(bufferedImage.getRGB(x, y));
                    r += pixelColor.getRed()/imagesList.length;
                    g += pixelColor.getGreen()/imagesList.length;
                    b += pixelColor.getBlue()/imagesList.length;
                    a += pixelColor.getAlpha()/imagesList.length;
                }
                int intR = (int) r;
                int intG = (int) g;
                int intB = (int) b;
                int intA = (int) a;

                int finalColour = ((intA&0x0ff)<<24)|((intR&0x0ff)<<16)|((intG&0x0ff)<<8)|(intB&0x0ff);
                finalImage.setRGB(x, y, finalColour);
            }
        }

        return finalImage;
    }

    public static ImageIcon getImage(String location){
        URL texture = ImageIO.class.getResource(location);
        assert texture != null;
        return new ImageIcon(Objects.requireNonNull(GameInfo.class.getClassLoader().getResource(location)));
    }
}
