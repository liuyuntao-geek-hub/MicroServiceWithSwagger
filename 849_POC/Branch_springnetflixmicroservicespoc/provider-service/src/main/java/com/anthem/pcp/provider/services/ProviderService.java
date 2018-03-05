package com.anthem.pcp.provider.services;

import com.anthem.pcp.provider.model.Provider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProviderService {

    private List<Provider> list = new ArrayList<>();

    public ProviderService() {
        Provider m= new Provider();
        m.setContactName("Arun");
        m.setId("provider-1");
        m.setContactEmail("arun@anthem.com");
        m.setContactPhone("1112223333");
        m.setName("Arun Prasath");

        list.add(m);
    }

    public void save(Provider org) {
        org.setId(UUID.randomUUID().toString());
        list.add(org);
    }

    public Provider getProvider() {
        try {
            Thread.sleep(500);
        } catch(Exception  e) {
        }

        Provider provider = list.get(0);
        provider.setId("provider-"+System.currentTimeMillis());
        return provider;
    }
}
