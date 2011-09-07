package com.asusoda.whatshappeningatasu;

import java.net.URL;

public class Organization {
	private String name, description;
	private URL website;
	private User owner;
	
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
	
	public URL getWebsite() {
		return this.website;
	}
	
	public void setWebsite(URL website) {
		this.website = website;
	}
	
	public User getOwner() {
		return this.owner;
	}
	
	public void setOwner(User owner) {
		this.owner = owner;
	}
}
