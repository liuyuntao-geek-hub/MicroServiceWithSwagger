package com.anthem.hca.smartpcp.drools.preprocessor;

import java.util.List;
import java.util.ArrayList;

/**
 * The RulesMatrix class is used to store Excel Rules data in a Matrix format
 * which is used for pre-processing the Rules before firing them.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.1
 */

public class RulesMatrix {

	private List<String> lobs;
	private List<String> markets;
	private List<String> products;
	private List<String> assignmentTypes;
	private List<String> assignmentMethods;

	private int initialCapacity;

	/**
	 * Create a RulesMatrix Object and initialize the individual columns with an
	 * Initial Capacity of elements passed in the parameter.
	 * 
	 * @param initialCapacity The initial number of elements to hold in each column
	 */
	public RulesMatrix(int initialCapacity) {
		this.initialCapacity = initialCapacity;

		lobs = new ArrayList<>(initialCapacity);
		markets = new ArrayList<>(initialCapacity);
		products = new ArrayList<>(initialCapacity);
		assignmentTypes = new ArrayList<>(initialCapacity);
		assignmentMethods = new ArrayList<>(initialCapacity);
	}

	/**
	 * Create a RulesMatrix Object and initialize the individual columns with the
	 * Parameters supplied in the Constructor.
	 * 
	 * @param lobs              A list of LOBs
	 * @param markets           A list of Markets
	 * @param products          A list of Products
	 * @param assignmentTypes   A list of Assignment Types
	 * @param assignmentMethods A list of Assignment Methods
	 */
	public RulesMatrix(List<String> lobs, List<String> markets, List<String> products, List<String> assignmentTypes, List<String> assignmentMethods) {
		this.lobs = lobs;
		this.markets = markets;
		this.products = products;
		this.assignmentTypes = assignmentTypes;
		this.assignmentMethods = assignmentMethods;
	}

	/**
	 * This method is used to create a Sub-Matrix from the Current Matrix
	 * using the list of Indices provided in the parameter. Elements from
	 * all the Columns matching the Indices are taken to form the new Matrix.
	 * 
	 * @param  indexes     The list of indices from the original Matrix
	 * @return The Sub-Matrix
	 * @see    RulesMatrix
	 */
	public RulesMatrix subMatrix(List<Integer> indexes) {
		List<String> subLobs = new ArrayList<>(initialCapacity);
		List<String> subMarkets = new ArrayList<>(initialCapacity);
		List<String> subProducts = new ArrayList<>(initialCapacity);
		List<String> subAssignmentTypes = new ArrayList<>(initialCapacity);
		List<String> subAssignmentMethods = new ArrayList<>(initialCapacity);

		indexes.forEach(idx -> {
			subLobs.add(this.getLobs().get(idx));
			subMarkets.add(this.getMarkets().get(idx));
			subProducts.add(this.getProducts().get(idx));
			subAssignmentTypes.add(this.getAssignmentTypes().get(idx));
			subAssignmentMethods.add(this.getAssignmentMethods().get(idx));
		});

		return new RulesMatrix(subLobs, subMarkets, subProducts, subAssignmentTypes, subAssignmentMethods);
	}

	/**
	 * This method is used to get the LOB Column from the Rules Matrix.
	 * 
	 * @param  None
	 * @return The list of LOBs
	 */
	public List<String> getLobs() {
		return lobs;
	}

	/**
	 * This method is used to set the LOB Column in the Rules Matrix.
	 * 
	 * @param  lobs The list of LOBs
	 * @return None
	 */
	public void setLobs(List<String> lobs) {
		this.lobs = lobs;
	}

	/**
	 * This method is used to get the Market Column from the Rules Matrix.
	 * 
	 * @param  None
	 * @return The list of Markets
	 */
	public List<String> getMarkets() {
		return markets;
	}

	/**
	 * This method is used to set the Market Column in the Rules Matrix.
	 * 
	 * @param  markets The list of Markets
	 * @return None
	 */
	public void setMarkets(List<String> markets) {
		this.markets = markets;
	}

	/**
	 * This method is used to get the Product Column from the Rules Matrix.
	 * 
	 * @param  None
	 * @return The list of Products
	 */
	public List<String> getProducts() {
		return products;
	}

	/**
	 * This method is used to set the Product Column in the Rules Matrix.
	 * 
	 * @param  products The list of Products
	 * @return None
	 */
	public void setProducts(List<String> products) {
		this.products = products;
	}

	/**
	 * This method is used to get the Assignment Type Column from the Rules Matrix.
	 * 
	 * @param  None
	 * @return The list of Assignment Types
	 */
	public List<String> getAssignmentTypes() {
		return assignmentTypes;
	}

	/**
	 * This method is used to set the Assignment Type Column in the Rules Matrix.
	 * 
	 * @param  assignmentTypes The list of Assignment Types
	 * @return None
	 */
	public void setAssignmentTypes(List<String> assignmentTypes) {
		this.assignmentTypes = assignmentTypes;
	}

	/**
	 * This method is used to get the Assignment Method Column from the Rules Matrix.
	 * 
	 * @param  None
	 * @return The list of Assignment Methods
	 */
	public List<String> getAssignmentMethods() {
		return assignmentMethods;
	}

	/**
	 * This method is used to set the Assignment Method Column in the Rules Matrix.
	 * 
	 * @param  assignmentMethods The list of Assignment Methods
	 * @return None
	 */
	public void setAssignmentMethods(List<String> assignmentMethods) {
		this.assignmentMethods = assignmentMethods;
	}

}
