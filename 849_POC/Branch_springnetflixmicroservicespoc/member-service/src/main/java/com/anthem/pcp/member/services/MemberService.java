package com.anthem.pcp.member.services;

import com.anthem.pcp.member.model.Member;
import com.anthem.pcp.provider.model.Provider;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MemberService {

    @Autowired
    private ProviderClient providerClient;

    private List<Member> list = new ArrayList<>();

    public MemberService() {
        Member m= new Member();
        m.setContactName("Arun");
        m.setId("member-1");
        m.setContactEmail("arun@anthem.com");
        m.setContactPhone("1112223333");
        m.setName("Arun Prasath");
        list.add(m);

        m= new Member();
        m.setContactName("User2");
        m.setId("member-2");
        m.setContactEmail("user2@anthem.com");
        m.setContactPhone("1112223333");
        m.setName("User 2");
        list.add(m);
    }

    public void save(Member member) {
        member.setId(UUID.randomUUID().toString());
        list.add(member);
    }

    @HystrixCommand(fallbackMethod = "getFallBackProvider")
    public Member getMember(String id) {
        Member member = list.get(0);
        member.setProviderId(providerClient.getProvider(id).getId());

        return member;
    }

    public Member getFallBackProvider(String id) {
        Member member = new Member();
        member.setId("Default-1");
        member.setName("Fallback");
        return member;
    }
}
