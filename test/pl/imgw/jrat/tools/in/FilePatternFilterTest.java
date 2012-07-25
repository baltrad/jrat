/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.in;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Test;
/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class FilePatternFilterTest {

    private String pattern;
    private FilePatternFilter filter;
    private List<FileDate> list;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
    
    @Test
    public void getFileListFromRegexPatternTest() {
        
        pattern = "test-data/*.cmax";
        filter = new RegexFileFilter();
        list = filter.getFileList(pattern);
        assertEquals("number of files filtered is wrong", 3, list.size());
    }
    
    @Test
    public void getFileListFromMultiplyRegexPatternsTest() {
        
        pattern = "test-data/*.cmax test-data/*.vol";
        filter = new RegexFileFilter();
        list = filter.getFileList(pattern);
        assertEquals("number of files filtered is wrong", 6, list.size());
    }
    
    @Test
    public void getFileListWithDateInTheBegginingOfFileNameTest() {
        pattern = "test-data/*dBZ.vol";
        filter = new RegexFileFilter();
        list = filter.getFileList(pattern);
        String dateFromFile = sdf.format(list.get(0).getDate());
        String dateGiven = "201110312320";
        assertTrue("reading date failed", dateFromFile.matches(dateGiven));
        
    }
    
    @Test
    public void getFileListWithDateInTheMiddleOfFileNameTest() {
        pattern = "test-data/*.h5";
        filter = new RegexFileFilter();
        list = filter.getFileList(pattern);
        String dateFromFile = sdf.format(list.get(0).getDate());
        String dateGiven = "201109111340";
        assertTrue("reading date failed", dateFromFile.matches(dateGiven));
        
    }
    
    @Test
    public void parseInvalidPatternTest() {
        pattern = "nie ma takiego pliku";
        filter = new RegexFileFilter();
        list = filter.getFileList(pattern);
        assertEquals("invalid pattern returned value", 0, list.size());
    }
}
