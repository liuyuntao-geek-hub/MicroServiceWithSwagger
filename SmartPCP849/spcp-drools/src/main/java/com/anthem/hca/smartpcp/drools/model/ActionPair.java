package com.anthem.hca.smartpcp.drools.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * The ActionPair class encapsulates the model of a Drools Action where
 * there are two Actionable items. One is considered as the Key and the other is the Value.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.1
 */

public class ActionPair<K, V> {

	@ApiModelProperty(required = true, dataType = "Object", notes = "Contains the Key element of the Pair")
	private K key;

	@ApiModelProperty(required = true, dataType = "Object", notes = "Contains the Value element of the Pair")
	private V value;

	/**
	 * Constructor to create an ActionPair object.
	 * 
	 * @param key   The Key
	 * @param value The Value
	 */
	public ActionPair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * This method is used to get the Key of the ActionPair.
	 * 
	 * @param  None
	 * @return The Key
	 */
	public K getKey() {
		return key;
	}

	/**
	 * This method is used to set the Key of the ActionPair.
	 * 
	 * @param  key The Key
	 * @return None
	 */
	public void setKey(K key) {
		this.key = key;
	}

	/**
	 * This method is used to get the Value of the ActionPair.
	 * 
	 * @param  None
	 * @return The Value
	 */
	public V getValue() {
		return value;
	}

	/**
	 * This method is used to set the Value of the ActionPair.
	 * 
	 * @param  value The Value
	 * @return None
	 */
	public void setValue(V value) {
		this.value = value;
	}

}
