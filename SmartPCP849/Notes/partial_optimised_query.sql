select
   A.*
  , case
    when B.MDO_RANKING is null
     then 0
     else B.MDO_RANKING
   end as MDO_RANKING
  from
   (
      select  top 1000
       a.PROV_PCP_ID
      , a.PCP_FRST_NM
      , a.PCP_MID_NM
      , a.HMO_TYPE_CD
      , a.ADRS_LINE_1_TXT
      , a.ADRS_LINE_2_TXT
      , a.ADRS_CITY_NM
      , a.ADRS_ST_CD
      , a.ADRS_ZIP_CD
      , a.ADRS_ZIP_PLUS_4_CD
      , a.ADRS_CNTY_CD
      , a.SPCLTY_MNEMONICS
      , a.PROV_PRTY_CD
      , a.PA_CMNCTN_TYP_VALUE
      , a.PCP_LAST_NM
      , a.LATD_CORDNT_NBR
      , a.LNGTD_CORDNT_NBR
      , a.RGNL_NTWK_ID
      , a.GRPG_RLTD_PADRS_EFCTV_DT
      , a.GRPG_RLTD_PADRS_TRMNTN_DT
      , a.MAX_MBR_CNT
      , a.CURNT_MBR_CNT
      , a.ACC_NEW_PATIENT_FLAG
      , a.SPCLTY_DESC
      , a.TIER_LEVEL
      , a.VBP_FLAG
      , a.ISO_3_CD
      , a.TAX_ID
      , a.AERIAL_DIST
      , a.PANEL_CAPACITY
      FROM
       (

          SELECT
           PROV_PCP_ID
          , PCP_FRST_NM
          , PCP_MID_NM
          , HMO_TYPE_CD
          , ADRS_LINE_1_TXT
          , ADRS_LINE_2_TXT
          , ADRS_CITY_NM
          , ADRS_ST_CD
          , ADRS_ZIP_CD
          , ADRS_ZIP_PLUS_4_CD
          , ADRS_CNTY_CD
          , PA_CMNCTN_TYP_VALUE
          , SPCLTY_MNEMONICS
          , PROV_PRTY_CD
          , PCP_LAST_NM
          , PCP_RANKG_ID
          , LATD_CORDNT_NBR
          , LNGTD_CORDNT_NBR
          , RGNL_NTWK_ID
          , GRPG_RLTD_PADRS_EFCTV_DT
          , ISO_3_CD
          , TAX_ID
          , GRPG_RLTD_PADRS_TRMNTN_DT
          , MAX_MBR_CNT
          , CURNT_MBR_CNT
          , ACC_NEW_PATIENT_FLAG
          , SPCLTY_DESC
          , TIER_LEVEL
          , VBP_FLAG
          , PCP_LANG
          ,(DEGREES(ACOS(SIN(RADIANS((34.064690)))*SIN(RADIANS(LATD_CORDNT_NBR))+COS(RADIANS((34.064690)))*COS(RADIANS(LATD_CORDNT_NBR))*COS(RADIANS((-118.382843)-LNGTD_CORDNT_NBR))))*60*1.1515) AS AERIAL_DIST
          ,(cast(CURNT_MBR_CNT as decimal(10,4))/cast(MAX_MBR_CNT as decimal(10,4)))*100                                                                                                            AS PANEL_CAPACITY
          , ROW_NUMBER() over(
                             partition by PROV_PCP_ID
                             order by
                              (cast(CURNT_MBR_CNT as decimal(10,4))/cast(MAX_MBR_CNT as decimal(10,4))) asc) as rnum
          FROM
           DV_PDPSPCP_XM.PROVIDER_INFO1
          where
           RGNL_NTWK_ID in ('CC09','CC32')
           and LATD_CORDNT_NBR     > (33.064690)
           and LATD_CORDNT_NBR     < (35.064690)
           and LNGTD_CORDNT_NBR    > (-119.382843)
           and LNGTD_CORDNT_NBR    < (-117.382843)
           AND ACC_NEW_PATIENT_FLAG='Y'
           AND
           (
            '2014-01-01'
           )
           >=GRPG_RLTD_PADRS_EFCTV_DT
           AND
           (
           '2014-01-01'
           )
                                                                          <GRPG_RLTD_PADRS_TRMNTN_DT
           AND DATEDIFF(DAY, ('2014-01-01'),GRPG_RLTD_PADRS_TRMNTN_DT ) >(90)
           AND
           (
            REPLACE(SPCLTY_DESC,'Pediatric','')    like '%Internal Medicine%'
            OR REPLACE(SPCLTY_DESC,'Pediatric','') like '%Family Practice%'
            OR REPLACE(SPCLTY_DESC,'Pediatric','') like '%Pediatric%'
            OR REPLACE(SPCLTY_DESC,'Pediatric','') like '%General Practice%'
           )
       )
       a
	   WHERE a.rnum=1 and a.AERIAL_DIST<(60) and a.PANEL_CAPACITY <(85) order by a.AERIAL_DIST asc
     )

   A
   left join
    (
     select
      TAX_ID
     , MDO_RANKING
     from
      DV_PDPSPCP_XM.HCA_PROV_RANKING
     where
      ADRS_ST_CD ='VA'
    )
    B
    on
     A.TAX_ID = B.TAX_ID
