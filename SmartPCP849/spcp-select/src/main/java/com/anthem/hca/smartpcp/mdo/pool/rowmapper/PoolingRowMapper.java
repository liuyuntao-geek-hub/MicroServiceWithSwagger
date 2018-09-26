/**
* Copyright © 2018 Anthem, Inc.
* 
* @author AF74173
*/

package com.anthem.hca.smartpcp.mdo.pool.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.anthem.hca.smartpcp.model.ScoringProvider;

public class PoolingRowMapper implements ResultSetExtractor<List<ScoringProvider>> {
	
	
	int poolsize ;
	
	public PoolingRowMapper(){
		
	}
	
	public PoolingRowMapper(int poolSize){
		this.poolsize = poolSize;
	}

	@Override
	public List<ScoringProvider> extractData(ResultSet resultSet) throws SQLException {
		List<ScoringProvider> providerList = new ArrayList<>(this.poolsize);
		
		while (resultSet.next()) {
			ScoringProvider scoringProvider = new ScoringProvider();
            scoringProvider.provPcpId = resultSet.getString(1);
            scoringProvider.lastName = resultSet.getString(2);
            scoringProvider.rgnlNtwkId = resultSet.getString(3);
            scoringProvider.grpgRltdPadrsEfctvDt = resultSet.getDate(4);
            scoringProvider.pcpLang = Arrays.asList(resultSet.getString(5).split(","));
            scoringProvider.vbpFlag = resultSet.getString(6);
            scoringProvider.distance = resultSet.getDouble(7);
            scoringProvider.panelCapacity = resultSet.getDouble(8);
            scoringProvider.rank = resultSet.getInt(9);
            scoringProvider.speciality = Arrays.asList(resultSet.getString(10).split(","));
            
            providerList.add(scoringProvider);

			
		}
		
		return providerList;
	}

}
