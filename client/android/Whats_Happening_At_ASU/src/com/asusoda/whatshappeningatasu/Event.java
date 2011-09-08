package com.asusoda.whatshappeningatasu;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class Event {
	private final String TAG = "Events";
	private String name, description, start_timeframe, end_timeframe;
	private Date start_timeframe_date, end_timeframe_date;
	private Location location;
	private Organization organization;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getStart_timeframe_date() {
		if(this.start_timeframe_date == null) {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				this.start_timeframe_date = formatter.parse(this.start_timeframe);
			} catch (ParseException e) {
				Log.e(TAG, "Couldn't parse star_timeframe date");
			}
		}
		return this.start_timeframe_date;
	}
	
	public String getStart_timeframe() {
		return this.start_timeframe;
	}
	
	public void setStart_timeframe(String start_timeframe) {
		this.start_timeframe = start_timeframe;
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			this.start_timeframe_date = formatter.parse(this.start_timeframe);
		} catch (ParseException e) {
			Log.e(TAG, "Couldn't parse star_timeframe date");
		}
	}
	
	public Date getEnd_timeframe_date() {
		if(this.end_timeframe_date == null) {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				this.end_timeframe_date = formatter.parse(this.end_timeframe);
			} catch (ParseException e) {
				Log.e(TAG, "Couldn't parse end_timeframe date");
			}
		}
		return this.end_timeframe_date;
	}
	
	public String getEnd_timeframe() {
		return this.end_timeframe;
	}
	
	public void setEnd_timeframe(String end_timeframe) {
		this.end_timeframe = end_timeframe;
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			this.end_timeframe_date = formatter.parse(this.end_timeframe);
		} catch (ParseException e) {
			Log.e(TAG, "Couldn't parse end_timeframe date");
		}
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Organization getOrganization() {
		return this.organization;
	}
	
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	@Override
	public String toString() {
		return this.name + " at " + this.location.toString();
	}
}
