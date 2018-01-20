
%put  &T. Executed on %sysfunc(strip(%qsysfunc(today(),worddate.))) at %sysfunc(strip(%qsysfunc(time(),timeampm.))).;


proc means data=SPEND.SPEND_&spend_date.  sum mean maxdec=2 ;
title1 "Spend File";
                *title2 "Baseline: %UPCASE(%substr(&EFFDT12.,2,11)) - %UPCASE(%substr(&ENDDT.,2,11))";
                *title3 "Paid through: %UPCASE(%substr(&PAIDDT.,2,11))";

*             title5 "%sysfunc(strip(%qsysfunc(today(),worddate.))), %sysfunc(strip(%qsysfunc(time(),timeampm.)))";
class DrvdDOS;
var          CPTTN_ALLOWED
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
                PAID_TOTAL;

format CPTTN_ALLOWED
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
                PAID_TOTAL dollar10.2;
footnote1 "%sysfunc(strip(%qsysfunc(today(),worddate.))), %sysfunc(strip(%qsysfunc(time(),timeampm.)))";
run;


%put  &T. Completed on %sysfunc(strip(%qsysfunc(today(),worddate.))) at %sysfunc(strip(%qsysfunc(time(),timeampm.))).;


proc contents data=SPEND.SPEND_&spend_date. ; run;