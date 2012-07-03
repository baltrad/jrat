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
public class RainbowImageFieldsName extends RainbowFieldsNameForParser {

    public RainbowImageFieldsName() {
        sizeX = "rows";
        sizeY = "columns";
        depth = "depth";
        product = "product";
        tags = new String[] {"datamap", "flagmap"};
    }
}
