/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.scansun.data;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:przemyslaw.jacewicz@imgw.pl">Przemyslaw Jacewicz</a>
 * 
 */
public class ScansunScanResult extends AbstractSet<ScansunEvent> {

	private Set<ScansunEvent> events;

	public ScansunScanResult() {
		events = new HashSet<ScansunEvent>();
	}

	@Override
	public boolean add(ScansunEvent event) {
		return events.add(event);
	}

	@Override
	public Iterator<ScansunEvent> iterator() {
		return events.iterator();
	}

	@Override
	public int size() {
		return events.size();
	}

	public boolean hasResults() {
		return !events.isEmpty();
	}
}
