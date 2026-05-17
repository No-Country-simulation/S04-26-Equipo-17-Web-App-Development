package com.northpay.backend.operations;

import com.northpay.backend.common.exception.InvalidTokenException;
import com.northpay.backend.operations.dto.LoginRequest;
import com.northpay.backend.operations.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OperatorRepository operatorRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, Long> tokenStore = new ConcurrentHashMap<>();

    public LoginResponse login(LoginRequest request) {
        Operator operator = operatorRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidTokenException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), operator.getPasswordHash())) {
            throw new InvalidTokenException("Credenciales inválidas");
        }

        String token = UUID.randomUUID().toString();
        tokenStore.put(token, operator.getId());

        return new LoginResponse(
                token,
                operator.getId(),
                operator.getFullName(),
                operator.getEmail(),
                operator.getRole()
        );
    }

    public Operator validateToken(String token) {
        Long operatorId = tokenStore.get(token);
        if (operatorId == null) {
            throw new InvalidTokenException("Token inválido o expirado");
        }
        return operatorRepository.findById(operatorId)
                .orElseThrow(() -> new InvalidTokenException("Operador no encontrado"));
    }

    public Operator getCurrentOperator(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Token ausente o mal formado");
        }
        String token = authHeader.substring("Bearer ".length()).trim();
        return validateToken(token);
    }
}