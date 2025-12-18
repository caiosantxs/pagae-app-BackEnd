package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.hangout.HangOut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HangOutRepository extends JpaRepository<HangOut, Long> {
    @Query("SELECT hm.hangOut FROM hangout_members hm WHERE hm.user.id = :userId")
    Page<HangOut> findHangOutsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT DISTINCT h 
        FROM hangouts h 
        LEFT JOIN h.members m 
        WHERE h.creator.id = :userId 
           OR m.user.id = :userId
    """)
    Page<HangOut> findByUserInvolvement(@Param("userId") Long userId, Pageable pageable);
}
