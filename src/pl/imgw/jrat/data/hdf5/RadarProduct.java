/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;

import java.io.File;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RadarProduct implements DataSource{

    private ArrayData data;
    private String projection;
    
    /*
     * wspolne parametry dla produktow
     */
    

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.hdf5.DataSource#initializeFromFile(java.io.File)
     */
    @Override
    public boolean initializeFromFile(File f) {
        return false;
        // TODO Auto-generated method stub
        
    }
    

    /**
     * @return the data
     */
    public ArrayData getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(ArrayData data) {
        this.data = data;
    }

    /**
     * @return the projection
     */
    public String getProjection() {
        return projection;
    }

    /**
     * @param projection the projection to set
     */
    public void setProjection(String projection) {
        this.projection = projection;
    }


    
    

}
