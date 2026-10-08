package com.mateusleite.loginseguro.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Telas de autenticação. O POST /login e o POST /logout são tratados
 * pelo próprio Spring Security (configurado no SecurityConfig).
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "pages/login";
    }
}
