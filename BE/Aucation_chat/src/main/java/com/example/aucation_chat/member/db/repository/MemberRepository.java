package com.example.aucation_chat.member.db.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aucation_chat.member.db.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
	Optional<Member> findByMemberPk(long memberPk);
}
