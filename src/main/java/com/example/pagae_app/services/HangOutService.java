package com.example.pagae_app.services;

import com.example.pagae_app.domain.hangout.HangOut;
import com.example.pagae_app.domain.hangout.HangOutRequestDTO;
import com.example.pagae_app.domain.hangout.HangOutResponseDTO;
import com.example.pagae_app.domain.hangout_member.HangOutMember;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.repositories.HangOutMemberRepository;
import com.example.pagae_app.repositories.HangOutRepository;
import com.example.pagae_app.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HangOutService {

    @Autowired
    private HangOutRepository hangOutRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HangOutMemberRepository hangOutMemberRepository;

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
    public List<HangOutResponseDTO> findHangOutsByUserId(Long userId) {
        List<HangOut> hangOuts = hangOutRepository.findHangOutsByUserId(userId);

        return hangOuts.stream()
                .map(HangOutResponseDTO::new)
                .collect(Collectors.toList());
    }
}
