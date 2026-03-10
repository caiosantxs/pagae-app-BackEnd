package com.example.pagae_app.services;

import com.example.pagae_app.domain.user.GoogleTokenDTO;
import com.example.pagae_app.domain.user.User;
import com.example.pagae_app.domain.user.UserRole;
import com.example.pagae_app.repositories.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${google.client.id}")
    private String googleClientId;

    @Autowired
    private UserRepository userRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String loginComGoogle(GoogleTokenDTO googleTokenDTO) {
        try {
            // 1. Configura o verificador oficial do Google
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            // 2. Valida o token recebido do Front-end
            GoogleIdToken idToken = verifier.verify(googleTokenDTO.idToken());

            if (idToken != null) {
                // 3. Token válido! Extraímos os dados do usuário
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                // 4. Verifica se o usuário já existe no nosso banco (se não, cria)
                User user = userRepository.findByEmail(email).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setRole(UserRole.USER);
                    newUser.setLogin(email.replace("@gmail.com",""));
                    newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    return userRepository.save(newUser);
                });

                return "TOKEN_GERADO_AQUI";

            } else {
                throw new RuntimeException("Token do Google inválido!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao validar token do Google: " + e.getMessage());
        }
    }

}
