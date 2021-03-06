/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out;

import java.awt.Color;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class MapColor implements Comparable<Object>{

    private double value;
    private Color color;

    public MapColor(double value, Color color) {
        this.value = value;
        this.color = color;

    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color
     *            the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    public String toString() {
        return String.valueOf(value);
    }

    public int compareTo(Object o1) {

        if (this.value == ((MapColor) o1).value)
            return 0;
        if (this.value > ((MapColor) o1).value)
            return 1;
        else
            return -1;
    }

}
