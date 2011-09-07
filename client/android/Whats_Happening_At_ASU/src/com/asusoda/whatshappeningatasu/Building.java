package com.asusoda.whatshappeningatasu;

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
}
