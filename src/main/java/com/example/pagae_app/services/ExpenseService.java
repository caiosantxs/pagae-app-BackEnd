package com.example.pagae_app.services;

import com.example.pagae_app.domain.expense.Expense;
import com.example.pagae_app.domain.expense.ExpenseRequestDTO;
import com.example.pagae_app.domain.expense.ExpenseResponseDTO;
import com.example.pagae_app.domain.expense_shares.ExpenseShare;
import com.example.pagae_app.domain.hangout.HangOut;
import com.example.pagae_app.domain.hangout_member.HangOutMember;
import com.example.pagae_app.domain.payment.Payment;
import com.example.pagae_app.domain.payment.PaymentRequestDTO;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private HangOutRepository hangOutRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HangOutMemberRepository hangOutMemberRepository;

    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    @Transactional
    public ExpenseResponseDTO createExpense(ExpenseRequestDTO data, Long hangOutId, Long currentUserId) {

        HangOut hangOut = hangOutRepository.findById(hangOutId)
                .orElseThrow(() -> new EntityNotFoundException("HangOut not found"));

        if (!hangOutMemberRepository.existsByHangOutIdAndUserId(hangOutId, currentUserId)) {
            throw new SecurityException("User is not in HangOut");
        }

        Expense expense = new Expense(data, hangOut, currentUserId);

        BigDecimal totalPaid = BigDecimal.ZERO;

        List<PaymentRequestDTO> payments = data.payments();

        if (!payments.isEmpty()) {
            for (PaymentRequestDTO paymentDto : payments) {
                User payer = userRepository.findById(currentUserId).orElseThrow(() -> new EntityNotFoundException("User not found"));

                if (!hangOutMemberRepository.existsByHangOutIdAndUserId(hangOutId, currentUserId)) {
                    throw new SecurityException("User is not in HangOut");
                }

                Payment payment = new Payment(paymentDto, expense, payer);
                expense.getPayments().add(payment);
                totalPaid = totalPaid.add(payment.getAmount());
            }
        }

        if (totalPaid.compareTo(expense.getTotalAmount()) != 0) {
            throw new IllegalStateException("The sum of the payments (" + totalPaid + ") don't match with the total value of the hangOut (" + expense.getTotalAmount() + ").");
        }

        Expense savedExpense = expenseRepository.save(expense);

        List<HangOutMember> members = hangOutMemberRepository.findByHangOut_Id(hangOutId);
        if (members.isEmpty()) {
            throw new IllegalStateException("HangOut doesn't have members to divide the hangOut");
        }

        BigDecimal amountPerMember = savedExpense.getTotalAmount()
                .divide(new BigDecimal(members.size()), 2, RoundingMode.HALF_UP);

        for (HangOutMember hangOutMember : members) {
            ExpenseShare share = new ExpenseShare(expense, hangOutMember.getUser(), amountPerMember);
            expenseShareRepository.save(share);
        }

        return new ExpenseResponseDTO(expense);
    }
}
