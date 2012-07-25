/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

import pl.imgw.jrat.data.ArrayData;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ImageTools {

    private String description;
    private Set<MapColor> scale;
    private BufferedImage background;
    private BufferedImage foreground;
    private int transparency;
    private ArrayData data;
    private ArrayData mask;
    private BufferedImage img;
    private int ySize = 640;
    private int xSize = 480;
    private boolean darker;
    private double nodata;

    /**
     * @param nodata the nodata to set
     */
    public void setNodata(double nodata) {
        this.nodata = nodata;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param scale
     *            the scale to set
     */
    public void setScale(Set<MapColor> scale) {
        this.scale = scale;
    }

    /**
     * @param background
     *            the background to set
     */
    public void setBackground(File background) {
        BufferedImage image = null;
        if (background != null && background.isFile()) {
            try {
                image = ImageIO.read(background);
            } catch (IOException e) {

            }
        }
        this.background = image;
    }

    public void setDarker(boolean darker) {
        this.darker = darker;
    }

    /**
     * @param foreground
     *            the foreground to set
     */
    public void setForeground(File foreground) {
        BufferedImage image = null;
        if (foreground != null && foreground.isFile()) {
            try {
                image = ImageIO.read(foreground);
            } catch (IOException e) {

            }
        }
        this.foreground = image;
    }

    public void setTransparency(int transparency) {
        if (transparency > 255 || transparency < 0)
            this.transparency = 128;
        this.transparency = transparency;
    }

    /**
     * @param data
     */
    public void setData(ArrayData data) {
        if (data != null) {
            this.xSize = data.getSizeX();
            this.ySize = data.getSizeY();
            this.data = data;
        }
    }

    public void setMask(ArrayData mask) {
        this.mask = mask;
    }

    /**
     * @return the img
     */
    public BufferedImage getImg() {
        return img;
    }

    /**
     * @param xSize
     * @param ySize
     */
    public void setSize(int xSize, int ySize) {
        if (xSize > 0)
            this.xSize = xSize;
        if (ySize > 0)
            this.ySize = ySize;

    }

    /**
     * 
     */
    public void paint() {
        if (data == null) {
            throw new IllegalArgumentException("must specify data");
        }

        img = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_ARGB);
        BufferedImage map = new BufferedImage(xSize, ySize,
                BufferedImage.TYPE_INT_ARGB);

        int maskColor = new Color(64, 64, 64, 128).getRGB();
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                int rgb = 0;
                double value = data.getPoint(x, y);
                if (value == nodata || mask != null && mask.getRawIntPoint(x, y) == 1)
                    rgb = maskColor;
                else {
                    for (MapColor color : scale) {
                        if (value >= color.getValue()) {

                            int r = color.getColor().getRed();
                            int g = color.getColor().getGreen();
                            int b = color.getColor().getBlue();
                            Color clr = new Color(r, g, b, transparency);

                            // Color clr = color.getColor();
                            if (darker) {
                                clr = clr.darker();
                            }
                            rgb = clr.getRGB();

                        }
                    }
                }
                map.setRGB(x, y, rgb);
            }
        }

        Graphics g = img.getGraphics();

        // background
        if (background != null) {
            g.drawImage(background, 0, 0, null);
        }
        // map
        g.drawImage(map, 0, 0, null);
        // foreground
        if (foreground != null) {
            g.drawImage(foreground, 0, 0, null);
        }

    }

}
