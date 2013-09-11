/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.data;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pl.imgw.jrat.data.PolarData;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.VolumeParser;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunVolumesContainer implements Iterator<PolarData> {

	private VolumeParser parser = GlobalParser.getInstance().getVolumeParser();

	private List<PolarData> volumes;
	private Map<Date, Set<PolarData>> segregated;

	private Iterator<PolarData> volumesItr;
	private Iterator<Date> dateItr;

	private int size = 0;

	public ScansunVolumesContainer() {
	}

	public void setFiles(List<File> files) {
		List<PolarData> volumes = new LinkedList<PolarData>();
		for (File file : files) {
			PolarData volume = null;
			if (parser.parse(file)) {
				volume = parser.getPolarData();
				volumes.add(volume);
			}
		}

		setVolumes(volumes);
	}

	public void setVolumes(List<PolarData> volumes) {
		this.volumes = volumes;
		initialize();
	}

	private void initialize() {
		volumesItr = volumes.iterator();
		segregated = new TreeMap<Date, Set<PolarData>>();
		Date date;

		for (PolarData vol : volumes) {

			date = vol.getTime();
			// date = parseDateFromFileName(f.getName());
			if (date != null) {
				Set<PolarData> singles = segregated.get(date);

				if (singles == null) {
					singles = new HashSet<PolarData>();
				}

				singles.add(vol);
				segregated.put(date, singles);
			}
		}

		dateItr = segregated.keySet().iterator();
		setSize();
	}

	private void setSize() {
		int size = 0;
		Iterator<Set<PolarData>> itr = segregated.values().iterator();
		while (itr.hasNext()) {
			size += combination(itr.next().size());
		}
		this.size = size;
	}

	private static int combination(int n) {
		if (n < 2)
			return 0;
		return factorial(n) / (2 * factorial(n - 2));

	}

	private static int factorial(int n) {
		int fact = 1; // this will be the result
		for (int i = 1; i <= n; i++) {
			fact *= i;
		}
		return fact;
	}

	@Override
	public boolean hasNext() {

		if (volumesItr.hasNext()) {
			return true;
		}

		return false;

		/*
		 * if (volumesItr.hasNext()) { return true; } else if
		 * (dateItr.hasNext()) { setPairItr(dateItr.next()); // set new pairItr
		 * return hasNext(); } else return false;
		 * 
		 * return false;
		 */
	}

	@Override
	public PolarData next() {
		return volumesItr.next();

		/*
		 * if (hasNext()) return pairItr.next(); else return null;
		 */
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}

}
