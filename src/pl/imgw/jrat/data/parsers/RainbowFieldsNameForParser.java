/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public abstract class RainbowFieldsNameForParser {

    protected String sizeX = "";
    protected String sizeY = "";
    protected String depth = "";
    protected String product = "";
    protected String[] tags = null;
    
    public String[] getTags() {
        return tags;
    }

    public String getX() {
        return sizeX;
    }

    public String getY() {
        return sizeY;
    }

    public String getD() {
        return depth;
    }

    public String getProduct() {
        return product;
    }

}