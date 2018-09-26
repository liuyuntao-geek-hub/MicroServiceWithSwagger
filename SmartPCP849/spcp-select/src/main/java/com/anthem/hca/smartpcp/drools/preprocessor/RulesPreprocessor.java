package com.anthem.hca.smartpcp.drools.preprocessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.anthem.hca.smartpcp.constants.DroolsConstants;
import com.anthem.hca.smartpcp.drools.rules.AgendaGroup;
import com.anthem.hca.smartpcp.drools.rules.RulesRegex;

/**
 * The RulesPreprocessor class is used to pre-process the Rules before firing them in Drools.
 * This means that the initial Rules are checked with the actual Rules data in the Excel sheets
 * and they are updated (if neccessary) in order to get compared with the 'ALL' placeholder
 * kept in the Excel files.
 *
 * @author  Saptarshi Dey (AF66853)
 * @version 1.2
 */

@Component
public class RulesPreprocessor {

	@Autowired
	private Environment environment;

	private Map<String, RulesMatrix> data;

	/**
	 * This method is used to get the RulesMatrix data for all the Agenda Groups
	 * 
	 * @param  None
	 * @return The Excel Data as a Map of (AgendaGroup, RulesMatrix)
	 */
	public Map<String, RulesMatrix> getData() {
		return data;
	}

	/**
	 * This method is used to initialize the RulesMatrix for different Agenda Groups
	 * during Drools application startup and refresh. After it has been created, one
	 * can use the cached data to Pre-process the Rules before firing them.
	 * 
	 * @param  streams A List of InputStream containing Excel data for each Agenda Group 
	 * @return None
	 * @throws IOException If problem occurs during reading the Excel files
	 * @see    AgendaGroup
	 * @see    RulesRegex 
	 */
	public void createMatrix(List<InputStream> streams) throws IOException {
		int agendaCapacity = Integer.parseInt(environment.getProperty("spcp.business.rule.decisiontable.agenda.types"));
		int firstDataRow = Integer.parseInt(environment.getProperty("spcp.business.rule.decisiontable.datarow.start"));
		int agendaGroupColumn = Integer.parseInt(environment.getProperty("spcp.business.rule.decisiontable.agendagroup.column"));
		int initialCapacity = Integer.parseInt(environment.getProperty("spcp.business.rule.decisiontable.initial.capacity"));

		data = new HashMap<>(agendaCapacity);
		Arrays.stream(AgendaGroup.values()).forEach(ag -> data.put(ag.getValue(), new RulesMatrix(initialCapacity)));

		for (InputStream is: streams) {
			try (Workbook workbook = new HSSFWorkbook(is)) {
				Sheet sheet = workbook.getSheetAt(0);

				for (int rowIndex = firstDataRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
					Row row = sheet.getRow(rowIndex);
					if(row != null) {
						Cell agendaGroupCell = row.getCell(agendaGroupColumn);
						if(agendaGroupCell != null) {
							String agendaGroup = agendaGroupCell.getStringCellValue().replaceAll(RulesRegex.DOUBLE_QUOTES, "");
							createMatrixColumn(DroolsConstants.LOB, agendaGroup, row.getCell(1).getStringCellValue().replaceAll(RulesRegex.DOUBLE_QUOTES, ""));
							createMatrixColumn(DroolsConstants.MARKET, agendaGroup, row.getCell(2).getStringCellValue().replaceAll(RulesRegex.DOUBLE_QUOTES, ""));
							createMatrixColumn(DroolsConstants.PRODUCT, agendaGroup, row.getCell(3).getStringCellValue().replaceAll(RulesRegex.DOUBLE_QUOTES, ""));
							createMatrixColumn(DroolsConstants.ASSIGNMENT_TYPE, agendaGroup, row.getCell(4).getStringCellValue().replaceAll(RulesRegex.DOUBLE_QUOTES, ""));
							createMatrixColumn(DroolsConstants.ASSIGNMENT_METHOD, agendaGroup, row.getCell(5).getStringCellValue().replaceAll(RulesRegex.DOUBLE_QUOTES, ""));
						}
					}
				}
			} finally {
				is.close();
			}
		}
	}

	/**
	 * This method is used to initialize the RulesMatrix Columns for a specific
	 * Agenda Group. The data is read from the Excel files and filled into the
	 * different columns in the Rules Matrix. This is a child method that is called
	 * from 'createMatrix' method.
	 * 
	 * @param  attr        Column header; whether it is LOB or MARKET etc.
	 * @param  agendaGroup The Agenda Group of the data.
	 * @param  value       The actual value to be inserted in the Column cell. 
	 * @return None 
	 */
	private void createMatrixColumn(String attr, String agendaGroup, String value) {
		switch (attr) {
		case DroolsConstants.LOB:
			data.get(agendaGroup).getLobs().add(value);
			break;
		case DroolsConstants.MARKET:
			data.get(agendaGroup).getMarkets().add(value);	
			break;
		case DroolsConstants.PRODUCT:
			data.get(agendaGroup).getProducts().add(value);
			break;
		case DroolsConstants.ASSIGNMENT_TYPE:
			data.get(agendaGroup).getAssignmentTypes().add(value);
			break;
		case DroolsConstants.ASSIGNMENT_METHOD:
			data.get(agendaGroup).getAssignmentMethods().add(value);
			break;
		default:
			break;
		}
	}

}
