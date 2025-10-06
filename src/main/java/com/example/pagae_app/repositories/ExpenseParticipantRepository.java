package com.example.pagae_app.repositories;

import com.example.pagae_app.domain.expense_participants.ExpenseParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, Long> {
}
