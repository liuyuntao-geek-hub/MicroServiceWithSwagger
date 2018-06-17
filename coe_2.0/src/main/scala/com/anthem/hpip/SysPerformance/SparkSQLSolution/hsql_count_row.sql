select from_unixtime(Unix_timestamp());

select count (*) from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_a ;

insert overwrite table dv_pdpspfiph_nogbd_r000_wh.zz_tgttb_a 
select 
TBBO.random_string  MatchingString,
TBBO.id_key b_id_key,
TBAO.id_key a_id_key,
TBBO.random_number b_random_number,
TBAO.random_number a_random_number,
unix_timestamp() 
from
	(
		select * from 
		(
			select * , row_number() over (partition by random_string order by id_key  ) rn
			from 
			(select distinct id_key, random_number, random_string from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_b ) TBBI
		) TBBROW
		where rn = 1
	) TBBO 

,

	(
		select * from 
		(
			select * , row_number() over (partition by random_string order by id_key  ) rn
			from 		
			(select distinct id_key, random_number, random_string from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_a  ) TBAI
		) TBBROW
		where rn = 1
	) TBAO 

Where TBBO.random_string = TBAO.random_String;




select from_unixtime(Unix_timestamp());

select * from dv_pdpspfiph_nogbd_r000_wh.zz_srctb_a limit 100;