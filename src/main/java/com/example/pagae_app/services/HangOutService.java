package com.example.pagae_app.services;

import com.example.pagae_app.domain.hangout.HangOut;
import com.example.pagae_app.domain.hangout.HangOutRequestDTO;
import com.example.pagae_app.domain.hangout.HangOutResponseDTO;
import com.example.pagae_app.domain.hangout.StatusHangOut;
import com.example.pagae_app.domain.hangout_member.HangOutMember;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.domain.user.UserResponseDTO;
import com.example.pagae_app.repositories.ExpenseShareRepository;
import com.example.pagae_app.repositories.HangOutMemberRepository;
import com.example.pagae_app.repositories.HangOutRepository;
import com.example.pagae_app.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class HangOutService {

    @Autowired
    private HangOutRepository hangOutRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HangOutMemberRepository hangOutMemberRepository;

    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    @Transactional
    public HangOutResponseDTO create(HangOutRequestDTO data, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        HangOut hangOut = new HangOut(data, creator);
        HangOut saved = hangOutRepository.save(hangOut);

        HangOutMember firstMember = new HangOutMember(saved, creator);
        hangOutMemberRepository.save(firstMember);

        if (data.memberIds() != null && !data.memberIds().isEmpty()) {
            List<User> members = userRepository.findAllById(data.memberIds());

            for (User user : members) {

                if (!user.getId().equals(creator.getId())) {
                    HangOutMember member = new HangOutMember(saved, user);
                    hangOutMemberRepository.save(member);
                }
            }
        }

        return new HangOutResponseDTO(saved);
    }

    @Transactional
    public void delete(Long hangOutId, Long currentUserId) {
        HangOut hangOut = hangOutRepository.findById(hangOutId)
                .orElseThrow(() -> new EntityNotFoundException("HangOut not found"));

        if (!hangOut.getCreator().getId().equals(currentUserId)) {
            throw new SecurityException("Access denied: Only the creator of the hangOut can delete");
        }

        hangOutRepository.deleteById(hangOutId);
    }

    @Transactional
    public void update(HangOutRequestDTO data, Long hangOutId, Long currentUserId) {
        HangOut hangOut = hangOutRepository.findById(hangOutId)
                .orElseThrow(() -> new EntityNotFoundException("HangOut not found"));

        if(!hangOut.getCreator().getId().equals(currentUserId)) {
            throw new SecurityException("Access denied: Only the creator of the hangOut can update");
        }

        hangOut.setTitle(data.title());

        hangOutRepository.save(hangOut);
    }

    @Transactional
    public void addMemberToHangOut(Long hangOutId, Long userId, Long currentUserId) {
        HangOut hangOut = hangOutRepository.findById(hangOutId)
                .orElseThrow(() -> new RuntimeException("HangOut not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isCurrentUserMember = hangOutMemberRepository.existsByHangOutIdAndUserId(hangOutId, currentUserId);
        if (!isCurrentUserMember) {
            throw new SecurityException("Access denied: Only member of the hangOut can add member");
        }

        boolean alreadyExists = hangOutMemberRepository.existsByHangOutIdAndUserId(hangOutId, userId);
        if (alreadyExists) {
            throw new RuntimeException("Member already exists");
        }

        HangOutMember hangOutMember = new HangOutMember(hangOut, user);

        hangOutMemberRepository.save(hangOutMember);
    }

    @Transactional(readOnly = true)
    public Page<HangOutResponseDTO> findHangOutsByUserId(Long userId, Pageable pageable) {
        // 1. Busca a página do banco (como você já fazia)
        Page<HangOut> hangOutsPage = hangOutRepository.findByUserInvolvement(userId, pageable);

        // Se a página estiver vazia, retorna logo pra economizar processamento
        if (hangOutsPage.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2. Extrai apenas os IDs dos Hangouts dessa página
        List<Long> hangoutIds = hangOutsPage.getContent().stream()
                .map(HangOut::getId)
                .toList();

        // 3. Vai no banco verificar quais desses IDs possuem dívida pendente para este usuário
        // (Isso executa apenas 1 query rápida, ao invés de 1 query por hangout)
        List<Long> idsComDivida = expenseShareRepository.findPendingDebtHangoutIds(userId, hangoutIds);

        // Transforma em Set para a busca ficar instantânea
        Set<Long> setDeDividas = new HashSet<>(idsComDivida);

        // 4. Mapeia usando o construtor novo (HangOut, boolean)
        return hangOutsPage.map(hangout -> new HangOutResponseDTO(
                hangout,
                setDeDividas.contains(hangout.getId()) // Passa TRUE se o ID estiver no Set
        ));
    }


    public List<UserResponseDTO> getHangoutParticipants(Long hangOutId) {
        List<User> participants = hangOutMemberRepository.findUsersByHangOutId(hangOutId);

        return participants.stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    public HangOutResponseDTO getHangOutById(Long hangOutId) {
        HangOut hangOut = hangOutRepository.findHangOutsById(hangOutId);
        return new HangOutResponseDTO(hangOut);
    }

    @Transactional
    public void finalize(Long id, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        HangOut hangout = hangOutRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hangout não encontrado"));

        if(!hangout.getCreator().getId().equals(currentUserId)) {
           throw new EntityNotFoundException("Acesso proibido: Apenas o criador do HangOut pode finalizá-lo");
        }

        hangout.setStatus(StatusHangOut.FINALIZADO);
        hangOutRepository.save(hangout);
    }

    public void open(Long id, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        HangOut hangout = hangOutRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hangout não encontrado"));

        if(!hangout.getCreator().getId().equals(currentUserId)) {
            throw new EntityNotFoundException("Acesso proibido: Apenas o criador do HangOut pode reabri-lo");
        }

        hangout.setStatus(StatusHangOut.ATIVO);
        hangOutRepository.save(hangout);
    }

    @Transactional
    public void joinHangout(Long hangoutId, Long currentUserId) {
        HangOut hangout = hangOutRepository.findById(hangoutId)
                .orElseThrow(() -> new EntityNotFoundException("Rolê não encontrado"));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        HangOutMember newMember = new HangOutMember(hangout, user);

        boolean isAlreadyMember = hangout.getMembers().stream()
                .anyMatch(p -> p.getUser().getId().equals(user.getId()));

        if (!isAlreadyMember) {
            hangout.getMembers().add(newMember);
            hangOutRepository.save(hangout);
        }
    }
}
