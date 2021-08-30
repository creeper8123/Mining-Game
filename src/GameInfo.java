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
        public static final Color nullColor = new Color(255, 82, 255, 255); //Use for green 32 in GIMP, for some reason it comes out as 82. Continue to use 32 in GIMP

        public static Image getImage(String location, boolean randomOrientation, boolean getRaw){
            try{
                if(!getRaw){
                    if(randomOrientation){
                        int rotation = (int) (Math.floor(Game.WORLD_RANDOM.nextDouble()*3)*90); //Randomly calculate a rotation in 90 degree increments
                        if(rotation != 0){
                            return resizeImage(4, rotateImageRandomly(new ImageIcon(Objects.requireNonNull(GameInfo.class.getClassLoader().getResource(location))).getImage(), rotation));
                        }else{
                            return resizeImage(4, new ImageIcon(Objects.requireNonNull(GameInfo.class.getClassLoader().getResource(location))).getImage());
                        }
                    }
                    return resizeImage(4, new ImageIcon(Objects.requireNonNull(GameInfo.class.getClassLoader().getResource(location))).getImage());
                }else{
                    if(randomOrientation){
                        int rotation = (int) (Math.floor(Game.WORLD_RANDOM.nextDouble()*3)*90); //Randomly calculate a rotation in 90 degree increments
                        if(rotation != 0){
                            return rotateImageRandomly(new ImageIcon(Objects.requireNonNull(GameInfo.class.getClassLoader().getResource(location))).getImage(), rotation);
                        }else{
                            return new ImageIcon(Objects.requireNonNull(GameInfo.class.getClassLoader().getResource(location))).getImage();
                        }
                    }
                    return new ImageIcon(Objects.requireNonNull(GameInfo.class.getClassLoader().getResource(location))).getImage();
                }
            }catch(NullPointerException e){
                System.out.println("Warning! Texture file \"" + location + "\" not found! Defaulting to missing texture!");
                return new ImageIcon(Objects.requireNonNull(GameInfo.class.getClassLoader().getResource("textures/missingTexture.png"))).getImage();
            }
        }

        public static Image resizeImage(int rescaleFactor, Image inputImage){
            return new ImageIcon(inputImage.getScaledInstance(inputImage.getWidth(null)*rescaleFactor, inputImage.getHeight(null)*rescaleFactor, Image.SCALE_DEFAULT)).getImage(); //Has to be done like this, otherwise the width and height are -1
        }

        public static Image changeBrightness(Image inputImage, double modifier){
            BufferedImage oldImage = new BufferedImage(inputImage.getWidth(null), inputImage.getHeight(null), 6);
            Graphics2D g2d = oldImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, null);
            g2d.dispose();

            BufferedImage newImage = new BufferedImage(Tile.TILE_WIDTH, Tile.TILE_HEIGHT, 6);
            int a;// red component 0...255
            int r;// = 255;// red component 0...255
            int g;// = 0;// green component 0...255
            int b;// = 0;// blue component 0...255
            int col;// = ((a&0x0ff)<<24)|((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
            for (int x = 0; x < oldImage.getWidth(); x++) {
                for (int y = 0; y < oldImage.getHeight(); y++) {
                    Color myColour = new Color(oldImage.getRGB(x, y));
                    a = myColour.getAlpha();
                    r = (int) (myColour.getRed() * modifier);
                    g = (int) (myColour.getGreen() * modifier);
                    b = (int) (myColour.getBlue() * modifier);
                    col = ((a&0x0ff)<<24)|((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
                    newImage.setRGB(x, y, col);
                }
            }
            return newImage;
        }

        public static Image rotateImageRandomly(Image inputImage, int rotation){
            //Convert the image from an Image object to a BufferedImage object
            BufferedImage oldImage = new BufferedImage(inputImage.getWidth(null), inputImage.getHeight(null), 6);
            Graphics2D g2d = oldImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, null);
            g2d.dispose();

            //From here down is copied code. Source is https://blog.idrsolutions.com/2019/05/image-rotation-in-java/
            final double rads = Math.toRadians(rotation);

            final double sin = Math.abs(Math.sin(rads));
            final double cos = Math.abs(Math.cos(rads));
            final int w = (int) Math.floor(oldImage.getWidth() * cos + oldImage.getHeight() * sin);
            final int h = (int) Math.floor(oldImage.getHeight() * cos + oldImage.getWidth() * sin);
            final BufferedImage rotatedImage = new BufferedImage(w, h, oldImage.getType());
            final AffineTransform at = new AffineTransform();
            at.translate((double) w / 2, (double) h / 2);
            at.rotate(rads,0, 0);
            at.translate((double) -oldImage.getWidth() / 2, (double) -oldImage.getHeight() / 2);
            final AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            rotateOp.filter(oldImage,rotatedImage);

            return rotatedImage;
        }

        //TODO: 2 parameters, 1st is base image, 2nd is image to overlay onto the first
        public static Image overlayImage(Image baseImage, Image imageOverlay){
            //Convert the images from an Image object to a BufferedImage object
            BufferedImage oldBaseImage = new BufferedImage(baseImage.getWidth(null), baseImage.getHeight(null), 6);
            Graphics2D g2d = oldBaseImage.createGraphics();
            g2d.drawImage(baseImage, 0, 0, null);
            g2d.dispose();

            BufferedImage oldOverlayImage = new BufferedImage(imageOverlay.getWidth(null), imageOverlay.getHeight(null), 6);
            Graphics2D g2dB = oldOverlayImage.createGraphics();
            g2dB.drawImage(imageOverlay, 0, 0, null);
            g2dB.dispose();

            BufferedImage newImage = new BufferedImage(oldBaseImage.getWidth(), oldBaseImage.getHeight(), 6);

            int aB;// red component 0...255
            int rB;// = 255;// red component 0...255
            int gB;// = 0;// green component 0...255
            int bB;// = 0;// blue component 0...255

            int aO;// red component 0...255
            int rO;// = 255;// red component 0...255
            int gO;// = 0;// green component 0...255
            int bO;// = 0;// blue component 0...255

            int col;// = ((a&0x0ff)<<24)|((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
            for (int x = 0; x < oldBaseImage.getWidth(); x++) {
                for (int y = 0; y < oldBaseImage.getHeight(); y++) {
                    Color myColour;
                    myColour = new Color(oldOverlayImage.getRGB(x, y));
                    aO = myColour.getAlpha();
                    rO = myColour.getRed();
                    gO = myColour.getGreen();
                    bO = myColour.getBlue();
                    if(rO == nullColor.getRed() && gO == nullColor.getGreen() && bO == nullColor.getBlue()){
                        myColour = new Color(oldBaseImage.getRGB(x, y));
                        aB = myColour.getAlpha();
                        rB = myColour.getRed();
                        gB = myColour.getGreen();
                        bB = myColour.getBlue();
                        col = ((aB&0x0ff)<<24)|((rB&0x0ff)<<16)|((gB&0x0ff)<<8)|(bB&0x0ff);
                    }else{
                        col = ((aO&0x0ff)<<24)|((rO&0x0ff)<<16)|((gO&0x0ff)<<8)|(bO&0x0ff);
                    }
                    newImage.setRGB(x, y, col);
                }
            }
            return newImage;
        }
    }

    public static class LevelScrolling{

        public static int scrollLevelX = 0;
        public static int scrollLevelY = 0;
        public static final int SCROLL_AMOUNT = 1;


        //TODO: Rework this function to prevent world holes
        public static void ScrollLevelRight(Tile.TileType[][] tiles, Tile.TileGraphics[][] graphics){
            scrollLevelX++;
            for (int i = 0; i < graphics.length; i++) {
                for (int j = 0; j < graphics[i].length; j++) {
                    graphics[i][j].updateTilePosition(graphics[i][j].x -= 64, graphics[i][j].y);
                }
            }

            for (int i = 0; i < graphics[0].length; i++) {
                graphics[scrollLevelX % graphics.length][i].regenerateTile(tiles[scrollLevelX + graphics.length][i]);
                graphics[scrollLevelX % graphics.length][i].updateTilePosition(64 * (graphics.length - 1), graphics[scrollLevelX % graphics.length][i].y);
                if(tiles[scrollLevelX + graphics.length][i].image == null){
                    graphics[scrollLevelX % graphics.length][i].texture.setIcon(null);
                }else {
                    if(graphics[scrollLevelX % graphics.length][i].tileType.isBroken){
                        graphics[scrollLevelX % graphics.length][i].updateTexture(tiles[scrollLevelX + graphics.length][i].imageBackground);
                    }else{
                        graphics[scrollLevelX % graphics.length][i].updateTexture(tiles[scrollLevelX + graphics.length][i].image);
                    }
                }
                graphics[scrollLevelX % graphics.length][i].updateTexture();
            }
        }

        public static void ScrollLevelLeft(Tile.TileType[][] tiles, Tile.TileGraphics[][] graphics){
            scrollLevelX--;
            for (int i = 0; i < graphics.length; i++) {
                for (int j = 0; j < graphics[i].length; j++) {
                    //graphics[i][j].updateTilePosition(graphics[i][j].x += 64, graphics[i][j].y);
                }
            }

            for (int i = 0; i < graphics[graphics.length-1].length; i++) {
                //graphics[scrollLevelX % graphics.length][i].regenerateTile(tiles[scrollLevelX + graphics.length][i]);
            }
        }


        //NOT-WORKING CORRECTLY
        public static void ScrollLevelUp(Tile.TileType[][] tiles, Tile.TileGraphics[][] graphics){
            scrollLevelY++;
            for (int i = 0; i < graphics.length; i++) {
                for (int j = 0; j < graphics[i].length; j++) {
                    graphics[i][j].updateTilePosition(graphics[i][j].x, graphics[i][j].y += 64);
                }
            }

            for (int i = 0; i < graphics.length; i++) {
                graphics[i][scrollLevelY % graphics.length].regenerateTile(tiles[i][scrollLevelY]);
                graphics[i][scrollLevelY % graphics.length].updateTilePosition(graphics[i][scrollLevelY % graphics.length].x, 0);
                if(tiles[i][scrollLevelY].image == null){
                    graphics[i][scrollLevelY % graphics.length].texture.setIcon(null);
                }else {
                    if(graphics[i][scrollLevelY % graphics.length].tileType.isBroken){
                        graphics[i][scrollLevelY % graphics.length].updateTexture(tiles[i][scrollLevelY].imageBackground);
                    }else{
                        graphics[i][scrollLevelY % graphics.length].updateTexture(tiles[i][scrollLevelY].image);
                    }
                }
                graphics[i][scrollLevelY % graphics.length].updateTexture();
            }
        }
    }
}