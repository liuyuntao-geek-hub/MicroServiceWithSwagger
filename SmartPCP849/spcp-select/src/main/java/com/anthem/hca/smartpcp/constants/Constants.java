package com.anthem.hca.smartpcp.constants;

/** 
 * 
 * Copyright © <2018> Anthem, Inc.
 * 
 * Additional license information 
 * 
 * Description - Contains all the constants used in the service.
 * 
 * 
 * @author AF71111
 */
public class Constants {

	public static final String EQUAL = "=";
	public static final String QUESTION_MARK = "?";
	public static final String COMMA = ",";
	public static final String AND = "&";
	public static final String BING_PARAM_COUNTRY = "countryRegion";
	public static final String BING_PARAM_STATE = "adminDistrict";
	public static final String BING_PARAM_CITY = "locality";
	public static final String BING_PARAM_ZIP = "postalCode";
	public static final String BING_PARAM_ADDRESS = "addressLine";
	public static final String BING_PARAM_KEY = "key";
	public static final String BING_PARAM_OUTPUT = "output";
	public static final String BING_OUTPUT_JSON = "json";
	public static final String BING_RESOURCE_SETS = "resourceSets";
	public static final String BING_RESOURCES = "resources";
	public static final String BING_POINT = "point";
	public static final String BING_COORDINATES = "coordinates";
	public static final String COUNTRY_US = "US";

	public static final String OUTPUT_SUCCESS = "S";
	public static final String OUTPUT_UNSUCCESSFUL = "U";
	
	public static final String API_KEY = "apikey";
	public static final String BEARER = "Bearer";
	public static final String BASIC = "Basic";
	public static final String GRANT_TYPE = "grant_type";
	public static final String CLIENT_CREDENTIALS = "client_credentials";
	public static final String SCOPE = "scope";
	public static final String PUBLIC = "public";
	public static final String ACCESS_TOKEN ="access_token";
	
	public static final String REPORTING_CODE_AFFINITY = "A";
	public static final String REPORTING_CODE_MDO = "M";
	public static final String REPORTING_CODE_DEFAULT = "D";
	
	public static final String REPORTING_TEXT_AFFINITY = "Affinity assigned";
	public static final String REPORTING_TEXT_MDO = "MDO Assigned";
	public static final String REPORTING_TEXT_DEFAULT = "Default Assigned";

	

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	
	public static final String SMART_PCP = "SMARTPCP";
	public static final String DROOLS_APP_NAME = "spcp-drools";
	public static final String AFFINITY_APP_NAME = "spcp-affinity";
	
	public static final String MDO_APP_NAME = "spcp-mdo";

	public static final String ENGLISH = "ENG";

	public static final String AM = "AM";
	public static final String MA = "MA";
	public static final String A = "A";
	public static final String M = "M";

	public static final String TRUE = "Y";
	public static final String FALSE = "N";

	public static final String PCP = "PCP";
	public static final String PMG = "PMG";

	public static final String PCP_TYPE_I = "I";
	public static final String PCP_TYPE_P = "P";

	public static final String TRIM_AS = ") AS ";

	public static final String EMPTY_STRING = "";

	
	public static final String REGEX_SPECIAL_CHAR = "[^a-zA-Z0-9\\s+]";
	
	public static final String PCP_ASSIGNED_REQUEST_STATUS = "REQUEST_RECEIVED";
	
	public static final String PRODUCT_TYPE_DEFAULT = "ALL";
	
	
	public static final String REGEX = "[a-z-A-Z]*";
	
	public static final String TARGET_TABLE = "PROVIDER_INFO";
	public static final String RANKING_TABLE = "HCA_PROV_RANKING";

	public static final String PEDIATRICS_SPCLTY = "PEDIATRIC";
	public static final String OBGYN_SPCLTY = "OBGYN";
	
	public static final String VBP_FLAG="Y";
	public static final String ENG_CON = "ENG";
	
	public static final String PCP_FETCHED = "PCP_FETCHED";
	public static final String PCP_ASSIGNED_TO_MBR = "PCP_ASSIGNED_TO_MBR";
	public static final String NO_PCP_IN_MEMBER_TABLE = "NO_PCP_IN_MEMBER_TABLE";
	public static final String NO_VALID_PCP_IDENTIFIED = "NO_VALID_PCP_IDENTIFIED";
	public static final String NO_PCP_ASSIGNED_TO_MBR = "NO_PCP_ASSIGNED_TO_MBR";
	
	public static final String VALID_PCPS = "VALID_PCPS";
	
	public static final String PCP_ASSIGNED = "PCP_ASSIGNED";
	
	public static final String RULES_EXTRACTED = "Rules Extracted";
	private Constants() {

	}

}
