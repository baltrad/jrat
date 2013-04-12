/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
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
public class ConsoleProgressBar {

    private int barSize = 1;
    private double factor = 1;
    private boolean show = false;
    private int maxValue = 1;
    private String msg = "";
    private int progress = 0;
    
    private ConsoleProgressBar() {
        ;
    }
    
    private static ConsoleProgressBar cpb = new ConsoleProgressBar();
    
    public static ConsoleProgressBar getProgressBar() {
        return cpb;
    }
    
    /**
     * 
     * it has to be initialized first
     * 
     * @param barSize
     *            width of progress bar in console
     * @param maxValue
     *            reaching this value progress bar will show 100%
     * @param show
     *            use false if you don't want to display progress bar in console
     * @param msg
     *            message printed in front of progress bar
     */
    public void initialize(int barSize, int maxValue, boolean show, String msg) {
        if(maxValue == 0 || barSize == 0) {
            reset();
            return;
        }
        if(this.show)
            printDoneMsg();
        this.msg = msg;
        this.barSize = barSize;
        factor = (double)barSize / (double)maxValue;
        this.show = show;
        this.maxValue = maxValue;
    }
    
    /**
     * 
     * it has to be initialized first
     * 
     * @param barSize
     *            width of progress bar in console
     * @param maxValue
     *            reaching this value progress bar will show 100%
     * @param show
     *            use false if you don't want to display progress bar in console
     */
    public void initialize(int barSize, int maxValue, boolean show) {
        initialize(barSize, maxValue, show, "");
        
    }
    
    private void reset() {
        show = false;
        progress = 0;
    }
    
    /**
     * Update progress bar, the progress will show (<code>value</code> / <code>maxValue</code>) * 100%
     * 
     * @param value
     */
    public void printProgress(int value) {
        if (show) {
            String progress = getProgress(value + 1);
            System.out.print(msg + "\t\t|" + progress + "|\r");
        }
        if (value == maxValue) {
            reset();
        }
    }
    
    public void evaluate() {
        if(show)
            printProgress(progress++);
    }

    /**
     * Must be printed in the end if value never reach maxValue
     */
    public void printDoneMsg(String endMsg) {
        if (show) {
            String progress = getProgress(maxValue);
            System.out.print(msg + "\tdone\t|" + progress + "| " + endMsg);
            System.out.print("\n");
        }
        reset();
    }

    /**
     * Completing progress bar
     */
    public void printDoneMsg() {
        printDoneMsg("");
    }
    
    private String getProgress(int value) {
        int prog = (int) (factor * value);
        
        String progress = "";
        for (int i = 0; i < prog; i++)
            progress += "=";
        for (int i = 0; i < barSize - prog; i++)
            progress += " ";
        return progress;
    }
    
    public static void main(String[] args) {
        int max = 50;
        int barSize = 25;
        ConsoleProgressBar bar = new ConsoleProgressBar();
        bar.initialize(barSize, max, true);
        for(int i = 0; i < max; i++) {
            bar.printProgress(i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        bar.printDoneMsg();
    }
    
}
