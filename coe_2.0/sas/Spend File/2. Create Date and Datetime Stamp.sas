
/*First date of new quarter*/
%LET QUARTER='2017-08-01';

%LET VERSNDT=20170801;


/*Update Dates Quarterly; Update ADDLMONTHS monthly*/

%LET EFFDT24 = 		'2015-04-01'; 	
%LET EFFDT12 = 		'2016-04-01';   
%LET ENDDT = 		'2017-03-31';
%LET ADJDT = 		'2017-06-30';
%LET ADDLMONTHS = 	'2017-06-30';    /*Update monthly*/

%LET sEFFDT24 = 	'01Apr2015'd; 	
%LET sEFFDT12 = 	'01Apr2016'd; 	
%LET sENDDT = 		'31Mar2017'd;
%LET sADJDT = 		'30Jun2017'd;
%LET sADDLMONTHS = 	'30Jun2017'd;    /*Update monthly*/
 
%LET EFFDT12M = 	'2016-06-01'; /* This will be 2 months after the EFFDT12 date ---- used for baseline_months calculation*/
%LET ENDDTM = 		'2017-05-31'; /* This will be 2 months after the ENDDT date ---- used for baseline_months calculation*/



*Specify month run--e.g. YY=13 and MM=09 for September 2013 run;
%LET YY=17;
%LET MM=08;

/*Determine rolling 12 for visit counts
%let ROLL_IN='2014-11-01';
%let ROLL_OUT='2015-10-31';
*/
%let AFNTY_EFCTV='2017-08-01';
