/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */

package pl.imgw.jrat.scansun;

import java.util.HashSet;

import pl.imgw.jrat.scansun.ScansunEvent.EventType;
import pl.imgw.jrat.scansun.ScansunEvent.PulseDuration;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */

public class ScansunEventsContainer extends HashSet<ScansunEvent> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ScansunEventsContainer() {
    }

    public void addEvent(ScansunEvent event) {
	add(event);
    }

    public HashSet<String> getSiteNames() {
	HashSet<String> siteNames = new HashSet<>();

	for (ScansunEvent event : this) {
	    siteNames.add(event.getSiteName());
	}
	return siteNames;
    }

    public ScansunEventsContainer filterEvents() {
	return this;
    }

    public ScansunEventsContainer bySiteName(String siteName) {
	ScansunEventsContainer container = new ScansunEventsContainer();

	for (ScansunEvent event : this) {
	    if (event.getSiteName().equalsIgnoreCase(siteName)) {
		container.addEvent(event);
	    }
	}
	return container;
    }

    public ScansunEventsContainer byDay(ScansunDay day) {
	ScansunEventsContainer container = new ScansunEventsContainer();

	for (ScansunEvent event : this) {
	    if (event.getDay().equals(day)) {
		container.addEvent(event);
	    }
	}

	return container;
    }

    public ScansunEventsContainer byEventType(EventType eventType) {
	ScansunEventsContainer container = new ScansunEventsContainer();

	for (ScansunEvent event : this) {
	    if (event.isSolarRay() == eventType.isSolarRay()) {
		container.addEvent(event);
	    }
	}

	return container;
    }

    public ScansunEventsContainer byPulseDuration(PulseDuration pulseDuration) {
	ScansunEventsContainer container = new ScansunEventsContainer();

	for (ScansunEvent event : this) {
	    if (event.getPulseDuration() == pulseDuration) {
		container.addEvent(event);
	    }
	}

	return container;
    }

    public ScansunEventsContainer byMeanPowerCalibrationMode(boolean isMeanPowerCalibrated) {
	ScansunEventsContainer container = new ScansunEventsContainer();

	for (ScansunEvent event : this) {
	    if (event.isMeanPowerCalibrated() == isMeanPowerCalibrated) {
		container.addEvent(event);
	    }
	}

	return container;
    }

    public HashSet<ScansunEvent> getEvents() {
	return this;
    }

}
