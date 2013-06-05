/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.tools.in;

import static org.junit.Assert.*;

import java.io.File;
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
    private RegexFileFilter filter;
    private List<File> list;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
    
    @Test
    public void getFileListFromRegexPatternTest() {
        
        pattern = "test-data/pair/*.vol";
        filter = new RegexFileFilter();
        list = filter.getFileList(pattern);
        assertEquals("number of files filtered is wrong", 9, list.size());
    }
    
    @Test
    public void getFileListFromMultiplyRegexPatternsTest() {
        
        pattern = "test-data/pair/*.h5 test-data/pair/*.vol";
        filter = new RegexFileFilter();
        list = filter.getFileList(pattern);
        assertEquals("number of files filtered is wrong", 14, list.size());
    }
    
    @Test
    public void getFileListWithDateInTheBegginingOfFileNameTest() {
        pattern = "test-data/pair/*00dBZ.vol";
        filter = new RegexFileFilter();
        list = filter.getFileList(pattern);
        String dateFromFile = sdf.format(filter.getDate(list.get(0)));
        String dateGiven = "201110100300";
        assertTrue("reading date failed", dateFromFile.matches(dateGiven));
        
    }
    
    @Test
    public void getFileListWithDateInTheMiddleOfFileNameTest() {
        pattern = "test-data/pair/T_PAGZ48*";
        filter = new RegexFileFilter();
        list = filter.getFileList(pattern);
        String dateFromFile = sdf.format(filter.getDate(list.get(1)));
        String dateGiven = "201110100310";
        assertTrue("reading date failed", dateFromFile.matches(dateGiven));
        
    }
    
    @Test
    public void parseInvalidPatternTest() {
        pattern = "this_is_not_a_file";
        filter = new RegexFileFilter();
        list = filter.getFileList(pattern);
        assertEquals("invalid pattern returned value", 0, list.size());
    }
}
