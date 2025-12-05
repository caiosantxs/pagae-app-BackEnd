package com.example.pagae_app.services;

import com.example.pagae_app.domain.hangout.DashboardStatsDTO;
import com.example.pagae_app.repositories.ExpenseShareRepository;
import com.example.pagae_app.repositories.HangOutMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardService {
    @Autowired
    private HangOutMemberRepository hangOutMemberRepository;
    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    public DashboardStatsDTO getDashboardStats(Long currentUserId) {

        Long totalHangouts = hangOutMemberRepository.countByUser_Id(currentUserId);

        BigDecimal totalOwed = expenseShareRepository.sumTotalOwedByUserId(currentUserId);

        if (totalOwed == null) {
            totalOwed = BigDecimal.ZERO;
        }

        return new DashboardStatsDTO(totalHangouts, totalOwed);
    }
}
