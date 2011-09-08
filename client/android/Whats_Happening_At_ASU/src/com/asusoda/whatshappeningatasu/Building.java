package com.asusoda.whatshappeningatasu;

import android.net.Uri;

public class Building {
	private String abbreviation, name;
	private float latitude, longitude;
	
	public String getAbbreviation() {
		return this.abbreviation;
	}
	
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public float getLatitude() {
		return this.latitude;
	}
	
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	
	public float getLongitude() {
		return this.longitude;
	}
	
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	public Uri getGeoUri() {
		return Uri.parse("geo:" + this.getLatitude() + "," + this.getLongitude() + "?z=18");
	}
	
	public Uri getDirections(){
		return Uri.parse("http://maps.google.com/maps?daddr=" + this.getLatitude() + "," + this.getLongitude() + "&dirflg=w");
	}
	
	@Override
	public String toString() {
		return this.abbreviation;
	}
}
