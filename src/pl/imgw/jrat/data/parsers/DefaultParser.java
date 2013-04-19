/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import java.io.File;

import pl.imgw.jrat.data.containers.DataContainer;
import pl.imgw.jrat.data.containers.OdimDataContainer;
import pl.imgw.jrat.data.containers.OdimH5Volume;
import pl.imgw.jrat.data.containers.RainbowDataContainer;
import pl.imgw.jrat.data.containers.RainbowVolume;
import pl.imgw.jrat.data.containers.VolumeContainer;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class DefaultParser implements FileParser, VolumeParser {

	private Rainbow53VolumeParser rbvol;
	private Rainbow53ImageParser rbimg;
	private OdimH5Parser odim;
	private WZFileParser wz;
	private WZStatsParser wzstat;
	private IntArrayParser intarray;

	private final int HDF = 0;
	private final int RBI = 1;
	private final int RBV = 2;
	private final int WZ = 3;
	private final int WZSTAT = 4;
	private final int INTARRAY = 5;
	private int format = -1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.imgw.jrat.data.parsers.FileParser#isValid(java.io.File)
	 */
	@Override
	public boolean isValid(File file) {
		
	    initializeParsers();
	    
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
		/*
		 * reading int array, additional
		 */
		if (intarray.isValid(file)) {
			format = INTARRAY;
			return true;
		}

		return false;
	}

	private void initializeParsers() {
	    rbvol = new Rainbow53VolumeParser();
	    rbimg = new Rainbow53ImageParser();
	    odim = new OdimH5Parser();
	    wz = new WZFileParser();
	    wzstat = new WZStatsParser();
	    intarray = new IntArrayParser();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.imgw.jrat.data.parsers.FileParser#initialize(java.io.File)
	 */
	@Override
	public boolean initialize(File file) {

	    initializeParsers();
	    
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
		if (intarray.isValid(file)) {
			format = INTARRAY;
			return intarray.initialize(file);
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
		if (format == INTARRAY)
			return intarray.getProduct();
		return null;
	}

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.parsers.VolumeParser#getVolume()
     */
    @Override
    public VolumeContainer getVolume() {
        if (format == RBV) {
            VolumeContainer vol = rbvol.getVolume();
            return (vol.isValid()) ? vol : null;
        }
        if (format == HDF) {
            VolumeContainer vol = odim.getVolume();
            return (vol.isValid()) ? vol : null;
        }
        return null;
    }
    
}
