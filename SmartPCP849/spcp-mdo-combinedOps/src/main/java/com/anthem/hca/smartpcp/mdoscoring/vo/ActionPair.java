package com.anthem.hca.smartpcp.mdoscoring.vo;


import javax.validation.constraints.NotNull;
public class ActionPair<K, V> {

	@NotNull(message="Key can not be Null")
	private K key;
	
	@NotNull(message="Value can not be Null")
	private V value;
	
	
	public ActionPair() {
		super();
	}


	public ActionPair( K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	public K getKey() {
		return key;
	}
	
	public void setKey( K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}
	 
	public void setValue(V value) {
		this.value = value;
	}

}
