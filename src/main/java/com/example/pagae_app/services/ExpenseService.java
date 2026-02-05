package com.example.pagae_app.services;

import com.example.pagae_app.domain.expense.Expense;
import com.example.pagae_app.domain.expense.ExpenseRequestDTO;
import com.example.pagae_app.domain.expense.ExpenseResponseDTO;
import com.example.pagae_app.domain.expense_participants.ExpenseParticipant;
import com.example.pagae_app.domain.expense_shares.ExpenseShare;
import com.example.pagae_app.domain.hangout.HangOut;
import com.example.pagae_app.domain.hangout_member.HangOutMember;
import com.example.pagae_app.domain.payment.Payment;
import com.example.pagae_app.domain.payment.PaymentRequestDTO;
import com.example.pagae_app.domain.payment.PaymentResponseDTO;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.domain.user.UserResponseDTO;
import com.example.pagae_app.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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

    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;

    @Transactional
    public ExpenseResponseDTO createExpense(ExpenseRequestDTO data, Long hangOutId, Long currentUserId) {

        HangOut hangOut = hangOutRepository.findById(hangOutId)
                .orElseThrow(() -> new EntityNotFoundException("HangOut not found"));

        User creator = userRepository.findById(currentUserId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        User payer;
        if (data.payerId() != null) {
            payer = userRepository.findById(data.payerId())
                    .orElseThrow(() -> new EntityNotFoundException("Pagador não encontrado"));
        } else {
            payer = creator; // Se não informou, assume que quem criou pagou
        }

        if (!hangOutMemberRepository.existsByHangOutIdAndUserId(hangOutId, payer.getId())) {
            throw new EntityNotFoundException("O pagador deve ser membro do rolê");
        }

        if (!hangOutMemberRepository.existsByHangOutIdAndUserId(hangOutId, currentUserId)) {
            throw new SecurityException("Acesso negado: O usuário que está criando a despesa não é membro do HangOut.");
        }

        Expense expense = new Expense();
        expense.setDescription(data.description());
        expense.setTotalAmount(data.totalAmount());
        expense.setHangOut(hangOut);
        expense.setCreator(creator);
        expense.setPayer(payer);
        Expense savedExpense = expenseRepository.save(expense);

        List<Long> participantIds = new ArrayList<>();

        if(data.participantsIds() != null) {
            participantIds = data.participantsIds();
        }

        if(!participantIds.isEmpty()){

                List<User> participants = new ArrayList<>();

                for (Long participantId : participantIds) {
                    User participant = userRepository.findById(participantId)
                            .orElseThrow(() -> new EntityNotFoundException("Participante com ID " + participantId + " não encontrado."));

                    if (!hangOutMemberRepository.existsByHangOutIdAndUserId(hangOutId, participant.getId())) {
                        throw new IllegalStateException("Usuário " + participant.getName() + " não pode participar da despesa pois não é membro do HangOut.");
                    }
                    participants.add(participant);
                }

                for (User participant : participants) {
                    ExpenseParticipant expenseParticipant = new ExpenseParticipant(savedExpense, participant);
                    expenseParticipantRepository.save(expenseParticipant);
                }

                BigDecimal amountPerMember = savedExpense.getTotalAmount()
                        .divide(new BigDecimal(participants.size()), 2, RoundingMode.HALF_UP);

                for (User participant : participants) {
                    ExpenseShare share = new ExpenseShare(savedExpense, participant, amountPerMember);
                    if (participant.equals(payer)) {
                        share.setPaid(true);
                    }
                    expenseShareRepository.save(share);
                }
        }else {
            List<HangOutMember> allMembers = hangOutMemberRepository.findByHangOut_Id(hangOutId);

            BigDecimal amountPerMember = savedExpense.getTotalAmount()
                    .divide(new BigDecimal(allMembers.size()), 2, RoundingMode.HALF_UP);

            for (HangOutMember member : allMembers) {
                ExpenseShare share = new ExpenseShare(savedExpense, member.getUser(), amountPerMember);
                expenseShareRepository.save(share);
            }
        }
        
        return new ExpenseResponseDTO(savedExpense);
    }


    @Transactional
    public PaymentResponseDTO addPayment(PaymentRequestDTO data, Long expenseId, User currentUser) {


        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));

        Long hangOutId = expense.getHangOut().getId();


        if(!hangOutMemberRepository.existsByHangOutIdAndUserId(hangOutId, currentUser.getId())){
            throw new SecurityException("Você não é membro deste rolê e não pode realizar pagamentos.");
        }


        ExpenseShare share = expenseShareRepository.findByExpense_IdAndUser_Id(expenseId, currentUser.getId());

        if (share == null) {
            throw new EntityNotFoundException("Você não possui pendências registradas para esta despesa.");
        }

        var novoValor = share.getAmountOwed().subtract(data.amount());
        if(novoValor.compareTo(BigDecimal.ZERO) == 0){
            share.setPaid(true);
        }
        share.setAmountOwed(share.getAmountOwed().subtract(data.amount()));

        expenseShareRepository.save(share);

        Payment payment = new Payment();
        payment.setUser(currentUser);
        payment.setAmount(data.amount());

        expense.addPayment(payment);
        expenseRepository.save(expense);

        return new PaymentResponseDTO(payment.getId(), payment.getAmount(), new UserResponseDTO(currentUser));
    }

    @Transactional
    public void deleteExpense(Long expenseId, Long currentUserId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));

        if(!expense.getHangOut().getCreator().getId().equals(currentUserId)){
            throw new SecurityException("Access denied: Only the creator of the hangOut can delete");
        }

        expenseRepository.delete(expense);
    }

    @Transactional
    public void updateExpense(Long expenseId, ExpenseRequestDTO data, Long currentUserId){
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));

        if(!expense.getHangOut().getCreator().getId().equals(currentUserId)){
            throw new SecurityException("Access denied: Only the creator of the hangOut can delete");
        }

        expense.setDescription(data.description());
        expense.setTotalAmount(data.totalAmount());
        expense.setHangOut(expense.getHangOut());

        expenseRepository.save(expense);
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponseDTO> getExpenses(Long hangOutId, Long currentUserId, Pageable pageable) {
        HangOut hangOut = hangOutRepository.findById(hangOutId)
                .orElseThrow(() -> new EntityNotFoundException("HangOut not found"));

        if (!hangOutMemberRepository.existsByHangOutIdAndUserId(hangOut.getId(), currentUserId)) {
            throw new SecurityException("Acesso negado: Você não é membro deste HangOut.");
        }

        Page<Expense> expensePage = expenseRepository.findByHangOutId(hangOutId, pageable);

        return expensePage.map(ExpenseResponseDTO::new);
    }



}
