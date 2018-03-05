package com.hivePoc.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Entity
@Table(name="test")
public class HiveBO {

	@Id
	private double col1;
	private float col2;
	public double getCol1() {
		return col1;
	}
	public void setCol1(double col1) {
		this.col1 = col1;
	}
	public float getCol2() {
		return col2;
	}
	public void setCol2(float col2) {
		this.col2 = col2;
	}
	
	
	
	
}
