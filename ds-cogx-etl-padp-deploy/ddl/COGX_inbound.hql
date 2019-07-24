use pr_ccpcogxph_gbd_r000_in;

DROP VIEW IF EXISTS um_rqst;
CREATE VIEW IF NOT EXISTS um_rqst AS SELECT * FROM pr_cdledwdph_r000_allphi.um_rqst;

DROP VIEW IF EXISTS um_rqst_prov;
CREATE VIEW um_rqst_prov AS SELECT * FROM pr_cdledwdph_r000_allphi.UM_RQST_PROV;

DROP VIEW IF EXISTS um_srvc;
CREATE VIEW um_srvc AS SELECT * FROM pr_cdledwdph_r000_allphi.um_srvc;

DROP VIEW IF EXISTS um_srvc_prov;
CREATE VIEW um_srvc_prov AS SELECT * FROM pr_cdledwdph_r000_allphi.UM_SRVC_PROV;

DROP VIEW IF EXISTS um_srvc_stts;
CREATE VIEW um_srvc_stts AS SELECT * FROM pr_cdledwdph_r000_allphi.UM_SRVC_STTS;