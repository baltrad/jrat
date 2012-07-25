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

    int barSize;
    double factor;
    
    public void initialize(int barSize, int maxValue) {
        this.barSize = barSize;
        factor = (double)barSize / (double)maxValue;
    }
    
    public void printProgress(int value) {
        String progress = getProgress(value + 1);
        System.out.print("|" + progress + "|\r");
    }

    public void printDoneMsg() {
        System.out.print("\n");
    }

    private String getProgress(int value) {
        int prog = (int) (factor * value);
//        System.out.println(value + " " +factor + " " + prog);
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
        bar.initialize(barSize, max);
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
