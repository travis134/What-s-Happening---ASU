package com.asusoda.whatshappeningatasu;

import java.util.ArrayList;
import java.util.List;

public class SaveData {
	private int radius;
	private int delay;
	private boolean formatMetric;
	private List<Event> eventsList;
	
	public SaveData(int radius, int delay, boolean formatMetric, ArrayList<Event> eventsList) {
		this.radius = radius;
		this.delay = delay;
		this.formatMetric = formatMetric;
		this.eventsList = eventsList;
	}
	
	public int getRadius() {
		return this.radius;
	}
	
	public int getDelay() {
		return this.delay;
	}
	
	public boolean getFormatMetric() {
		return this.formatMetric;
	}
	
	public List<Event> getEventsList() {
		return this.eventsList;
	}
}
