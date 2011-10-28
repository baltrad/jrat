/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.view;

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

    public static Set<MapColor> getRainbowScale() {

        Set<MapColor> scale = new TreeSet<MapColor>();

        // min value
        scale.add(new MapColor(-1, new Color(125, 125, 125)));
        scale.add(new MapColor(0, new Color(0, 0, 0)));
        scale.add(new MapColor(73, new Color(0, 0, 255)));
        scale.add(new MapColor(79, new Color(0, 50, 255)));
        scale.add(new MapColor(85, new Color(0, 120, 255)));
        scale.add(new MapColor(91, new Color(25, 160, 255)));
        scale.add(new MapColor(97, new Color(81, 210, 255)));
        scale.add(new MapColor(97, new Color(81, 210, 255)));
        scale.add(new MapColor(103, new Color(135, 240, 255)));
        scale.add(new MapColor(109, new Color(255, 255, 255)));
        scale.add(new MapColor(115, new Color(255, 245, 190)));
        scale.add(new MapColor(115, new Color(255, 245, 190)));
        scale.add(new MapColor(121, new Color(255, 230, 0)));
        scale.add(new MapColor(127, new Color(255, 190, 0)));
        scale.add(new MapColor(133, new Color(255, 110, 0)));
        scale.add(new MapColor(139, new Color(255, 60, 0)));
        scale.add(new MapColor(145, new Color(200, 0, 0)));
        scale.add(new MapColor(151, new Color(160, 0, 0)));
        scale.add(new MapColor(157, new Color(125, 0, 0)));

        // no data
//        scale.add(new MapColor(255, new Color(127, 127, 127)));

        return scale;
    }
    public static Set<MapColor> getODCScale() {
        
        Set<MapColor> scale = new TreeSet<MapColor>();
        
        // min value
//        scale.add(new MapColor(-9999000, new Color(127, 127, 127)));
//        scale.add(new MapColor(0, new Color(0, 0, 0)));

        scale.add(new MapColor(-9999900, new Color(150, 150, 200)));
        scale.add(new MapColor(1, new Color(75, 75, 225)));
        scale.add(new MapColor(5, new Color(0, 0, 255)));
        scale.add(new MapColor(8 , new Color(0, 50, 255)));
        scale.add(new MapColor(11, new Color(0, 120, 255)));
        scale.add(new MapColor(14, new Color(25, 160, 255)));
        scale.add(new MapColor(17, new Color(81, 210, 255)));
        scale.add(new MapColor(20, new Color(135, 240, 255)));
        scale.add(new MapColor(23, new Color(255, 255, 255)));
        scale.add(new MapColor(26, new Color(255, 245, 190)));
        scale.add(new MapColor(29, new Color(255, 245, 190)));
        scale.add(new MapColor(32, new Color(255, 230, 0)));
        scale.add(new MapColor(35, new Color(255, 190, 0)));
        scale.add(new MapColor(38, new Color(255, 110, 0)));
        scale.add(new MapColor(41, new Color(255, 60, 0)));
        scale.add(new MapColor(44, new Color(200, 0, 0)));
        scale.add(new MapColor(47, new Color(160, 0, 0)));
        scale.add(new MapColor(50, new Color(125, 0, 0)));
        
        // no data
//        scale.add(new MapColor(255, new Color(127, 127, 127)));
        
        return scale;
    }

    /**
     * 
     * @param min
     * @param max
     * @param step
     * @return
     */
    public static Set<MapColor> getGrayScale(int min, int max, int step) {
        Set<MapColor> scale = new TreeSet<MapColor>();

        int spec = (max-min)/step;
        
        for (int i = 0; i < spec; i++) {
            scale.add(new MapColor(i * step, new Color((1+i) * 255/spec, (1+i) * 255/spec, (1+i) * 255/spec)));
        }
        return scale;
    }
    
    public static Set<MapColor> getGray256Scale() {
        Set<MapColor> scale = new TreeSet<MapColor>();
        

        for (int i = 0; i < 256; i++) {
            scale.add(new MapColor(i, new Color(i, i,i)));
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
        scale.add(new MapColor(0*step, new Color(0, 0, 60)));
        scale.add(new MapColor(1*step, new Color(0, 0, 120)));
        scale.add(new MapColor(2*step, new Color(0, 0, 255)));
        scale.add(new MapColor(3*step, new Color(0, 50, 255)));
        scale.add(new MapColor(4*step, new Color(0, 120, 255)));
        scale.add(new MapColor(5*step, new Color(25, 160, 255)));
        scale.add(new MapColor(6*step, new Color(81, 210, 255)));
        scale.add(new MapColor(7*step, new Color(135, 240, 255)));
        scale.add(new MapColor(8*step, new Color(255, 255, 255)));
        scale.add(new MapColor(9*step, new Color(255, 245, 190)));
        scale.add(new MapColor(10*step, new Color(255, 230, 0)));
        scale.add(new MapColor(11*step, new Color(255, 190, 0)));
        scale.add(new MapColor(12*step, new Color(255, 110, 0)));
        scale.add(new MapColor(13*step, new Color(255, 60, 0)));
        scale.add(new MapColor(14*step, new Color(200, 0, 0)));
        scale.add(new MapColor(15*step, new Color(160, 0, 0)));
        scale.add(new MapColor(16*step, new Color(125, 0, 0)));

        // no data
//        scale.add(new MapColor(255, new Color(127, 127, 127)));

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

    public static void main(String[] args) {

        Set<MapColor> set = ColorScales.getODCScale();

        for (MapColor color : set) {
            System.out.println((color.getValue() +1) + " "
                    + color.getColor().toString());
        }

    }

}
