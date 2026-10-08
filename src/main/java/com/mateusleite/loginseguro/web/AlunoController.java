package com.mateusleite.loginseguro.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mateusleite.loginseguro.user.UserService;

/**
 * Área do ALUNO (acesso liberado no SecurityConfig só para ROLE_ALUNO).
 */
@Controller
@RequestMapping("/aluno")
public class AlunoController {

    private final UserService userService;

    public AlunoController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String painel(Authentication authentication, Model model) {
        model.addAttribute("usuario", userService.buscarPorEmail(authentication.getName()));
        return "pages/aluno";
    }
}
