package com.anthem.pcp.member.repository;

import com.anthem.pcp.member.model.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends CrudRepository<Member, String> {
    public Member findById(String id);
}
