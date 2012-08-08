/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

import pl.imgw.jrat.data.ArrayData;
import pl.imgw.jrat.data.RainbowData;
import pl.imgw.jrat.data.parsers.OdimH5Parser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.data.parsers.RainbowImageParser;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ImageBuilder {

    private String description = "";
    private Set<MapColor> scale = null;
    private File background = null;
    private File foreground = null;
    private int transparency = 255;
    private boolean darker = false;
    private boolean caption = false;
    private ArrayData mask = null;
    private ArrayData data = null;
    private int xSize = 0;
    private int ySize = 0;
    private double nodata = 999999;
    private double nodetected = 999999;
    private String format = "PNG";

    public void saveToFile(File file) {
        try {
            ImageIO.write(create(), format, file);
            LogHandler.getLogs().displayMsg(
                    "Saving image to file: '" + file.getCanonicalFile() + "' complete",
                    LogsType.WARNING);
        } catch (Exception e) {
            LogHandler.getLogs().displayMsg(
                    "Saving image to file: '" + file + "' failed",
                    LogsType.WARNING);
        }
    }
    
    public ImageBuilder setData(ArrayData data) {
        this.data = data;
        return this;
    }
    
    public ImageBuilder setFormat(String format) {
        this.format = format;
        return this;
    }

    public ImageBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ImageBuilder setScale(Set<MapColor> scale) {
        this.scale = scale;
        return this;
    }

    public ImageBuilder setBackground(File background) {
        this.background = background;
        return this;
    }

    public ImageBuilder setForeground(File foreground) {
        this.foreground = foreground;
        return this;
    }

    public ImageBuilder setNoDataValue(double nodata) {
        this.nodata = nodata;
        return this;
    }

    public ImageBuilder setNoDetectedValue(double nodetected) {
        this.nodetected = nodetected;
        return this;
    }
    
    /**
     * 0-256
     * 
     * @param transparency
     * @return
     */
    public ImageBuilder setTransparency(int transparency) {
        this.transparency = transparency;
        return this;
    }

    public ImageBuilder setMask(ArrayData mask) {
        this.mask = mask;
        return this;
    }

    public  ImageBuilder setDarker(boolean darker) {
        this.darker = darker;
        return this;
    }

    public ImageBuilder hasCaption(boolean caption) {
        this.caption = caption;
        return this;
    }
    
    public  ImageBuilder setSize(int x, int y) {
        this.xSize = x;
        this.ySize = y;
        return this;
    }

    /**
     * Resets the member variables to their default values.
     */
    private void reset() {
        description = "";
        scale = null;
        background = null;
        foreground = null;
        data = null;
        xSize = 0;
        ySize = 0;
        mask = null;
        transparency = 255;
        darker = false;
        nodata = 999999;
        nodetected = 999999;
    }

    /**
     * 
     * @return
     */
    public BufferedImage create() throws IllegalArgumentException {
        if (data == null) {
            LogHandler.getLogs()
                    .displayMsg("Must specify data", LogsType.ERROR);
            throw new IllegalArgumentException("No data to create image.");
        }

        ImageTools img = new ImageTools();
        img.setData(data);
        img.setBackground(background);
        img.setDescription(description);
        img.setForeground(foreground);
        img.setScale(scale);
        img.setSize(xSize, ySize);
        img.setMask(mask);
        img.setDarker(darker);
        img.setTransparency(transparency);
        img.setNodata(nodata);
        img.setNodetected(nodetected);
        img.hasCaption(caption);
        img.paint();
        reset();
        return img.getImg();
    }

    public static void main(String[] args) {
        
        ParserManager pm = new ParserManager();
        BufferedImage img;
        ArrayData data;
        
        LogHandler.getLogs().setLoggingVerbose(LogsType.ERROR);
        File file = new File("test-data", "1img.hdf");
        File bg = new File("test-data", "bg.png");
        File fg = new File("test-data", "fg.png");
        pm.setParser(new OdimH5Parser());
        pm.initialize(file);
        double nodata = (Double) pm.getProduct().getAttributeValue("/dataset1/what", "nodata");
        double undetect = (Double) pm.getProduct().getAttributeValue("/dataset1/what", "undetect");
//        double nodata = 0;
        
        data = pm.getProduct().getArray("dataset1");
        img = new ImageBuilder()
                .setData(data)
                .setBackground(bg)
                .setForeground(fg)
//                .setDarker(true)
//                .setTransparency(0)
                .setScale(ColorScales.getODCScale())
                .setNoDataValue(nodata)
                .setNoDetectedValue(undetect)
                .create();
        try {
            ImageIO.write(img, "PNG", new File("test-data", "imagebuilder1.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        File rfile = new File("test-data", "2012032609103300dBZ.cmax");
        pm.setParser(new RainbowImageParser());
        pm.initialize(rfile);
        data = pm.getProduct().getArray("datamap");
        ArrayData mask = pm.getProduct().getArray("flagmap");
        img = new ImageBuilder()
                // .setDarker(true)
                .setData(data)
                .setMask(mask)
                .setTransparency(128).setScale(ColorScales.getRBScale())
                .create();
        try {
            ImageIO.write(img, "PNG", new File("test-data", "imagebuilder2.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }    
    

}
