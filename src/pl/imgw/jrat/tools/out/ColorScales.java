/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out;

import java.awt.Color;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ColorScales {

    private Set<MapColor> scale;

    /**
     * Set default color scale
     * 
     * 
     */
    public ColorScales() {

        this.scale = getRainbowScale();

    }

    /**
     * 
     * Set color scale
     * 
     * @param values
     * @param colors
     */
    public ColorScales(Set<MapColor> scale) {

        this.scale = scale;

    }

    public static Set<MapColor> getRBScale() {

        Set<MapColor> scale = new TreeSet<MapColor>();

        double x = 1.5;
        double step = 3.5;
        // scale.add(new MapColor(-1, new Color(125, 125, 125)));
        // scale.add(new MapColor(0, new Color(0, 0, 0)));
//        scale.add(new MapColor(-9999900, new Color(150, 150, 200)));
        scale.add(new MapColor(x, new Color(75, 75, 225)));
        scale.add(new MapColor(x += step, new Color(0, 0, 255)));
        scale.add(new MapColor(x += step, new Color(0, 50, 255)));
        scale.add(new MapColor(x += step, new Color(0, 120, 255)));
        scale.add(new MapColor(x += step, new Color(25, 160, 255)));
        scale.add(new MapColor(x += step, new Color(81, 210, 255)));
        scale.add(new MapColor(x += step, new Color(135, 240, 255)));
        scale.add(new MapColor(x += step, new Color(255, 255, 255)));
        scale.add(new MapColor(x += step, new Color(255, 245, 190)));
        scale.add(new MapColor(x += step, new Color(255, 230, 0)));
        scale.add(new MapColor(x += step, new Color(255, 190, 0)));
        scale.add(new MapColor(x += step, new Color(255, 110, 0)));
        scale.add(new MapColor(x += step, new Color(255, 60, 0)));
        scale.add(new MapColor(x += step, new Color(200, 0, 0)));
        scale.add(new MapColor(x += step, new Color(160, 0, 0)));
        scale.add(new MapColor(x += step, new Color(125, 0, 0)));

        // no data
        // scale.add(new MapColor(255, new Color(127, 127, 127)));

        return scale;
    }
    
    public static Set<MapColor> getRedScale(double min, double step) {

        Set<MapColor> scale = new TreeSet<MapColor>();

//        scale.add(new MapColor(min, new Color(135, 240, 255)));
//        scale.add(new MapColor(min += step, new Color(255, 255, 255)));
        scale.add(new MapColor(min, new Color(255, 245, 190)));
        scale.add(new MapColor(min += step, new Color(255, 230, 0)));
        scale.add(new MapColor(min += step, new Color(255, 190, 0)));
        scale.add(new MapColor(min += step, new Color(255, 110, 0)));
        scale.add(new MapColor(min += step, new Color(255, 60, 0)));
        scale.add(new MapColor(min += step, new Color(200, 0, 0)));
        scale.add(new MapColor(min += step, new Color(160, 0, 0)));
        scale.add(new MapColor(min += step, new Color(125, 0, 0)));

        return scale;
    }
    
    public static Set<MapColor> getColdWarmScale(double extrim, double step) {

        double absEx = Math.abs(extrim);
        int length = (int) (absEx / step);
        Set<MapColor> scale = new TreeSet<MapColor>();
        int colorStep = 255 / length;
        
        for (int i = 0; i < length; i++) {
            scale.add(new MapColor(-absEx + i * step, new Color(255, colorStep * i,
                    colorStep * i)));
        }
        
        scale.add(new MapColor(0, Color.white));
        
        for (int i = length - 1; i >= 0; i--) {
            scale.add(new MapColor(absEx - i * step, new Color(colorStep * i,
                    colorStep * i, 255)));
        }

        return scale;
    }
    
    
    public static Set<MapColor> getRainbowScale() {

        Set<MapColor> scale = new TreeSet<MapColor>();

        int x = 66;
        int step = 7;
        // scale.add(new MapColor(-1, new Color(125, 125, 125)));
        // scale.add(new MapColor(0, new Color(0, 0, 0)));
        scale.add(new MapColor(x, new Color(75, 75, 225)));
        scale.add(new MapColor(x += step, new Color(0, 0, 255)));
        scale.add(new MapColor(x += step, new Color(0, 50, 255)));
        scale.add(new MapColor(x += step, new Color(0, 120, 255)));
        scale.add(new MapColor(x += step, new Color(25, 160, 255)));
        scale.add(new MapColor(x += step, new Color(81, 210, 255)));
        scale.add(new MapColor(x += step, new Color(135, 240, 255)));
        scale.add(new MapColor(x += step, new Color(255, 255, 255)));
        scale.add(new MapColor(x += step, new Color(255, 245, 190)));
        scale.add(new MapColor(x += step, new Color(255, 230, 0)));
        scale.add(new MapColor(x += step, new Color(255, 190, 0)));
        scale.add(new MapColor(x += step, new Color(255, 110, 0)));
        scale.add(new MapColor(x += step, new Color(255, 60, 0)));
        scale.add(new MapColor(x += step, new Color(200, 0, 0)));
        scale.add(new MapColor(x += step, new Color(160, 0, 0)));
        scale.add(new MapColor(x += step, new Color(125, 0, 0)));

        // no data
        // scale.add(new MapColor(255, new Color(127, 127, 127)));

        return scale;
    }

    public static Set<MapColor> getODCScale() {

        Set<MapColor> scale = new TreeSet<MapColor>();

        double x = 1.5;
        double step = 3.5;
        // scale.add(new MapColor(-1, new Color(125, 125, 125)));
        // scale.add(new MapColor(0, new Color(0, 0, 0)));
//        scale.add(new MapColor(-9999900, new Color(150, 150, 200)));
        scale.add(new MapColor(x, new Color(75, 75, 225)));
        scale.add(new MapColor(x += step, new Color(0, 0, 255)));
        scale.add(new MapColor(x += step, new Color(0, 50, 255)));
        scale.add(new MapColor(x += step, new Color(0, 120, 255)));
        scale.add(new MapColor(x += step, new Color(25, 160, 255)));
        scale.add(new MapColor(x += step, new Color(81, 210, 255)));
        scale.add(new MapColor(x += step, new Color(135, 240, 255)));
        scale.add(new MapColor(x += step, new Color(255, 255, 255)));
        scale.add(new MapColor(x += step, new Color(255, 245, 190)));
        scale.add(new MapColor(x += step, new Color(255, 230, 0)));
        scale.add(new MapColor(x += step, new Color(255, 190, 0)));
        scale.add(new MapColor(x += step, new Color(255, 110, 0)));
        scale.add(new MapColor(x += step, new Color(255, 60, 0)));
        scale.add(new MapColor(x += step, new Color(200, 0, 0)));
        scale.add(new MapColor(x += step, new Color(160, 0, 0)));
        scale.add(new MapColor(x += step, new Color(125, 0, 0)));

        // no data
        // scale.add(new MapColor(255, new Color(127, 127, 127)));

        return scale;
    }

    /**
     * Scale is adjusted to maximum value, maximum different colors is 256, and
     * depends on given maximum value
     * 
     * @param max
     * @return
     */
    public static Set<MapColor> getGrayScale(int max) {
        return getGrayScale(0, max, 1);
    }
    
    /**
     * Scale is adjusted to maximum value, maximum different colors is 256, and
     * depends on given maximum value
     * @param start
     *            starting value
     * @param max
     *            heighest value
     * @return
     */
    public static Set<MapColor> getGrayScale(int start, int max) {
        return getGrayScale(0, max, 1);
    }
    
    /**
     * Number of gray colors is vary depends on given maximum value. If max
     * value is bigger then 256 then scale is adjusted. The first value is given
     * and step is calculated based on max value.
     * 
     * @param start
     *            starting value
     * @param max
     *            heighest value
     * @return
     */
    public static Set<MapColor> getGrayScale(int start, int max, int step) {
        Set<MapColor> scale = new TreeSet<MapColor>();

        max = max - start;
        
        if(max == 0)
            return null;
        
        int stepParam = (max / 255 + 1) * step;
        int spec = max / stepParam;

//        System.out.println("Step: " + step);
        
        // if(max < 256)
        // return getGray256Scale();

        for (int i = 0; i < spec + 1; i++) {
            
            scale.add(new MapColor((i + start) * stepParam, new Color(i * 255 / spec, i * 255
                    / spec, i * 255 / spec)));
        }
        return scale;
    }

    public static Set<MapColor> getGray256Scale() {
        Set<MapColor> scale = new TreeSet<MapColor>();

        for (int i = 0; i < 256; i++) {
            scale.add(new MapColor(i, new Color(i, i, i)));
        }
        return scale;
    }

    public static Set<MapColor> getDefaultScale() {
        Set<MapColor> scale = new TreeSet<MapColor>();

        scale.add(new MapColor(0, new Color(0, 0, 0)));
        Random rnd = new Random(System.currentTimeMillis());
        for (int i = 1; i < 20; i++) {
            scale.add(new MapColor(i, new Color(rnd.nextInt(256), rnd
                    .nextInt(256), rnd.nextInt(256))));
        }
        return scale;
    }

    public static Set<MapColor> getCyfraScale(int step) {

        Set<MapColor> scale = new TreeSet<MapColor>();

        // min value
        scale.add(new MapColor(0 * step, new Color(0, 0, 60)));
        scale.add(new MapColor(1 * step, new Color(0, 0, 120)));
        scale.add(new MapColor(2 * step, new Color(0, 0, 255)));
        scale.add(new MapColor(3 * step, new Color(0, 50, 255)));
        scale.add(new MapColor(4 * step, new Color(0, 120, 255)));
        scale.add(new MapColor(5 * step, new Color(25, 160, 255)));
        scale.add(new MapColor(6 * step, new Color(81, 210, 255)));
        scale.add(new MapColor(7 * step, new Color(135, 240, 255)));
        scale.add(new MapColor(8 * step, new Color(255, 255, 255)));
        scale.add(new MapColor(9 * step, new Color(255, 245, 190)));
        scale.add(new MapColor(10 * step, new Color(255, 230, 0)));
        scale.add(new MapColor(11 * step, new Color(255, 190, 0)));
        scale.add(new MapColor(12 * step, new Color(255, 110, 0)));
        scale.add(new MapColor(13 * step, new Color(255, 60, 0)));
        scale.add(new MapColor(14 * step, new Color(200, 0, 0)));
        scale.add(new MapColor(15 * step, new Color(160, 0, 0)));
        scale.add(new MapColor(16 * step, new Color(125, 0, 0)));

        // no data
        // scale.add(new MapColor(255, new Color(127, 127, 127)));

        return scale;
    }
    
    public static Set<MapColor> getWZScale() {

        Set<MapColor> scale = new TreeSet<MapColor>();

        // min value
        scale.add(new MapColor(0, new Color(0, 0, 0)));
        scale.add(new MapColor(1, new Color(20, 20, 20)));
        scale.add(new MapColor(2, new Color(129, 255, 182)));
        scale.add(new MapColor(3, new Color(255, 255, 0)));
        scale.add(new MapColor(4, new Color(255, 153, 46)));
        scale.add(new MapColor(5, new Color(255, 90, 174)));
        scale.add(new MapColor(6, new Color(255, 0, 0)));
        
        

        // no data
        // scale.add(new MapColor(255, new Color(127, 127, 127)));

        return scale;
    }

    /**
     * @return the scale
     */
    public Set<MapColor> getScale() {
        return scale;
    }

    /**
     * @param scale
     *            the scale to set
     */
    public void setScale(Set<MapColor> scale) {
        this.scale = scale;
    }

    private static double raw2dbz(double x) {
        return 0.5 * x - 31.5;
    }

    public static void main(String[] args) {

        Set<MapColor> set = ColorScales.getODCScale();

//        for (MapColor color : set) {
//            System.out.println((raw2dbz(color.getValue())) + " "
//                    + color.getColor().toString());
//        }

//        set = ColorScales.getODCScale();

        for (MapColor color : set) {
            System.out.println(color.getValue() + " "
                    + color.getColor().toString());
        }

    }

}
