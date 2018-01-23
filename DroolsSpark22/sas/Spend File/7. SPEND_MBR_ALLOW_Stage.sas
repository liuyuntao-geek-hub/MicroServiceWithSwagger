* 
Build date parameters to get rolling 12 months based on system date.
Start date is the first of the months 13 months ago.
End date is the last of the month 2 months ago.
For example, for any day in Mar get First day of previous Feb to last day of Jan 
;

		%put  &T. Executed on %sysfunc(strip(%qsysfunc(today(),worddate.))) at %sysfunc(strip(%qsysfunc(time(),timeampm.))).;

proc contents data=&SPEND_FILE;
run;



*%check_exists_rename_old(&FLDR.,SPEND_MEMBER_ALLOW_S);

*Create Data Set of rolling 12 months of allowed dollars by MCID;
proc sql;
CREATE TABLE &FLDR..SPEND_MBR_ALLOW_S (COMPRESS=BINARY SORTEDBY=MEMBER_MCID) AS
SELECT 
MCID LENGTH=11 AS MEMBER_MCID
, &sEFFDT12. as sEFFDT12  format=yymmdd10.
, &sENDDT. as sENDDT format=yymmdd10.
, SUM(INPAT_ALLOWED) AS ALLOWED_INPATIENT_NO_TRANSPLANT format=dollar16.2
, SUM(Trnsplnt_ALLOWED ) AS ALLOWED_TRANSPLANT format=dollar16.2
, SUM(OUTPAT_ALLOWED) AS ALLOWED_OUTPATIENT format=dollar16.2
, SUM(PROF_ALLOWED) AS ALLOWED_PROFESSIONAL format=dollar16.2
, SUM(RX_ALLOWED ) AS ALLOWED_RX  format=dollar16.2
, SUM(CPTTN_ALLOWED) AS ALLOWED_CAPITATED format=dollar16.2
, SUM(SUM(INPAT_ALLOWED, Trnsplnt_ALLOWED)) AS ALLOWED_INPATIENT format=dollar16.2
, SUM(CPTTN_ALLOWED) AS ALLOWED_OTHER format=dollar16.2
, SUM(SUM(INPAT_ALLOWED, Trnsplnt_ALLOWED, OUTPAT_ALLOWED, PROF_ALLOWED, RX_ALLOWED, CPTTN_ALLOWED)) AS ALLOWED_TOTAL format=dollar16.2
FROM &SPEND_FILE
 
GROUP BY MCID
ORDER BY MCID;

quit;


proc contents data=&FLDR..SPEND_MBR_ALLOW_S varnum;
run;

*%check_exists_delete_old(&FLDR.,SPEND_MEMBER_ALLOW_S);


		%put  &T. Completed on %sysfunc(strip(%qsysfunc(today(),worddate.))) at %sysfunc(strip(%qsysfunc(time(),timeampm.))).;

