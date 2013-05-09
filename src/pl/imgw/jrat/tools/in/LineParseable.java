package pl.imgw.jrat.tools.in;

public interface LineParseable {

    public String getStringHeaderWithDelimiter(String delimiter);

    public void parseValues(String[] words);
}
