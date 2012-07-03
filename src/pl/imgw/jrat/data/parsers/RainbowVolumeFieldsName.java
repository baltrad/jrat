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
public class RainbowVolumeFieldsName extends RainbowFieldsNameForParser {

    public RainbowVolumeFieldsName() {
        
        sizeX = "rays";
        sizeY = "bins";
        depth = "depth";
        product = "volume";
        tags = new String[] {"rawdata", "rayinfo"};
        
    }

}
