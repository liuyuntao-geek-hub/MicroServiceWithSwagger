package com.hivePoc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="test12")
public class HiveBO  {

	@Id
	@GeneratedValue
	@Column(name="id")
	private int col1;
	@Column(name="name")
	private String col2;
	public int getCol1() {
		return col1;
	}
	public void setCol1(int col1) {
		this.col1 = col1;
	}
	public String getCol2() {
		return col2;
	}
	public void setCol2(String col2) {
		this.col2 = col2;
	}
	
	
	
	
}
