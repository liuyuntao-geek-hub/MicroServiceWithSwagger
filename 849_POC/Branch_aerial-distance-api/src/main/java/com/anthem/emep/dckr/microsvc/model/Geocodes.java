package com.anthem.emep.dckr.microsvc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "geocodes")
public class Geocodes {

	@Id
	@Column(name = "id")
	private int key;
	@Column(name = "lat")
	private double lat;
	@Column(name = "lon")
	private double lon;

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	@Override
	public String toString() {
		return "Geocodes [key=" + key + ", lat=" + lat + ", lon=" + lon + ", getKey()=" + getKey() + ", getLat()="
				+ getLat() + ", getLon()=" + getLon() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

	public Geocodes(int key, double lat, double lon) {
		super();
		this.key = key;
		this.lat = lat;
		this.lon = lon;
	}

	public Geocodes() {

	}

}
