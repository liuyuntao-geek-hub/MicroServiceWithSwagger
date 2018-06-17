
select from_unixtime(Unix_timestamp());


insert into table dv_pdpspfiph_nogbd_r000_wh.zz_srctb_a
select * from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_a;

insert into table dv_pdpspfiph_nogbd_r000_wh.zz_srctb_b
select * from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_b;


insert into table dv_pdpspfiph_nogbd_r000_wh.zz_srctb_a
select * from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_b;

insert into table dv_pdpspfiph_nogbd_r000_wh.zz_srctb_b
select * from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_a;

select from_unixtime(Unix_timestamp());


select count (*) from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_b ;

select count (*) from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_a ;
