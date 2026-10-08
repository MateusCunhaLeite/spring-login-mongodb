package com.mateusleite.loginseguro.auth;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.mateusleite.loginseguro.user.UserService;

import jakarta.validation.Valid;

/**
 * Telas de login e cadastro. O POST /login e o POST /logout são tratados
 * pelo próprio Spring Security (configurado no SecurityConfig).
 */
@Controller
public class AuthController {

    private final UserService userService;
    private final RegisterValidator registerValidator;

    public AuthController(UserService userService, RegisterValidator registerValidator) {
        this.userService = userService;
        this.registerValidator = registerValidator;
    }

    @ModelAttribute("dominioPermitido")
    public String dominioPermitido() {
        return registerValidator.dominioPermitido();
    }

    @GetMapping("/login")
    public String login() {
        return "pages/login";
    }

    @GetMapping("/cadastro")
    public String cadastro(@ModelAttribute("form") RegisterForm form) {
        return "pages/cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute("form") RegisterForm form, BindingResult result) {
        registerValidator.validar(form, result);
        if (result.hasErrors()) {
            return "pages/cadastro";
        }
        try {
            userService.cadastrarAluno(form.getNome(), form.getEmail(), form.getSenha());
        } catch (DuplicateKeyException e) {
            // Dois cadastros simultâneos com o mesmo e-mail: o índice único do banco barra o segundo
            result.rejectValue("email", "email.duplicado", "Este e-mail já está cadastrado. Que tal fazer login?");
            return "pages/cadastro";
        }
        return "redirect:/login?cadastrado";
    }
}
