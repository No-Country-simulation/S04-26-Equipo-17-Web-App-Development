package com.northpay.backend;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordTest {

    @Test
    void generarYVerificarHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String contraseniaPlana = "NorthPay123";

        // 1. Generamos un hash fresco desde tu propia máquina
        String nuevoHashGenerado = encoder.encode(contraseniaPlana);

        System.out.println("\n=======================================================");
        System.out.println("COPIA ESTE HASH Y PONLO EN SUPABASE:");
        System.out.println(nuevoHashGenerado);
        System.out.println("=======================================================\n");

        // 2. Lo verificamos inmediatamente para asegurar que da TRUE
        boolean coinciden = encoder.matches(contraseniaPlana, nuevoHashGenerado);
        System.out.println("¿Verificación interna exitosa?: " + coinciden);

        assertTrue(coinciden);
    }
}
