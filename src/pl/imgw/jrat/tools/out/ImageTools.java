/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import pl.imgw.jrat.data.ArrayData;
import pl.imgw.jrat.tools.out.ImageBuilder.PointText;

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
    private boolean caption;
    private double nodata;
    private double nodetected;
    private List<ImageBuilder.PointText> points = new ArrayList<ImageBuilder.PointText>();

    /**
     * @param nodata the nodata to set
     */
    public void setNodata(double nodata) {
        this.nodata = nodata;
    }

    /**
     * @param points the points to set
     */
    public void setPoints(List<ImageBuilder.PointText> points) {
        this.points = points;
    }

    /**
     * @param nodetected the nodetected to set
     */
    public void setNodetected(double nodetected) {
        this.nodetected = nodetected;
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
     * @param caption the hasCaption to set
     */
    public void hasCaption(boolean caption) {
        this.caption = caption;
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
        if (scale == null) {
            throw new IllegalArgumentException("must specify scale");
        }
        
        img = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_ARGB);
        BufferedImage map = new BufferedImage(xSize, ySize,
                BufferedImage.TYPE_INT_ARGB);

        int maskColor = new Color(64, 64, 64, 128).getRGB();
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                int rgb = 0;
                double value = data.getPoint(x, y);
                if(value == nodetected) {
                    rgb = new Color(0, 0, 0, 0).getRGB();
                }
                else if (value == nodata || mask != null && mask.getRawIntPoint(x, y) == 1)
                    rgb = maskColor;
                else {
                    for (MapColor color : scale) {
                        
                        if (value >= color.getValue()) {
//                            System.out.println(value + " a color: " + color.getValue());

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
        
        if(caption) {
            g.drawImage(paintCaption(), 0, 0, null);
        }

        Iterator<PointText> i = points.iterator();
        while(i.hasNext()) {
            paintPointText(g, i.next());
        }
        
    }

    private BufferedImage paintCaption() {
        
        int lenght = scale.size();
        int width = 12;
        int height = 12;
        int i = 0;
        
        BufferedImage caption = new BufferedImage(50, 16*lenght, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = caption.createGraphics();
        Iterator<MapColor> itr = scale.iterator();
        while (itr.hasNext()) {
            MapColor map = itr.next();
            g2d.setColor(map.getColor());
            g2d.fillRect(2, i * (height+ 2) + 10, width, height);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawString(map.getValue() + "", width + 4, i * (height+ 2) + 20);
            i++;
        }
        g2d.dispose();
        
        return caption;
    }
    
    private void paintPointText(Graphics g, PointText pt) {
        
        int factor = 6;
        int dist = 3;
         
        int x = pt.getX();
        int y = pt.getY();
        
        int xText = x;
        int yText = y;
        
        int width = pt.getText().length()*factor + dist;
        int height = pt.getFont().getSize() + dist;
        
        if (x < xSize - width) {
            xText = x + dist;
        } else {
            xText = x - width;
        }

        if (y > height) {
            yText = y - dist;
        } else {
            yText = y + height;
        }
        
        g.setColor(pt.clr);
        g.fillOval(x - 1, y - 1, 3, 3);
        
        g.setFont(pt.getFont());
        g.drawString(pt.getText(), xText, yText);
        
     }
    
}
