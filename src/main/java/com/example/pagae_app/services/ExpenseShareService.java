package com.example.pagae_app.services;

import com.example.pagae_app.domain.expense_shares.ExpenseShare;
import com.example.pagae_app.domain.expense_shares.ExpenseShareDTO;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.repositories.ExpenseShareRepository;
import com.example.pagae_app.repositories.HangOutMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseShareService {

    @Autowired
    private HangOutMemberRepository hangOutMemberRepository;
    @Autowired
    private ExpenseShareRepository expenseShareRepository;


    @Transactional(readOnly = true)
    public Page<ExpenseShareDTO> getAllExpenseSharesByUser(Long currentUserId, Pageable pageable) {
        Page<ExpenseShare> expensesShare = expenseShareRepository.findExpenseShareByUserId(currentUserId, pageable);

        return expensesShare.map(ExpenseShareDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<ExpenseShareDTO> getExpensesSharesByHangOut(Long hangOutId, Long userId, Pageable pageable) {

        if(!hangOutMemberRepository.existsByHangOutIdAndUserId(hangOutId, userId)) {
            throw new SecurityException("Acess denied: User is not a member of hang out");
        }

        Page<ExpenseShare> shares = expenseShareRepository.findByExpense_HangOut_Id(hangOutId, pageable);
        return shares.map(ExpenseShareDTO::new);
    }

    @Transactional
    public void markShareAsPaid(Long expenseShareId, Long userId) {
        ExpenseShare expenseShare = expenseShareRepository.findById(expenseShareId)
                .orElseThrow(() -> new EntityNotFoundException("Expense share not found"));

        User debtor = expenseShare.getUser();
        User hangOutCreator = expenseShare.getExpense().getHangOut().getCreator();

        if(!debtor.getId().equals(userId) && !hangOutCreator.getId().equals(userId)) {
            throw new SecurityException("Acess denied: Only debtors or hang out creators can mark share as paid");
        }

        expenseShare.setPaid(true);
        expenseShareRepository.save(expenseShare);
    }



}
