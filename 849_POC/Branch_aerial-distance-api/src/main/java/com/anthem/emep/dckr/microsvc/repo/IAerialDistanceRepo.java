package com.anthem.emep.dckr.microsvc.repo;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.anthem.emep.dckr.microsvc.model.Geocodes;

public interface IAerialDistanceRepo extends JpaRepository<Geocodes, Integer> {
    
    @Query("select ad from Geocodes ad where (st_distance_sphere(st_setsrid(st_makepoint(?1,?2), 4326), st_setsrid(st_makepoint(ad.lon, ad.lat), 4326))*0.000621371)<=60")
    public  ArrayList<Geocodes> findNearbyPoints(double lon,double lat);

}

