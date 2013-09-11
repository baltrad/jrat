/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.LocalDate;

import pl.imgw.jrat.tools.in.LineParseTool;
import pl.imgw.jrat.scansun.data.ScansunEvent.ScansunEventFacory;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;
import static pl.imgw.jrat.scansun.data.ScansunConstants.*;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunResultContainer extends AbstractSet<ScansunEvent> {

	private static Log log = LogManager.getLogger();

	private Set<ScansunEvent> events;
	private Set<ScansunSite> sites;

	public ScansunResultContainer() {
		this.events = new HashSet<>();
		this.sites = new HashSet<>();
	}

	public Set<ScansunSite> getSites() {
		return sites;
	}

	public void addEvent(ScansunEvent event) {
		events.add(event);
		sites.add(event.getSite());

	}

	public ScansunResultContainer getEvents() {
		return this;
	}

	public boolean hasResults() {
		return events.size() > 0;
	}

	public ScansunResultContainer bySite(ScansunSite site) {
		ScansunResultContainer container = new ScansunResultContainer();

		for (ScansunEvent event : events) {
			if (event.getSite() == site) {
				container.addEvent(event);
			}
		}

		return container;
	}

	public ScansunResultContainer byEventType(ScansunEventType eventType) {
		ScansunResultContainer container = new ScansunResultContainer();

		for (ScansunEvent event : events) {
			if (event.getEventType() == eventType) {
				container.addEvent(event);
			}
		}

		return container;
	}

	public ScansunResultContainer byPulseDuration(
			ScansunPulseDuration pulseDuration) {
		ScansunResultContainer container = new ScansunResultContainer();

		for (ScansunEvent event : events) {
			if (event.getPulseDuration() == pulseDuration) {
				container.addEvent(event);
			}
		}

		return container;
	}

	public ScansunResultContainer byMeanPowerCalibrationMode(
			ScansunMeanPowerCalibrationMode meanPowerCalibrationMode) {
		ScansunResultContainer container = new ScansunResultContainer();

		for (ScansunEvent event : events) {
			if (event.meanPowerCalibrationMode() == meanPowerCalibrationMode) {
				container.addEvent(event);
			}
		}

		return container;
	}

	public ScansunResultContainer byLocalDate(LocalDate day) {
		ScansunResultContainer container = new ScansunResultContainer();

		for (ScansunEvent event : events) {
			if (event.getLocalDate().equals(day)) {
				container.addEvent(event);
			}
		}

		return container;
	}

	/*
	 * public Map<ScansunSite, Iterable<LocalDate>> getSitedays() {
	 * 
	 * Map<ScansunSite, Iterable<LocalDate>> result = new HashMap<>();
	 * 
	 * for (final ScansunEvent event : events) { result.put(event.getSite(), new
	 * Iterable<LocalDate>() { LocalDate day = event.getLocalDate();
	 * 
	 * @Override public Iterator<LocalDate> iterator() { return new
	 * Iterator<LocalDate>() { // private LocalDate day= event.getLocalDate();
	 * 
	 * @Override public boolean hasNext() { return day != null; }
	 * 
	 * @Override public LocalDate next() { LocalDate nextDay = day.plusDays(1);
	 * return nextDay; }
	 * 
	 * @Override public void remove() { } }; } }); }
	 * 
	 * return result; }
	 */

	public Map<ScansunSite, Map<LocalDate, Set<ScansunEvent>>> asMap() {
		Map<ScansunSite, Map<LocalDate, Set<ScansunEvent>>> result = new HashMap<>();

		for (ScansunEvent event : events) {

			if (result.get(event.getSite()) == null) {
				result.put(event.getSite(),
						new HashMap<LocalDate, Set<ScansunEvent>>());
			}

			if (result.get(event.getSite()).get(event.getLocalDate()) == null) {
				result.get(event.getSite()).put(event.getLocalDate(),
						new HashSet<ScansunEvent>());
			}
			result.get(event.getSite()).get(event.getLocalDate()).add(event);
		}

		return result;
	}

	public Map<ScansunSite, SortedSet<LocalDate>> getSitedays() {
		Map<ScansunSite, SortedSet<LocalDate>> result = new HashMap<>();

		for (ScansunSite site : sites) {
			SortedSet<LocalDate> days = new TreeSet<LocalDate>(this
					.getEvents()
					.byMeanPowerCalibrationMode(
							ScansunMeanPowerCalibrationMode.CALIBRATED).asMap()
					.get(site).keySet());

			result.put(site, days);
		}

		return result;
	}

	@Override
	public Iterator<ScansunEvent> iterator() {
		return events.iterator();
	}

	@Override
	public int size() {
		return events.size();
	}

	public static ScansunResultContainer readEvents(Set<File> files) {
		ScansunResultContainer container = new ScansunResultContainer();

		for (File file : files) {
			try {
				Scanner scanner = new Scanner(file);
				while (scanner.hasNext()) {
					String line = scanner.nextLine();
					if (line.startsWith(COMMENT))
						continue;

					container.addEvent(LineParseTool.parseLine(line,
							new ScansunEventFacory(),
							ScansunEvent.EVENT_DELIMITER));
				}

				scanner.close();
			} catch (FileNotFoundException e) {
				log.printMsg("SCANSUN: Results file not found: ",
						Log.TYPE_ERROR, Log.MODE_VERBOSE);
			}
		}

		return container;

	}
}
