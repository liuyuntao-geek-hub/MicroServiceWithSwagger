
%put  WARNING: Executed on %sysfunc(strip(%qsysfunc(today(),worddate.))) at %sysfunc(strip(%qsysfunc(time(),timeampm.))).;

options fullstimer;


%LET userID 		= 	&UID;           		      		
%LET password 		= 	&password_PASSWORD;


%LET TDSrvr	= DWPROD2;			
%LET UsrTbl	= USERDATA_ENT;

/*

*Enter name of GL Spend file location;
%let spend_loc=/ephc/nshare/datafndn/medicaid/atrbn/final_output/SPEND;
                %put spend_loc: &spend_loc.;

*Location of Compressed Spend File (produced by Mitch Wise);
libname prod "&spend_loc.";
*/
*Final Spend output folder;
libname SPEND "/ephc/intlrprt/backup/data/SPEND";

*Name final spend file;
%let output_spend=201604_201703_201706; /*Add 3 months*/
                %put output_spend: &output_spend.;

*Identify spend_date_range;
%let spend_date=201604_201703_201706;
                %put spend_date: &spend_date.;

 %put  WARNING: Completed on %sysfunc(strip(%qsysfunc(today(),worddate.))) at %sysfunc(strip(%qsysfunc(time(),timeampm.))).;