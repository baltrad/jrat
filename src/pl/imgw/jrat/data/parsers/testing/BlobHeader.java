/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers.testing;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class BlobHeader {

    private static final String BLOBID = "blobid=";
    private static final String SIZE = "size=";
    private static final String COMPRESSION = "compression=";

    private int blobid = -1;
    private int size = 0;
    private String compression = "";

    public BlobHeader(String header) {

//        System.out.println(header);
        for (String s : header.split(" ")) {
            if (s.startsWith(BLOBID)) {
                blobid = Integer.parseInt(s.substring(BLOBID.length() + 1,
                        s.length() - 1));
            } else if (s.startsWith(SIZE)) {
                size = Integer.parseInt(s.substring(SIZE.length() + 1,
                        s.length() - 1));
            } else if (s.startsWith(COMPRESSION)) {
                compression = s.substring(COMPRESSION.length() + 1,
                        s.length() - 1);
            }
        }

//        System.out.println("Ustawil: " + blobid + " " + size + " " + compression);
        
    }

    public boolean isValid() {
        return (blobid != -1 && size > 0);
    }

    /**
     * @return the blobid
     */
    public int getBlobid() {
        return blobid;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    /**
     * @return the compression
     */
    public String getCompression() {
        return compression;
    }

}
