package com.example.pagae_app.services;

import com.example.pagae_app.domain.user.RegisterDTO;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.domain.user.UserResponseDTO;
import com.example.pagae_app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
