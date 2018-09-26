package com.anthem.hca.smartpcp.drools.preprocessor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.anthem.hca.smartpcp.drools.rules.AbstractRules;

/**
 * The RulesUpdater class is used to update the Rules before firing them in Drools.
 * This means that the initial state of the Rules object is updated (if required) by
 * comparing it with the cached RulesMatrix data.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.0
 */

public class RulesUpdater {

	private RulesMatrix matrix;

	private String market;
	private String product;
	private String assignmentType;
	private String assignmentMethod;

	/**
	 * Constructor to create an object or RulesUpdater
	 * 
	 * @param preProcessor The RulesPreprocessor object
	 * @see   RulesPreprocessor
	 */
	public RulesUpdater(RulesMatrix matrix) {
		this.matrix = matrix;
	}

	/**
	 * This method is used to update the initial Rules object that is passed in the
	 * parameter by comparing each Input attribute of the Rules object with the Rules
	 * Matrix that has been created earlier by reading the Excel file data.
	 * 
	 * @param  rule The Rules object containing the initial state of data 
	 * @return None 
	 */
	public void updateRule(AbstractRules rule) {
		// Initially set parameters to the incoming values
		setMarket(rule.getMarket());
		setProduct(rule.getProduct());
		setAssignmentType(rule.getAssignmentType());
		setAssignmentMethod(rule.getAssignmentMethod());

		// Check if the incoming Market is present in the Matrix
		if (!matrix.getMarkets().contains(rule.getMarket())) {
			setMarket("ALL");
		}

		// Find the indexes in the Matrix where Market has matched
		List<Integer> allMarketIndexes = IntStream.range(0, matrix.getMarkets().size()).boxed().filter(idx -> matrix.getMarkets().get(idx).equals(getMarket())).collect(Collectors.toList());

		// Shrink the Matrix to contain data for the rows where Market has matched
		setMatrix(matrix.subMatrix(allMarketIndexes));

		// Check if the incoming Product is present in the Matrix
		if (!matrix.getProducts().contains(rule.getProduct())) {
			setProduct("ALL");
		}

		// Find the indexes in the Matrix where Product has matched
		List<Integer> allProductIndexes = IntStream.range(0, matrix.getProducts().size()).boxed().filter(idx -> matrix.getProducts().get(idx).equals(getProduct())).collect(Collectors.toList());

		// Shrink the Matrix to contain data for the rows where Product has matched
		setMatrix(matrix.subMatrix(allProductIndexes));

		// Check if the incoming Assignment Type is present in the Matrix
		if (!matrix.getAssignmentTypes().contains(rule.getAssignmentType())) {
			setAssignmentType("ALL");
		}

		// Find the indexes in the Matrix where Assignment Type has matched
		List<Integer> allAssignTypeIndexes = IntStream.range(0, matrix.getAssignmentTypes().size()).boxed().filter(idx -> matrix.getAssignmentTypes().get(idx).equals(getAssignmentType())).collect(Collectors.toList());

		// Shrink the Matrix to contain data for the rows where Assignment Type has matched
		setMatrix(matrix.subMatrix(allAssignTypeIndexes));

		// Check if the incoming Assignment Method is present in the Matrix
		if (!matrix.getAssignmentMethods().contains(rule.getAssignmentMethod())) {
			setAssignmentMethod("ALL");
		}

		// Find the indexes in the Matrix where Assignment Method has matched
		List<Integer> allAssignMethodIndexes = IntStream.range(0, matrix.getAssignmentMethods().size()).boxed().filter(idx -> matrix.getAssignmentMethods().get(idx).equals(getAssignmentMethod())).collect(Collectors.toList());

		// Shrink the Matrix to contain data for the rows where Assignment Method has matched
		setMatrix(matrix.subMatrix(allAssignMethodIndexes));

		// Update the incoming Rule parameters
		rule.setMarket(getMarket());
		rule.setProduct(getProduct());
		rule.setAssignmentType(getAssignmentType());
		rule.setAssignmentMethod(getAssignmentMethod());
	}

	/**
	 * This method is used to get the current Matrix contents for the Rule's Agenda Group.
	 * 
	 * @param  None
	 * @return The Rules Matrix
	 * @see    RulesMatrix
	 */
	public RulesMatrix getMatrix() {
		return matrix;
	}

	/**
	 * This method is used to set the current Matrix contents for the Rule's Agenda Group.
	 * 
	 * @param  matrix The Rules Matrix
	 * @return None
	 */
	public void setMatrix(RulesMatrix matrix) {
		this.matrix = matrix;
	}

	/**
	 * This method is used to get the Updated Market after Pre-Processing.
	 * 
	 * @param  None
	 * @return The Updated Market
	 */
	public String getMarket() {
		return market;
	}

	/**
	 * This method is used to set the Updated Market after Pre-Processing.
	 * 
	 * @param  market The Updated Market
	 * @return None
	 */
	public void setMarket(String market) {
		this.market = market;
	}

	/**
	 * This method is used to get the Updated Product after Pre-Processing.
	 * 
	 * @param  None
	 * @return The Updated Product
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * This method is used to set the Updated Product after Pre-Processing.
	 * 
	 * @param  product The Updated Product
	 * @return None
	 */
	public void setProduct(String product) {
		this.product = product;
	}

	/**
	 * This method is used to get the Updated Assignment Type after Pre-Processing.
	 * 
	 * @param  None
	 * @return The Updated Assignment Type
	 */
	public String getAssignmentType() {
		return assignmentType;
	}

	/**
	 * This method is used to set the Updated Assignment Type after Pre-Processing.
	 * 
	 * @param  assignmentType The Updated Assignment Type
	 * @return None
	 */
	public void setAssignmentType(String assignmentType) {
		this.assignmentType = assignmentType;
	}

	/**
	 * This method is used to get the Updated Assignment Method after Pre-Processing.
	 * 
	 * @param  None
	 * @return The Updated Assignment Method
	 */
	public String getAssignmentMethod() {
		return assignmentMethod;
	}

	/**
	 * This method is used to set the Updated Assignment Method after Pre-Processing.
	 * 
	 * @param  assignmentMethod The Updated Assignment Method
	 * @return None
	 */
	public void setAssignmentMethod(String assignmentMethod) {
		this.assignmentMethod = assignmentMethod;
	}

}
