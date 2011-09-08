package com.asusoda.whatshappeningatasu;

public class Location {
	private Building building;
	private String room;
	
	public Building getBuilding() {
		return this.building;
	}
	
	public void setBuilding(Building building) {
		this.building = building;
	}
	
	public String getRoom() {
		return this.room;
	}
	
	public void setRoom(String room) {
		this.room = room;
	}
	
	@Override
	public String toString() {
		return this.building.toString() + " - " + this.room;
	}
}
