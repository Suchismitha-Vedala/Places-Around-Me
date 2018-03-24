package com.imissionlabs.placesaroundme;

import com.google.android.gms.maps.model.LatLng;

public class Place {
	
	private String name;
	private String address;
	private int distance;
	private LatLng location;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public LatLng getLocation() {
		return location;
	}
	public void setLocation(LatLng location) {
		this.location = location;
	}

}
