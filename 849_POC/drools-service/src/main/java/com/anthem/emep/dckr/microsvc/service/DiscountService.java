package com.anthem.emep.dckr.microsvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anthem.emep.dckr.microsvc.model.Discount;
import com.anthem.emep.dckr.microsvc.repository.IDiscountRepository;

@Service
public class DiscountService {

	@Autowired
	public IDiscountRepository repo;
	
	@Autowired
	public KieContainer kieContainer;
	
	public Discount getDiscount(Integer key){
		
		//Discount discount = repo.findById(key).get();
		Discount discount = repo.findOne(key);
		if(null != discount){
			//KieSession kieSession = kContainer.newKieSession();
			KieSession kieSession = kieContainer.newKieSession("ksession-dtables");
			kieSession.insert(discount);
			kieSession.fireAllRules();
			kieSession.dispose();
			repo.save(discount);
		}
		return discount;
	}
	
	public void insertValues(){
		
		final String[] strings = {"Alabama","Alaska","Arizona","Arkansas","California","Colorado","Connecticut","Delaware","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota","Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"};
		
		List<Discount> list = new ArrayList<Discount>();
		for(int i=1;i<=10000;i++){
			Discount p = new Discount();
			p.setKey(i);
			Random random = new Random();
			p.setState(strings[random.nextInt(strings.length)]);
			p.setDiscount(0);
			list.add(p);
		}
		
	//	KieSession kieSession = kContainer.newKieSession();
		KieSession kieSession = kieContainer.newKieSession("ksession-dtables");
		for(Discount p:list){
			kieSession.insert(p);
		}
		kieSession.fireAllRules();
		kieSession.dispose();
		
		repo.save(list);
		//repo.saveAll(list);
	}
	
}
