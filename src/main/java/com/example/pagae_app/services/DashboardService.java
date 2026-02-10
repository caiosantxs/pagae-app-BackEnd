package com.example.pagae_app.services;

import com.example.pagae_app.domain.dashboard.DashboardStatsDTO;
import com.example.pagae_app.domain.dashboard.ParticipantBadgeDTO;
import com.example.pagae_app.domain.dashboard.RecentHangOutDTO;
import com.example.pagae_app.repositories.ExpenseShareRepository;
import com.example.pagae_app.repositories.HangOutMemberRepository;
import com.example.pagae_app.repositories.HangOutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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

        // Buscas diretas, o banco já retorna 0 se não houver nada
        BigDecimal totalOwed = expenseShareRepository.sumTotalOwedByUserId(currentUserId);
        BigDecimal totalReceivable = expenseShareRepository.sumTotalReceivableByUserId(currentUserId);
        Integer pendingDebtsCount = expenseShareRepository.countPendingDebtsByUserId(currentUserId);

        // Mapeia os 3 últimos rolês
        List<RecentHangOutDTO> recentHangouts = hangoutRepository
                .findTop3ByMembers_User_IdOrderByCreationDateDesc(currentUserId)
                .stream()
                .map(h -> new RecentHangOutDTO(
                        h.getId(),
                        h.getTitle() != null && !h.getTitle().isEmpty() ? h.getTitle().substring(0, 1).toUpperCase() : "R",
                        h.getTitle(),
                        h.getCreationDate() != null ? h.getCreationDate().toString() : "",
                        "ATIVO",
                        BigDecimal.ZERO, 
                        h.getMembers().stream()
                                .map(m -> new ParticipantBadgeDTO(m.getUser().getName().substring(0, 1).toUpperCase()))
                                .toList()
                )).toList();

        return new DashboardStatsDTO(
                totalHangouts,
                totalOwed,
                totalReceivable,
                pendingDebtsCount,
                recentHangouts
        );
    }
}
