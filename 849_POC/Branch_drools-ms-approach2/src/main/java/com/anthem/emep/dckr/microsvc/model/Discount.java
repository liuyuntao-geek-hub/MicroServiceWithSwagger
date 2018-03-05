package com.anthem.emep.dckr.microsvc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Discount")
public class Discount {
	
	@Id
	@Column(name="id")
	private Integer key;
	@Column(name="state")
	private String state;
	@Column(name="discount")
	private Integer discount;
	
	public Integer getKey() {
		return key;
	}
	public void setKey(Integer key) {
		this.key = key;
	}
	@Override
	public String toString() {
		return "Discount [key=" + key + ", state=" + state + ", discount=" + discount + "]";
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Integer getDiscount() {
		return discount;
	}
	public void setDiscount(Integer discount) {
		this.discount = discount;
	}
	
	

}
