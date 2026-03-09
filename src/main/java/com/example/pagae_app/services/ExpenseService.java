package com.example.pagae_app.services;

import com.example.pagae_app.domain.expense.Expense;
import com.example.pagae_app.domain.expense.ExpenseRequestDTO;
import com.example.pagae_app.domain.expense.ExpenseResponseDTO;
import com.example.pagae_app.domain.expense_participants.ExpenseParticipant;
import com.example.pagae_app.domain.expense_shares.Devendo2DTO;
import com.example.pagae_app.domain.expense_shares.DevendoDTO;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            payer = creator;
        }

        if (!hangOutMemberRepository.existsByHangOutIdAndUserId(hangOutId, payer.getId())) {
            throw new EntityNotFoundException("O pagador deve ser membro do rolê");
        }

        if (!hangOutMemberRepository.existsByHangOutIdAndUserId(hangOutId, currentUserId)) {
            throw new SecurityException("Acesso negado: O usuário que está criando a despesa não é membro do HangOut.");
        }

        Expense expense = new Expense(data, hangOut, creator, payer);
        Expense savedExpense = expenseRepository.save(expense);

        this.addParticipantsAndSharesToExpense(data, savedExpense);
        
        return new ExpenseResponseDTO(savedExpense);
    }

    public void addParticipantsAndSharesToExpense(ExpenseRequestDTO data, Expense expense) {
        List<Long> participantIds = new ArrayList<>();

        if(data.participantsIds() != null) {
            participantIds = data.participantsIds();
        }

        if(!participantIds.isEmpty()){

            List<User> participants = new ArrayList<>();

            for (Long participantId : participantIds) {
                User participant = userRepository.findById(participantId)
                        .orElseThrow(() -> new EntityNotFoundException("Participante com ID " + participantId + " não encontrado."));

                if (!hangOutMemberRepository.existsByHangOutIdAndUserId(expense.getHangOut().getId(), participant.getId())) {
                    throw new IllegalStateException("Usuário " + participant.getName() + " não pode participar da despesa pois não é membro do HangOut.");
                }
                participants.add(participant);
            }

            for (User participant : participants) {
                ExpenseParticipant expenseParticipant = new ExpenseParticipant(expense, participant);
                expenseParticipantRepository.save(expenseParticipant);
            }

            BigDecimal amountPerMember = expense.getTotalAmount()
                    .divide(new BigDecimal(participants.size()), 2, RoundingMode.HALF_UP);

            for (User participant : participants) {
                ExpenseShare share = new ExpenseShare(expense, participant, amountPerMember);
                if (participant.equals(expense.getPayer())) {
                    share.setPaid(true);
                }
                expenseShareRepository.save(share);
            }
        }else {
            List<HangOutMember> allMembers = hangOutMemberRepository.findByHangOut_Id(expense.getHangOut().getId());

            BigDecimal amountPerMember = expense.getTotalAmount()
                    .divide(new BigDecimal(allMembers.size()), 2, RoundingMode.HALF_UP);

            for (HangOutMember member : allMembers) {
                ExpenseShare share = new ExpenseShare(expense, member.getUser(), amountPerMember);
                expenseShareRepository.save(share);
            }
        }

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
        payment.setDate(LocalDate.now());

        expense.addPayment(payment);
        expenseRepository.save(expense);

        return new PaymentResponseDTO(payment.getId(), payment.getAmount(), new UserResponseDTO(currentUser), payment.getDate());
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

    public List<Devendo2DTO> calculandoDescontos(Long currentUserId) {
        List<DevendoDTO> listaDeQuemEuDevo = expenseShareRepository.findTotalOwedGroupedByCreditor(currentUserId);
        List<DevendoDTO> listaDeQuemMeDeve = expenseShareRepository.findTotalReceivableGroupedByDebtor(currentUserId);

        List<Devendo2DTO> listaDePossiveisDescontos = new ArrayList<>();

        // Transforma a lista de quem ME DEVE em um Map (Dicionário) usando o ID como chave.
        // Isso evita o duplo 'for' e deixa a busca instantânea.
        Map<Long, DevendoDTO> mapaQuemMeDeve = listaDeQuemMeDeve.stream()
                .collect(Collectors.toMap(DevendoDTO::userId, dto -> dto));

        // Percorre apenas as pessoas para quem EU DEVO
        for (DevendoDTO euDevo : listaDeQuemEuDevo) {

            // Verifica se essa mesma pessoa também ME DEVE (Dívida Cruzada)
            if (mapaQuemMeDeve.containsKey(euDevo.userId())) {
                DevendoDTO eleMeDeve = mapaQuemMeDeve.get(euDevo.userId());

                // Calcula quem sai ganhando no final das contas
                BigDecimal saldoLiquido = eleMeDeve.total().subtract(euDevo.total());

                // Cria o objeto apenas com as pessoas que têm desconto possível
                Devendo2DTO devendoCruzado = new Devendo2DTO(
                        euDevo.userId(),
                        euDevo.name(),
                        euDevo.total(),
                        eleMeDeve.total(),
                        saldoLiquido
                );

                listaDePossiveisDescontos.add(devendoCruzado);
            }
        }

        return listaDePossiveisDescontos;
    }

    @Transactional
    public void realizandoDescontos(Long currentUserId, Long targetUserId) {
        // 1. Busca as fatias de despesa onde EU devo ao Marcos
        List<ExpenseShare> minhasDividas = expenseShareRepository.findDividasEntreUsuarios(currentUserId, targetUserId);

        // 2. Busca as fatias de despesa onde o MARCOS deve a Mim
        List<ExpenseShare> dividasDele = expenseShareRepository.findDividasEntreUsuarios(targetUserId, currentUserId);

        // Se um dos dois lados for zero, não há desconto cruzado para aplicar
        if (minhasDividas.isEmpty() || dividasDele.isEmpty()) {
            return;
        }

        // Soma o total de cada lado
        BigDecimal totalEuDevo = minhasDividas.stream().map(ExpenseShare::getAmountOwed).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalEleDeve = dividasDele.stream().map(ExpenseShare::getAmountOwed).reduce(BigDecimal.ZERO, BigDecimal::add);

        // O valor do desconto é o MENOR valor entre os dois (ex: devo 20, ele deve 40 -> desconto é 20)
        BigDecimal desconto = totalEuDevo.min(totalEleDeve);

        // Aplica o desconto nas MINHAS dívidas para com ele
        abaterDividas(minhasDividas, desconto);

        // Aplica o MESMO desconto nas dívidas DELE para comigo
        abaterDividas(dividasDele, desconto);

        // Salva tudo no banco (como o método tem @Transactional, se der erro em um, cancela tudo)
        expenseShareRepository.saveAll(minhasDividas);
        expenseShareRepository.saveAll(dividasDele);
    }

    // Método auxiliar privado para varrer a lista e ir zerando as fatias
    private void abaterDividas(List<ExpenseShare> parcelas, BigDecimal descontoDisponivel) {
        for (ExpenseShare parcela : parcelas) {
            if (descontoDisponivel.compareTo(BigDecimal.ZERO) <= 0) break;

            if (parcela.getAmountOwed().compareTo(descontoDisponivel) <= 0) {
                // O desconto quita essa parcela inteira
                descontoDisponivel = descontoDisponivel.subtract(parcela.getAmountOwed());
                parcela.setAmountOwed(BigDecimal.ZERO);
                parcela.setPaid(true);
            } else {
                // O desconto abate só um pedaço da parcela
                parcela.setAmountOwed(parcela.getAmountOwed().subtract(descontoDisponivel));
                descontoDisponivel = BigDecimal.ZERO;
            }
        }
    }



}
