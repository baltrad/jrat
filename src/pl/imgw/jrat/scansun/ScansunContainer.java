/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun;

import java.util.ArrayList;
import java.util.Iterator;

import pl.imgw.jrat.tools.out.LogHandler;
import pl.imgw.jrat.tools.out.Logging;

/**
 * 
 * /Class description/
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunContainer {

    private ArrayList<ScansunEvent> events;
    private Integer eventsNumber;
    private boolean hasResults;
    private boolean hasSolarRays;

    public ScansunContainer() {
	this.events = new ArrayList<ScansunEvent>();
	this.hasResults = false;
	this.hasSolarRays = false;
    }

    public boolean hasResults() {
	return hasResults;
    }

    public boolean hasSolarRays() {
	return hasSolarRays;
    }

    public ArrayList<ScansunEvent> getEvents() {
	return events;
    }

    public Integer getEventsNumber() {
	return eventsNumber;
    }

    public void saveEvent() {

	if (!hasResults) {
	    LogHandler.getLogs().displayMsg("SCANSUN: no results found", Logging.WARNING);
	    return;
	}

	Iterator<ScansunEvent> itr = events.iterator();
	while (itr.hasNext()) {
	    if (!ScansunFileHandler.saveEvent(itr.next())) {
		LogHandler.getLogs().displayMsg("SCANSUN: Cannot save result", LogHandler.ERROR);
	    }
	}

	LogHandler.getLogs().displayMsg("SCANSUN: Saving results completed", LogHandler.NORMAL);
    }

    public void resetContainer() {
	events.clear();
	hasResults = false;
	hasSolarRays = false;
    }

    public void addEvent(ScansunEvent event) {

	events.add(event);
	eventsNumber = events.size();
	hasResults = true;

	if (!hasSolarRays() && event.isSolarRay()) {
	    hasSolarRays = true;
	}
    }

}
