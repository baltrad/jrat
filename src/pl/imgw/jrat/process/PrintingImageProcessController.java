/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pl.imgw.jrat.data.ArrayData;
import pl.imgw.jrat.data.DataContainer;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.tools.out.ArrayFilePrinter;
import pl.imgw.util.LogManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class PrintingImageProcessController {

    /**
     * 
     * @param files
     * @param output output folder
     * @param options
     */
    public static void printImage(List<File> files, File output,
            String[] options) {

        // log.displayMsg("Printing image from file", WARNING);
        ImagesController ic = null;
        ParserManager pm = new ParserManager();

        pm.setParser(GlobalParser.getInstance().getParser());

        LogManager.getProgBar().initialize(20, files.size(),
                "Printing images...");
        
        for (File file : files) {
            if (!pm.initialize(file))
                continue;
            ic = new ImagesController(options);
            
            DataContainer prod = pm.getProduct();
            String folderName;
            if(file.getName().contains("."))
                folderName = file.getName().split("\\.")[0];
            else
                folderName = file.getName();
            
            /*
             * creating sub-folder for each file
             */
            File out = new File(output, folderName);
            out.mkdirs();
            
            if(prod == null)
                continue;
            
            printToFile(ic, out, prod);
            LogManager.getProgBar().evaluate();
        }
        LogManager.getProgBar().complete("done");

    }

    private static void printToFile(ImagesController ic, File output,
            DataContainer prod) {

        Map<String, ArrayData> data = new HashMap<String, ArrayData>();

            
        if (ic.getDatasetValue().isEmpty()) {
            data.putAll(prod.getArrayList());
        } else {
            data.put(ic.getDatasetValue(), prod.getArray(ic.getDatasetValue()));
        }

        
        Iterator<String> i = data.keySet().iterator();
        while (i.hasNext()) {
            String name = i.next();
            if (ic.getFormat().toLowerCase().matches("png")) {
                ic.getBuilder().setData(data.get(name));
                ic.getBuilder().saveToFile(
                        new File(output, "array_"
                                + name.replace("[^A-Za-z0-9]", "") + ".png"));
            } else if (ic.getFormat().toLowerCase().matches("txt")) {

                File out = new File(output,
                        "array_" + name.replaceAll("[^A-Za-z0-9]", "") + ".txt");
                
                ArrayFilePrinter.printTXT(data.get(name),  out);
            }
        }
    }
    
}
