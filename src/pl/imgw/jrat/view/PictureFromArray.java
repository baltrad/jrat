/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.view;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Set;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class PictureFromArray {

    int width;
    int height;
    private BufferedImage img;

    public PictureFromArray(int[][] array, Set<MapColor> colors, int gridSize) {

        width = array.length;
        height = array[0].length;

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int rgb = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (MapColor color : colors) {
                    if (array[i][j] >= color.getValue()) {
                        rgb = color.getColor().getRGB();
                    }
                }

                if (array[i][j] == 0
                        && (i % gridSize == 0 || j % gridSize == 0)) {
                    rgb = Color.GRAY.getRGB();
                }
                img.setRGB(i, j, rgb);
                rgb = 0;

            }
        }
    }

    public PictureFromArray(int[][] array, Set<MapColor> colors) {

        width = array.length;
        height = array[0].length;

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int rgb = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (MapColor color : colors) {
                    if (array[i][j] >= color.getValue()) {
                        rgb = color.getColor().getRGB();
                    }
                }
                img.setRGB(i, j, rgb);
                rgb = 0;

            }
        }
    }

    public PictureFromArray(double[][] array, Set<MapColor> colors,
            BufferedImage image, int nodata, int undetect) {

        width = array.length;
        height = array[0].length;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int rgb = 0;

        int darkGray = new Color(150, 150, 150).getRGB();
        int lightGray = new Color(200, 200, 200).getRGB();
        int borderColor = new Color(62, 62, 62).getRGB();
        
        

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
//                System.out.print(array[i][j] + " ");
                if (array[i][j] > undetect) {
                    for (MapColor color : colors) {
                        if (array[i][j] > color.getValue()) {
                            rgb = color.getColor().getRGB();
                        }
                    }
                    img.setRGB(i, j, rgb);

                } else if (array[i][j] == nodata) {
                    img.setRGB(i, j, darkGray);
                } else {
                    img.setRGB(i, j, lightGray);
                }

                if (image.getRGB(i, j) == borderColor && (array[i][j] < 91 || array[i][j] > 253))
                    img.setRGB(i, j, borderColor);
            }
        }

        
    }
    public PictureFromArray(int[][] array, Set<MapColor> colors,
            BufferedImage image, boolean test) {
        
        width = array.length;
        height = array[0].length;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int rgb = 0;
        
        int background = new Color(236, 233, 216).getRGB();

        
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (array[i][j] > 0 && image.getRGB(i, j) == background) {
                    for (MapColor color : colors) {
                        if (array[i][j] > color.getValue()) {
                            rgb = color.getColor().getRGB();
                        }
                    }
                    img.setRGB(i, j, rgb);
                    
                } else {
                    img.setRGB(i, j, image.getRGB(i, j));
                    
//                    if (image.getRGB(i, j) == -12698050)
//                        img.setRGB(i, j, borderColor);
//                    else if (array[i][j] >= -8888000) {
//                        img.setRGB(i, j, lightGray);
//                    } else
//                        img.setRGB(i, j, darkGray);
                }
            }
        }
        
        
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the img
     */
    public BufferedImage getImg() {
        return img;
    }

}
