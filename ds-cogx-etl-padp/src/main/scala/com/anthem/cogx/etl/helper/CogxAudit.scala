package com.anthem.cogx.etl.helper

@SerialVersionUID(2017L)
case class CogxAudit(program: String, user_id: String, app_id: String, start_time: String, app_duration: String, status: String, loaded_row_count: Long )

case class CogxUMRecord( rfrnc_nbr: String, srvc_line_nbr: String, clncl_sor_cd: String, mbrshp_sor_cd: String, um_srvc_stts_cd: String, src_um_srvc_stts_cd: String, src_sbscrbr_id: String, src_mbr_cd: String, prmry_diag_cd: String, rqstd_place_of_srvc_cd: String, src_rqstd_place_of_srvc_cd: String, authrzd_place_of_srvc_cd: String, src_authrzd_place_of_srvc_cd: String, rqstd_srvc_from_dt: String, authrzd_srvc_from_dt: String, rqstd_srvc_to_dt: String, authrzd_srvc_to_dt: String, rqstd_proc_srvc_cd: String, authrzd_proc_srvc_cd: String, rqstd_qty: String, authrzd_qty: String, src_um_prov_id: String, prov_id: String, src_um_prov_id_rp: String, prov_id_rp: String, src_prov_frst_nm_rp: String, src_prov_last_nm_rp: String, src_prov_frst_nm: String, src_prov_last_nm: String)

case class cogxUmInfo(cogxUMdata: CogxUMRecord)

case class cogxUmHistory(cogxUM: Array[CogxUMRecord])

case class cogxRecord( key: String,  cogxUM: Array[CogxUMRecord])

case class CogxClaimRecord(
  ddc_cd_dcn: String, ddc_cd_itm_cde: String, ddc_cd_clm_compl_dte: String, ddc_cd_clm_pay_act_1: String, ddc_cd_clm_pay_act_2_6: String, member_id: String,
  ddc_cd_pat_mbr_cde: String, ddc_cd_grp_nbr: String, ddc_cd_svc_from_dte: String, ddc_cd_svc_thru_dte: String, ddc_cd_prvdr_tax_id: String,
  ddc_cd_prvdr_nme: String, ddc_cd_prvdr_sec_nme: String, ddc_cd_prvdr_spclty_cde: String, prov_lcns_cd: String, ddc_cd_tot_chrg_amt: String,
  ddc_nat_ea2_type_of_bill: String, ddc_cd_med_rec_nbr_2: String, ddc_cd_med_rec_nbr: String, ddc_cd_icda_cde_1: String, ddc_cd_icda_cde_2: String,
  ddc_cd_icda_cde_3: String, ddc_cd_icda_cde_4: String, ddc_cd_icda_cde_5: String, prvdr_status: String, claim_type: String,
  ddc_dtl_lne_nbr: String, ddc_dtl_icda_pntr_1: String, ddc_dtl_prcdr_cde: String, ddc_dtl_svc_cde_1_3: String, ddc_dtl_proc_svc_cls_1: String,
  ddc_dtl_proc_svc_cls_2: String, ddc_dtl_proc_svc_cls_3: String, ddc_dtl_pcodec_hcpcs_cde: String, ddc_dtl_blld_amt: String, ddc_dtl_unts_occur: String,
  ddc_dtl_units_occur: String, ddc_dtl_prcdr_modfr_cde: String, ddc_dtl_pcodec_hcpcs_mod: String, ddc_dtl_mod_cde_1: String, ddc_dtl_mod_cde_2: String,
  ddc_dtl_mod_cde_3: String, ddc_dtl_hcfa_pt_cde: String, ddc_dtl_pt_cde: String, ddc_dtl_elig_expsn_amt: String, srvc_from_dt_dtl: String,
  srvc_to_dt_dtl: String, load_ingstn_id: String)
  
  
case class cogxClaimInfo(cogxClaimdata: CogxClaimRecord)

case class cogxClaimHistory(cogxClaim: Array[CogxClaimRecord])


