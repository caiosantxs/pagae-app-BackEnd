package com.example.pagae_app.services;

import com.example.pagae_app.domain.hangout.HangOut;
import com.example.pagae_app.domain.hangout.HangOutRequestDTO;
import com.example.pagae_app.domain.hangout.HangOutResponseDTO;
import com.example.pagae_app.domain.hangout_member.HangOutMember;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.domain.user.UserResponseDTO;
import com.example.pagae_app.repositories.HangOutMemberRepository;
import com.example.pagae_app.repositories.HangOutRepository;
import com.example.pagae_app.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public HangOutResponseDTO create(HangOutRequestDTO data, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        HangOut hangOut = new HangOut(data, creator);
        HangOut saved = hangOutRepository.save(hangOut);

        HangOutMember firstMember = new HangOutMember(saved, creator);
        hangOutMemberRepository.save(firstMember);

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

        hangOut.setDescription(data.description());
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
        Page<HangOut> hangOuts = hangOutRepository.findByUserInvolvement(userId, pageable);

        return hangOuts.map(HangOutResponseDTO::new);
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
}
