package com.anthem.pcp.member.services;


import com.anthem.pcp.provider.model.Provider;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value="providers"/*, url = "http://localhost:8082"*/)
public interface ProviderClient {

    @RequestMapping(value = "/v1/providers/{memberId}", method = RequestMethod.GET)
    public Provider getProvider(@PathVariable("memberId") String memberId);
}
