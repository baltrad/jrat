/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.out;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ResultPrinterManager {

    /*
     * Default printer
     */
    private ResultPrinter printer = new ResultPrinter() {
        @Override
        public void println(String str) {
            System.out.println(str);
        }

        @Override
        public void print(String str) {
            System.out.print(str);
            
        }
    };
    
    private static ResultPrinterManager manager = new ResultPrinterManager();
    
    /*
     * Private constructor preventing new instances of this class
     */
    private ResultPrinterManager() {}

    /**
     * @return the calculator
     */
    public static ResultPrinterManager getManager() {
        return manager;
    }

    /**
     * @return the printer
     */
    public ResultPrinter getPrinter() {
        return printer;
    }

    /**
     * @param printer the printer to set
     */
    public void setPrinter(ResultPrinter printer) {
        this.printer = printer;
    }
    
    
}
