USE ${hivedb};

------SPEND------ 
  
  insert into hpip_variance
  select "SPEND_MBR_ALLOW_S","rcrd_date_range","201601_201612_201703","mcid","Total_Members","count(mcid)",1,0,5,"false",current_timestamp();
  
  insert into hpip_variance 
  select "SPEND_MBR_ALLOW_S","rcrd_date_range","201601_201612_201703","ALLOWED_TOTAL","TOTAL_ALLOWED_AMOUNT","sum(ALLOWED_TOTAL)",1,0,5,"false",current_timestamp();
  
  insert into hpip_variance 
  select "SPEND_MBR_ALLOW_S","rcrd_date_range","201601_201612_201703","PAID_TOTAL","TOTAL_PAID_AMOUNT","sum(PAID_TOTAL)",1,0,5,"false",current_timestamp();
  
  
  ---------TARGETED_TIN----------
  
  insert into hpip_variance
  select "targeted_tins","rcrd_date_range","201713","Market_Date","COUNT_TINS_Market_Date","sum(Market_Date)",50,0,15,"false",current_timestamp();
   
   
   insert into hpip_variance
  select "targeted_tins","rcrd_date_range","201713","TINS_Market","COUNT_TINS_Market","sum(TINS_Market)",40,0,15,"false",current_timestamp();
   
    insert into hpip_variance
  select "targeted_tins","rcrd_date_range","201713","TINS_Date","COUNT_TINS_Date","sum(TINS_Date)",30,0,15,"false",current_timestamp();
    
   insert into hpip_variance
  select "targeted_tins","rcrd_date_range","201713","TINS","COUNT_TINS","count(TINS)",250,0,15,"false",current_timestamp();
