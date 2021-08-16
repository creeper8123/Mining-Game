import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.Objects;

public class GameInfo {

    public static final int UPDATE_RATE_IN_MILLIS = 4;
    public static final int TILE_LAYER = 0;
    public static final int SPRITE_LAYER = 1;

    public static class ImageProcessing{

        public static Image getImage(String location, boolean randomOrientation){
            try{
                if(randomOrientation){
                    return resizeImage(4, rotateImageRandomly(new ImageIcon(Objects.requireNonNull(GameInfo.class.getClassLoader().getResource(location))).getImage()));
                }
                return resizeImage(4, new ImageIcon(Objects.requireNonNull(GameInfo.class.getClassLoader().getResource(location))).getImage());
            }catch(NullPointerException e){
                System.out.println("Warning! Texture file not found! Defaulting to missing texture!");
                return new ImageIcon(Objects.requireNonNull(GameInfo.class.getClassLoader().getResource("textures/missingTexture.png"))).getImage();
            }
        }

        public static Image resizeImage(int rescaleFactor, Image inputImage){
            /*
            BufferedImage oldImage = new BufferedImage(inputImage.getWidth(null), inputImage.getHeight(null), 6);
            Graphics2D g2d = oldImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, null);
            g2d.dispose();
             */
            return new ImageIcon(inputImage.getScaledInstance(inputImage.getWidth(null)*rescaleFactor, inputImage.getHeight(null)*rescaleFactor, Image.SCALE_DEFAULT)).getImage(); //Has to be done like this, otherwise the width and height are -1
        }

        public static Image rotateImageRandomly(Image inputImage){
            BufferedImage oldImage = new BufferedImage(inputImage.getWidth(null), inputImage.getHeight(null), 6);
            Graphics2D g2d = oldImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, null);
            g2d.dispose();

            //From here down is copied code. Source is https://blog.idrsolutions.com/2019/05/image-rotation-in-java/
            final double rads = Math.toRadians(Math.floor(Math.random()*3)*90);
            final double sin = Math.abs(Math.sin(rads));
            final double cos = Math.abs(Math.cos(rads));
            final int w = (int) Math.floor(oldImage.getWidth() * cos + oldImage.getHeight() * sin);
            final int h = (int) Math.floor(oldImage.getHeight() * cos + oldImage.getWidth() * sin);
            final BufferedImage rotatedImage = new BufferedImage(w, h, oldImage.getType());
            final AffineTransform at = new AffineTransform();
            at.translate(w / 2, h / 2);
            at.rotate(rads,0, 0);
            at.translate(-oldImage.getWidth() / 2, -oldImage.getHeight() / 2);
            final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            rotateOp.filter(oldImage,rotatedImage);

            return rotatedImage;
        }
    }
}
