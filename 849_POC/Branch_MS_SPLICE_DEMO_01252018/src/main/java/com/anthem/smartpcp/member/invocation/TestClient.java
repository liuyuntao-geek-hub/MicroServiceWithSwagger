package com.anthem.smartpcp.member.invocation;

import org.springframework.web.client.RestTemplate;
import com.anthem.smartpcp.member.model.MBR;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.Timestamp;
import java.math.BigDecimal;

public class TestClient {

	public static void main(String[] args) throws Exception {
		// Test Client for Rest
		
		RestTemplate restTemplate = new RestTemplate();
        //MBR mbr = restTemplate.getForObject("http://localhost:8080/springbootapp/990399388883773", MBR.class);
        MBR mbr = restTemplate.getForObject("http://va10tlvsoa320.wellpoint.com:6432/springbootapp/990399388883773", MBR.class);
        byte[] image = mbr.getDblob();
        Path p = Paths.get("c:\\users\\af66853\\desktop\\pic.jpg");
        Files.write(p, image);

        String txt = mbr.getDclob();
        Path p2 = Paths.get("c:\\users\\af66853\\desktop\\txtdata.txt");
        Files.write(p2, txt.getBytes());

        MBR mbr2 = new MBR();
        mbr2.setKey(553938458123L);
        mbr2.setActive(true);
        mbr2.setCode("MBR02");
        mbr2.setDob(Date.valueOf("2010-01-31"));
        mbr2.setXcode(BigDecimal.valueOf(12.3329));
        mbr2.setYcode(23.2245);
        mbr2.setZcode(BigDecimal.valueOf(86.123));
        mbr2.setLname("Kumar");
        mbr2.setFname("Ankit");
        mbr2.setSubs("SS0012ENBLXX015");
        mbr2.setSequence(10);
        mbr2.setCard(new String(Files.readAllBytes(p2)));
        mbr2.setActivefrm(Timestamp.valueOf("2015-04-14 12:00:01"));
        mbr2.setDblob(Files.readAllBytes(p));
        mbr2.setDclob(new String(Files.readAllBytes(p2)));
        //restTemplate.postForObject("http://localhost:8080/springbootapp/persistMBR", mbr2, MBR.class);
        restTemplate.postForObject("http://va10tlvsoa320.wellpoint.com:6432/springbootapp/persistMBR", mbr2, MBR.class);
	}

}
