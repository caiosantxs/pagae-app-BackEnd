package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.hangout_member.HangOutMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HangOutMemberRepository extends JpaRepository<HangOutMember, Long> {
}
