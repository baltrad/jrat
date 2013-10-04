/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid.view;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.activation.FileDataSource;

import pl.imgw.jrat.calid.data.CalidDataHandler;
import pl.imgw.jrat.calid.data.CalidParameters;
import pl.imgw.jrat.calid.data.RadarsPair;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class CalidResultFileGetter {

	private static Log log = LogManager.getLogger();

	/**
	 * 
	 * @param pair
	 * @param params
	 * @return
	 */
	public static Set<File> getResultFiles(RadarsPair pair,
			CalidParameters params) {
		File folder = new File(CalidDataHandler.getCalidPath());
		return getResultFiles(pair, params, folder);
	}

	/**
	 * 
	 * @param pair
	 * @param params
	 * @param folder
	 * @return
	 */
	protected static Set<File> getResultFiles(RadarsPair pair,
			CalidParameters params, File folder) {

		Set<File> results = new TreeSet<File>();

		// skip not-folders
		if (!folder.isDirectory())
			return results;

		/*
		 * loop for pairname folders
		 */
		for (File pairname : folder.listFiles()) {
			// skip not-folders
			if (!pairname.isDirectory())
				continue;

			// pair siteName
			String name = pairname.getName();

			/*
			 * skip pairs which does not contains source siteName if they are set
			 */
			if (pair != null && !name.isEmpty() && !pair.getSource1().isEmpty()) {
				if (!pair.getSource2().isEmpty()) {
					if (!name.contains(pair.getSource1())
							|| !name.contains(pair.getSource2()))
						continue;
				} else if (!name.contains(pair.getSource1())) {
					continue;
				}
			}

			File[] resultFolders = null;

			if (areOnlySrcParametersSet(pair, params)) {
				File f = getDefaultFolder(pairname);
				// System.out.println("only src " + f);
				if (f != null) {
					resultFolders = new File[] { f };
				}
			} else if (areAllParametersUnset(pair, params)) {
				// System.out.println("nothing set");
				resultFolders = pairname.listFiles();
			} else {
				File f = getMatchingFolder(pairname, params);
				if (f != null)
					resultFolders = new File[] { f };
				// System.out.println("else " + f);
			}
			if (resultFolders == null)
				continue;

			/*
			 * loop for parametername folders
			 */
			for (File parameters : resultFolders) {
				// skip not-folders
				if (!parameters.isDirectory())
					continue;

				// parameters
				for (File file : parameters.listFiles()) {
					if (file.isFile() && file.getName().endsWith("results")) {
						if (keep(file, params))
							results.add(file);
					}
				}

			}

		}
		return results;
	}

	private static boolean keep(File file, CalidParameters params) {
		Date fileDate = null;
		try {
			fileDate = CalidDataHandler.CALID_DATE_FORMAT.parse(file.getName());
		} catch (ParseException e) {
			return false;
		}

		Calendar paramStartDate = Calendar.getInstance();
		paramStartDate.setTime(params.getStartRangeDate());
		paramStartDate.set(Calendar.HOUR_OF_DAY, 0);
		paramStartDate.set(Calendar.MINUTE, 0);
		paramStartDate.set(Calendar.SECOND, 0);
		paramStartDate.set(Calendar.MILLISECOND, 0);

		Calendar paramEndDate = Calendar.getInstance();
		paramEndDate.setTime(params.getEndRangeDate());
		paramEndDate.set(Calendar.HOUR_OF_DAY, 23);
		paramEndDate.set(Calendar.MINUTE, 59);
		paramEndDate.set(Calendar.SECOND, 59);

		if (fileDate.before(paramStartDate.getTime()))
			return false;

		if (!params.isEndDateDefault()) {
			if (fileDate.after(paramEndDate.getTime()))
				return false;
		}

		return true;

	}

	private static File getDefaultFolder(File pairFolder) {
		int max = 0;
		File biggest = null;
		for (File params : pairFolder.listFiles()) {
			if (params.isDirectory())
				if (params.listFiles().length > max) {
					biggest = params;
					max = params.listFiles().length;
				}
		}
		return biggest;
	}

	private static File getMatchingFolder(File pairFolder,
			CalidParameters params) {

		for (File f : pairFolder.listFiles()) {
			CalidParameters fParams = CalidDataHandler
					.getParamsFromFolderName(f.getName());

			if (fParams == null)
				return null;

			if (!params.isDistanceDefault())
				if (!params.getDistance().equals(fParams.getDistance()))
					continue;

			if (!params.isElevationDefault())
				if (!params.getElevation().equals(fParams.getElevation()))
					continue;

			if (!params.isMaxRangeDefault())
				if (!params.getMaxRange().equals(fParams.getMaxRange()))
					continue;

			if (!params.isReflectivityDefault())
				if (!params.getReflectivity().equals(fParams.getReflectivity()))
					continue;

			return f;
		}
		return null;
	}

	private static boolean areOnlySrcParametersSet(RadarsPair pair,
			CalidParameters params) {
		if (pair == null)
			return false;
		if ((!pair.getSource1().isEmpty() || !pair.getSource2().isEmpty())
				&& params.isDistanceDefault() & params.isElevationDefault()
				&& params.isReflectivityDefault())
			return true;

		return false;
	}

	private static boolean areAllParametersUnset(RadarsPair pair,
			CalidParameters params) {

		if ((pair == null || pair.getSource1().isEmpty()
				&& pair.getSource2().isEmpty())
				&& params.isDistanceDefault()
				& params.isElevationDefault()
				& params.isMaxRangeDefault() & params.isFrequencyDefault())
			return true;

		return false;
	}

}
