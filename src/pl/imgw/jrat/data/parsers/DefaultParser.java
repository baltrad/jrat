/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import java.io.File;

import pl.imgw.jrat.data.DataContainer;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class DefaultParser implements FileParser {

    private RainbowParser rbvol = new RainbowParser(
            new RainbowVolumeFieldsName());
    private RainbowParser rbimg = new RainbowParser(
            new RainbowImageFieldsName());
    private OdimH5Parser odim = new OdimH5Parser();
    private WZFileParser wz = new WZFileParser();
    private WZStatsParser wzstat = new WZStatsParser();
    private File file = null;

    private final int HDF = 0;
    private final int RBI = 1;
    private final int RBV = 2;
    private final int WZ = 3;
    private final int WZSTAT = 4;
    private int format = -1;

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.parsers.FileParser#isValid(java.io.File)
     */
    @Override
    public boolean isValid(File file) {
        this.file = file;
        if (odim.isValid(file)) {
            format = HDF;
            return true;
        }
        if (rbvol.isValid(file)) {
            format = RBV;
            return true;
        }
        if (rbimg.isValid(file)) {
            format = RBI;
            return true;
        }
        if (wz.isValid(file)) {
            format = WZ;
            return true;
        }
        if (wzstat.isValid(file)) {
            format = WZSTAT;
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.parsers.FileParser#initialize(java.io.File)
     */
    @Override
    public boolean initialize(File file) {

        if (odim.isValid(file)) {
            format = HDF;
            return odim.initialize(file);
        }
        if (rbvol.isValid(file)) {
            format = RBV;
            return rbvol.initialize(file);
        }
        if (rbimg.isValid(file)) {
            format = RBI;
            return rbimg.initialize(file);
        }
        if (wz.isValid(file)) {
            format = WZ;
            return wz.initialize(file);
        }
        if (wzstat.isValid(file)) {
            format = WZSTAT;
            return wzstat.initialize(file);
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.parsers.FileParser#getProduct()
     */
    @Override
    public DataContainer getProduct() {
        if (format == HDF)
            return odim.getProduct();
        if (format == RBV)
            return rbvol.getProduct();
        if (format == RBI)
            return rbimg.getProduct();
        if (format == WZ)
            return wz.getProduct();
        if (format == WZSTAT)
            return wzstat.getProduct();
        return null;
    }

}
