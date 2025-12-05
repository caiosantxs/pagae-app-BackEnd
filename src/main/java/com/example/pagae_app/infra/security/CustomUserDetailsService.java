package com.example.pagae_app.infra.security;

import com.example.pagae_app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service; // 1. Importe o @Service


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = this.userRepository.findByLogin(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o login: " + username);
        }

        return user;
    }
}
