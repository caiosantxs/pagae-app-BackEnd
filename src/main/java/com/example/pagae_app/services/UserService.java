package com.example.pagae_app.services;

import com.example.pagae_app.domain.user.*;
import com.example.pagae_app.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserResponseDTO create(RegisterDTO data) {
        String encryptedPassword = passwordEncoder.encode(data.password());

        User user = new User(data, encryptedPassword);

        this.userRepository.save(user);

        return new UserResponseDTO(user);
    }

    public User findById(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public void delete(Long id, Long currentUserId) throws SecurityException{

        User currentUser = this.findById(currentUserId);
        System.out.println(currentUser.getRole());

        if (!currentUser.getRole().equals(UserRole.ADMIN)) {
            throw new SecurityException("You do not have permission to delete this user");
        }

        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        this.userRepository.delete(user);
    }

    @Transactional
    public void updatePassword(UserUpdatePasswordDTO data){
        User user = this.userRepository.findById(data.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(data.newPassword());
        user.setPassword(encryptedPassword);

        this.userRepository.save(user);
    }


}
