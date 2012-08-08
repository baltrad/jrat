/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat;

import java.util.ArrayList;
import java.util.List;

import pl.imgw.jrat.process.ProcessController;
import eu.baltrad.beast.pgfwk.IGeneratorPlugin;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class PrintElevation implements IGeneratorPlugin {

    /* (non-Javadoc)
     * @see eu.baltrad.beast.pgfwk.IGeneratorPlugin#generate(java.lang.String, java.lang.String[], java.lang.Object[])
     */
    @Override
    public String generate(String algorithm, String[] files, Object[] arguments) {
        
        List<String> argList = new ArrayList<String>();
        
        argList.add("-i");
        for(String f : files) {
            argList.add(f);
        }
        for (Object o : arguments) {
            String s = (String) o;
            if (s != null) {
                argList.add(s);
            }
        }
        
        
        ProcessController proc = new ProcessController(argList.toArray(new String[argList.size()]));
        proc.start();
        
        return null;
    }

}
