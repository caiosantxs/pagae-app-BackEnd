package com.example.pagae_app.services;

import com.example.pagae_app.domain.user.*;
import com.example.pagae_app.infra.exceptions.InvalidTokenException;
import com.example.pagae_app.repositories.TokenRepository;
import com.example.pagae_app.repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenRepository tokenRepository;


    private static final Logger log = LoggerFactory.getLogger(UserService.class.getName());

    @Transactional
    public UserResponseDTO create(RegisterDTO data) {
        String encryptedPassword = passwordEncoder.encode(data.password());

        if (this.userRepository.findByLogin(data.login()) != null) {
            throw new EntityExistsException("Login already exists, try another login");
        }

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

    public void resetPasswordRequest (String userEmail){
        User user = this.userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();

        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10);

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        tokenRepository.save(resetToken);

        String resetUrl = "http://localhost:4200/reset-password?token=" + token;

        String subject = "Recuperação de Senha - Pagaê App";
        String text = "Olá " + user.getName() + ",\n\n"
                + "Você solicitou a redefinição de sua senha.\n"
                + "Clique no link abaixo para criar uma nova senha:\n"
                + token + "\n\n"
                + "Se você não solicitou isso, por favor ignore este email.\n\n"
                + "O link expira em 1 hora.";

        emailService.sendEmail(user.getEmail(), subject, text);

    }

    @Transactional
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token not found"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new InvalidTokenException("Token expired");
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);

        emailService.sendEmail(user.getEmail(), "Sua senha foi redefinida",
                "Olá " + user.getName() + ",\n\nSua senha foi redefinida com sucesso.");
    }


    @Transactional
    public void updatePassword(UserUpdatePasswordDTO data, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(data.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Senha atual incorreta.");
        }

        if (passwordEncoder.matches(data.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("A nova senha não pode ser igual à senha atual.");
        }

        if (!data.newPassword().equals(data.confirmPassword())) {
                throw new IllegalArgumentException("As novas senhas não coincidem.");
        }

        user.setPassword(passwordEncoder.encode(data.newPassword()));
        userRepository.save(user);

        try {
            emailService.sendEmail(user.getEmail(),
                    "Sua senha foi alterada",
                    "Olá " + user.getName() + ",\n\nSua senha na plataforma Pagaê App foi alterada recentemente.\n\n"
                            + "Se você não realizou esta alteração, entre em contato conosco imediatamente ou use a opção 'Esqueci minha senha'.");
            log.info("Email de notificação de alteração de senha enviado para {}", user.getEmail());
        } catch (Exception e) {
            log.error("Erro ao enviar email de notificação de alteração de senha para {}: {}", user.getEmail(), e.getMessage());
        }
    }


    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        
        return users.stream().map(UserResponseDTO::new).collect(Collectors.toList());
    }

    public List<UserResponseDTO> searchUsers(String search) {
        Pageable limit = PageRequest.of(0, 10);

        List<User> users = userRepository.searchByNameOrLogin(search, limit);

        return users.stream()
                .map(UserResponseDTO::new)
                .toList();
    }

}
