package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.hangout.HangOut;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HangOutRepository extends JpaRepository<HangOut, Long> {
}
