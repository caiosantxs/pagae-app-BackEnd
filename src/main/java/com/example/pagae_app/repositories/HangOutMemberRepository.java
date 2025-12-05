package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.hangout_member.HangOutMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HangOutMemberRepository extends JpaRepository<HangOutMember, Long> {

    boolean existsByHangOutIdAndUserId(Long hangOutId, Long currentUserId);

    List<HangOutMember> findByHangOut_Id(Long hangOutId);

    Long countByUser_Id(Long userId);
}
