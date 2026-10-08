package com.mateusleite.loginseguro.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mateusleite.loginseguro.user.UserService;

/**
 * Área do ORIENTADOR: lista (somente leitura) dos alunos cadastrados.
 */
@Controller
@RequestMapping("/orientador")
public class OrientadorController {

    private final UserService userService;

    public OrientadorController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String painel(Model model) {
        model.addAttribute("alunos", userService.listarAlunos());
        return "pages/orientador";
    }
}
