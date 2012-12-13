/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.io.File;
import java.util.List;

import pl.imgw.jrat.data.H5DataContainer;
import pl.imgw.jrat.data.OdimH5Volume;
import pl.imgw.jrat.data.RainbowDataContainer;
import pl.imgw.jrat.data.RainbowVolume;
import pl.imgw.jrat.data.VolumeContainer;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.FileParser;
import pl.imgw.jrat.process.FilesProcessor;
import pl.imgw.jrat.process.GlobalParserSetter;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ScansunProcessor implements FilesProcessor {

    private ScansunManager manager;
    private FileParser parser;
    
    public ScansunProcessor(String[] args) {
        manager = new ScansunManager(args);
        parser = GlobalParserSetter.getInstance().getParser();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.process.FilesProcessor#processFile(java.util.List)
     */
    @Override
    public void processFile(List<File> files) {

        for (File f : files) {
            parser.initialize(f);
            if (parser.getProduct() instanceof RainbowDataContainer) {
                manager.calculate(new RainbowVolume(
                        (RainbowDataContainer) parser.getProduct()));
            } else if (parser.getProduct() instanceof H5DataContainer) {
                manager.calculate(new OdimH5Volume((H5DataContainer) parser
                        .getProduct()));
            }
        }
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.process.FilesProcessor#getProcessName()
     */
    @Override
    public String getProcessName() {
        // TODO Auto-generated method stub
        return "scansun";
    }

    /* (non-Javadoc)
     * @see pl.imgw.jrat.process.FilesProcessor#isValid()
     */
    @Override
    public boolean isValid() {
        // TODO Auto-generated method stub
        return true;
    }

}
