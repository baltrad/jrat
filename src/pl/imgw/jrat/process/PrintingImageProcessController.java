/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.process.CommandLineArgsParser.PRINTIMAGE;

import java.io.File;
import java.util.List;

import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.ParserManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class PrintingImageProcessController {

    public static void printImage(List<File> files, File output,
            String[] options) {

        // LogHandler.getLogs().displayMsg("Printing image from file", WARNING);
        ImagesController ic = null;
        ParserManager pm = new ParserManager();

        pm.setParser(new DefaultParser());

        for (File file : files) {
            if (pm.initialize(file)) {
                ic = new ImagesController(options);
                ic.getBuilder().setData(
                        pm.getProduct().getArray(ic.getDatasetValue()));

                File imgout = new File(output, file.getName() + "."
                        + ic.getFormat());
                ic.getBuilder().saveToFile(imgout);
            }
        }

    }
    
}
