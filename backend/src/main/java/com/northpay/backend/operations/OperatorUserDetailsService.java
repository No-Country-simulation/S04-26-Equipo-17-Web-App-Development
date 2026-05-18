package com.northpay.backend.operations;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperatorUserDetailsService implements UserDetailsService {

    private final OperatorRepository operatorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Operator operator = operatorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Operador no encontrado: " + email));

        return User.builder()
                .username(operator.getEmail())
                .password(operator.getPasswordHash())
                .roles(operator.getRole())
                .build();
    }
}