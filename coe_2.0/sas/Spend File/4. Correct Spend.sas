
proc contents data=Spend.S_&spend_date._D   varnum;
run;


data SPEND.S_&spend_date.  (compress=binary keep=MCID DrvdDOS MBU PRODCF FUNDCF QUARTR START END PMNT_TYPE ALLOWED PAID);
length MCID $11 DrvdDOS 8 MONTH $6 ;
set Spend.S_&spend_date._D ;

 /*rename=(MONTH=OLD_MONTH));*/

/* MONTH=trim(put(OLD_MONTH,YYMMN6.));*/
year=substr(MONTH,1,4);
mon=substr(MONTH,5,2);
if mon in ("01","03","05","07","08","10","12") then day="31";
else if mon in ("04","06","09","11") then day="30";
else if mon="02" then do;
if year in ("2008","2012","2016","2020") then day="29";
else day="28";
end;
DrvdDOS=input(strip(mon)||strip(day)||strip(year), mmddyy8.);
format DrvdDOS mmddyy10.;
run;



proc datasets library=SPEND nolist;
                modify S_&spend_date. ;
                index create mcid_DrvdDOS=(mcid DrvdDOS);
quit;

/*
proc contents data=SPEND.S_&spend_date.;
run;
*/
proc sql;
create table SPEND.S_&spend_date._sum  as
select PMNT_TYPE,sum(ALLOWED)as ALLOWED_TOTAL,sum(PAID) as PAID_TOTAL
from SPEND.S_&spend_date. 
group by 1
;
quit;

proc print data=SPEND.S_&spend_date._sum ;
title1 "SPEND.S_&spend_date._sum";
                var _all_;
                sum ALLOWED_TOTAL PAID_TOTAL;
                format ALLOWED_TOTAL PAID_TOTAL dollar25.2;
run;


proc contents data=SPEND.S_&spend_date. ;
run;

proc sort data= SPEND.S_&spend_date.  force; by MCID DrvdDOS; run;


data SPEND.SPEND_&spend_date.  (compress=binary index=(MCID) pointobs=yes sortedby=MCID DrvdDOS);
                set SPEND.S_&spend_date. ;
                where find(MCID,".")=0 and MCID ne "";
                by MCID DrvdDOS;

                if PMNT_TYPE='CAP' then do;
                                                                CPTTN_ALLOWED+ALLOWED; 
                                                                CPTTN_PAID+PAID;
                                                                end;
                else do;
                                                                CPTTN_ALLOWED+0;
                                                                CPTTN_PAID+0;
                end;

                if PMNT_TYPE='INPT' then do;
                                                                INPAT_ALLOWED+ALLOWED;
                                                                INPAT_PAID+PAID;
                                                                end;
                else do;
                                                                INPAT_ALLOWED+0;
                                                                INPAT_PAID+0;
                end;

                if PMNT_TYPE='OUTPT' then do;
                                                                OUTPAT_ALLOWED+ALLOWED;
                                                                OUTPAT_PAID+PAID;
                                                                end;
                else do;
                                                                OUTPAT_ALLOWED+0;
                                                                OUTPAT_PAID+0;
                end;

                if PMNT_TYPE='PHRCY' then do;
                                                                RX_ALLOWED+ALLOWED;
                                                                RX_PAID+PAID;
                                                                end;
                else do;
                                                                RX_ALLOWED+0;
                                                                RX_PAID+0;
                end;

                if PMNT_TYPE='PROF' then do;
                                                                PROF_ALLOWED+ALLOWED;
                                                                PROF_PAID+PAID;
                                                                end;
                else do;
                                                                PROF_ALLOWED+0;
                                                                PROF_PAID+0;
                end;

                if substr(PMNT_TYPE,1,8)='TRNSPLNT' then do;
                                                                TRNSPLNT_ALLOWED+ALLOWED;
                                                                TRNSPLNT_PAID+PAID;
                                                                end;
                else do;
                                                                TRNSPLNT_ALLOWED+0;
                                                                TRNSPLNT_PAID+0;
                end;

                                if last.DrvdDOS then do;
                                                ALLOWED_TOTAL=CPTTN_ALLOWED+INPAT_ALLOWED+OUTPAT_ALLOWED+RX_ALLOWED+PROF_ALLOWED+TRNSPLNT_ALLOWED;
                                                PAID_TOTAL=CPTTN_PAID+INPAT_PAID+OUTPAT_PAID+RX_PAID+PROF_PAID+TRNSPLNT_PAID;
                                                output;
                CPTTN_ALLOWED=0;
                CPTTN_PAID=0;
                INPAT_ALLOWED=0;
                INPAT_PAID=0;
                OUTPAT_ALLOWED=0;
                OUTPAT_PAID=0;
                RX_ALLOWED=0;
                RX_PAID=0;
                PROF_ALLOWED=0;
                PROF_PAID=0;
                TRNSPLNT_ALLOWED=0;
                TRNSPLNT_PAID=0;
                                end;
                drop allowed paid pmnt_type;
                format  
                CPTTN_ALLOWED
                CPTTN_PAID
                INPAT_ALLOWED
                INPAT_PAID
                OUTPAT_ALLOWED
                OUTPAT_PAID
                RX_ALLOWED
                RX_PAID
                PROF_ALLOWED
                PROF_PAID
                TRNSPLNT_ALLOWED
                TRNSPLNT_PAID
                ALLOWED_TOTAL
                PAID_TOTAL
                dollar10.2;
run;

/*241123214*/
proc contents data=SPEND.SPEND_&spend_date.  varnum;
run;




%put  &T. Completed on %sysfunc(strip(%qsysfunc(today(),worddate.))) at %sysfunc(strip(%qsysfunc(time(),timeampm.))).;







