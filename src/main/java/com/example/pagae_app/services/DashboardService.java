package com.example.pagae_app.services;

import com.example.pagae_app.domain.dashboard.DashboardStatsDTO;
import com.example.pagae_app.domain.dashboard.ParticipantBadgeDTO;
import com.example.pagae_app.domain.dashboard.RecentHangOutDTO;
import com.example.pagae_app.domain.expense.Expense;
import com.example.pagae_app.domain.hangout.HangOut;
import com.example.pagae_app.repositories.ExpenseShareRepository;
import com.example.pagae_app.repositories.HangOutMemberRepository;
import com.example.pagae_app.repositories.HangOutRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DashboardService {

    @Autowired
    private HangOutMemberRepository hangOutMemberRepository;
    @Autowired
    private ExpenseShareRepository expenseShareRepository;
    @Autowired
    private HangOutRepository hangoutRepository;

    public DashboardStatsDTO getDashboardStats(Long currentUserId) {

        Long totalHangouts = hangOutMemberRepository.countByUser_Id(currentUserId);

        BigDecimal totalOwed = expenseShareRepository.sumTotalOwedByUserId(currentUserId);
        BigDecimal totalReceivable = expenseShareRepository.sumTotalReceivableByUserId(currentUserId);
        Integer pendingDebtsCount = expenseShareRepository.countPendingDebtsByUserId(currentUserId);

        List<HangOut> top3Hangouts = hangoutRepository.findTop3ByMembers_User_IdOrderByCreationDateDesc(currentUserId);
        List<Long> top3Ids = top3Hangouts.stream().map(HangOut::getId).toList();
        List<Long> debtIds = top3Ids.isEmpty() ? Collections.emptyList() :
                expenseShareRepository.findPendingDebtHangoutIds(currentUserId, top3Ids);

        Set<Long> debtSet = new HashSet<>(debtIds);

        List<RecentHangOutDTO> recentHangouts = hangoutRepository
                .findTop3ByMembers_User_IdOrderByCreationDateDesc(currentUserId)
                .stream()
                .map(h -> {
                    BigDecimal totalGasto = h.getExpenses().stream()
                            .map(Expense::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);


                    boolean temPendencia = debtSet.contains(h.getId());

                    return new RecentHangOutDTO(
                            h.getId(),
                            h.getTitle().substring(0, 1).toUpperCase(),
                            h.getTitle(),
                            h.getCreationDate().toString(),
                            "ATIVO",
                            totalGasto,
                            temPendencia,
                            h.getMembers().stream()
                                    .map(m -> new ParticipantBadgeDTO(m.getUser().getName().substring(0, 1).toUpperCase()))
                                    .toList()
                    );
                }).toList();

        return new DashboardStatsDTO(totalHangouts, totalOwed, totalReceivable, pendingDebtsCount, recentHangouts);
    }

}
