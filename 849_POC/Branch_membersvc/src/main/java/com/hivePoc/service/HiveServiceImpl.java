package com.hivePoc.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.hivePoc.model.HiveBO;
import com.hivePoc.repository.IHiveRepository;

@Service()
public class HiveServiceImpl implements IHiveService {

//	private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
//	
//	
//	public DataSource datasource;
	
	@Autowired
	private IHiveRepository repo;
	List<HiveBO> list=new ArrayList<HiveBO>();
	
	public List<HiveBO> getData(){
		
		

		repo.findAll().forEach(temp_tab -> list.add(temp_tab));
		return list;
	}
}
