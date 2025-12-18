package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.hangout_member.HangOutMember;
import com.example.pagae_app.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HangOutMemberRepository extends JpaRepository<HangOutMember, Long> {

    boolean existsByHangOutIdAndUserId(Long hangOutId, Long currentUserId);

    List<HangOutMember> findByHangOut_Id(Long hangOutId);

    Long countByUser_Id(Long userId);

    @Query("""
        SELECT hm.user 
        FROM hangout_members hm 
        WHERE hm.hangOut.id = :hangOutId
    """)
    List<User> findUsersByHangOutId(@Param("hangOutId") Long hangOutId);
}
