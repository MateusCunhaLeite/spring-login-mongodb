package com.mateusleite.loginseguro.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Destino após o login: encaminha cada usuário para a área do seu perfil.
 * Olha só as authorities (ROLE_*), então funciona com qualquer forma de autenticação.
 */
@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        var roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        if (roles.contains("ROLE_ADMIN")) {
            return "redirect:/admin";
        }
        if (roles.contains("ROLE_ORIENTADOR")) {
            return "redirect:/orientador";
        }
        return "redirect:/aluno";
    }
}
