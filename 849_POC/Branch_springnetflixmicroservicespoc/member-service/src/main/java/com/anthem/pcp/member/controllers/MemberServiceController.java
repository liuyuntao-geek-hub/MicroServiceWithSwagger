package com.anthem.pcp.member.controllers;


import com.anthem.pcp.member.model.Member;
import com.anthem.pcp.member.services.MemberService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "v1/members")
@Api(value = "member-service", description = "Member Service APIs")
public class MemberServiceController {

    @Autowired
    private MemberService memberService;

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceController.class);

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public String getMember() {
        return "Service is up";
    }

    @RequestMapping(value = "/{memberId}", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    public Member getMember(@PathVariable("memberId") String memberId) {
        logger.debug(String.format("Looking up data for member {}", memberId));
        Member member = memberService.getMember(memberId);
        return member;
    }

    @RequestMapping(value = "/{memberId}", method = RequestMethod.PUT)
    public void updateMember(@PathVariable("memberId") String memberId, @RequestBody Member member) {
        logger.debug("Member updated: {}, {}", member.getContactName(), member.getName());
    }

    @RequestMapping(value = "/{memberId}", method = RequestMethod.POST)
    public void saveMember(@RequestBody Member member) {
    }

    @RequestMapping(value = "/{memberId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable("memberId") String memberId, @RequestBody Member member) {
    }
}
