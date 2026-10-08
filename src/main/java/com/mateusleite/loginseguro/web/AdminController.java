package com.mateusleite.loginseguro.web;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mateusleite.loginseguro.user.Role;
import com.mateusleite.loginseguro.user.Status;
import com.mateusleite.loginseguro.user.UserService;

import jakarta.validation.Valid;

/**
 * Painel do ADMIN: lista de usuários, criação de ORIENTADOR/ADMIN e ativação/desativação de contas.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    /** Perfis que o admin pode criar. ALUNO se cadastra sozinho, pelo formulário público. */
    private static final List<Role> PERFIS_CRIAVEIS = List.of(Role.ORIENTADOR, Role.ADMIN);

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String painel(@ModelAttribute("form") NovoUsuarioForm form, Model model) {
        return paginaAdmin(model);
    }

    @PostMapping("/usuarios")
    public String criarUsuario(@Valid @ModelAttribute("form") NovoUsuarioForm form, BindingResult result,
                               Model model, RedirectAttributes redirect) {
        if (form.getRole() != null && !PERFIS_CRIAVEIS.contains(form.getRole())) {
            result.rejectValue("role", "role.invalida", "Escolha Orientador ou Administrador.");
        }
        if (!result.hasFieldErrors("email") && userService.emailJaCadastrado(form.getEmail())) {
            result.rejectValue("email", "email.duplicado", "Este e-mail já está cadastrado.");
        }
        if (result.hasErrors()) {
            return paginaAdmin(model);
        }
        try {
            userService.criar(form.getNome(), form.getEmail(), form.getSenha(), form.getRole());
        } catch (DuplicateKeyException e) {
            result.rejectValue("email", "email.duplicado", "Este e-mail já está cadastrado.");
            return paginaAdmin(model);
        }
        redirect.addFlashAttribute("sucesso",
                form.getNome().trim() + " foi criado(a) com o perfil " + form.getRole().getRotulo() + ".");
        return "redirect:/admin";
    }

    @PostMapping("/usuarios/{id}/ativar")
    public String ativar(@PathVariable String id, Authentication authentication, RedirectAttributes redirect) {
        return alterarStatus(id, Status.ATIVO, authentication, redirect);
    }

    @PostMapping("/usuarios/{id}/desativar")
    public String desativar(@PathVariable String id, Authentication authentication, RedirectAttributes redirect) {
        return alterarStatus(id, Status.INATIVO, authentication, redirect);
    }

    private String alterarStatus(String id, Status status, Authentication authentication, RedirectAttributes redirect) {
        try {
            var user = userService.alterarStatus(id, status, authentication.getName());
            String acao = status == Status.ATIVO ? "ativada" : "desativada";
            redirect.addFlashAttribute("sucesso", "Conta de " + user.getNome() + " " + acao + ".");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/admin";
    }

    private String paginaAdmin(Model model) {
        model.addAttribute("usuarios", userService.listarTodos());
        model.addAttribute("perfis", PERFIS_CRIAVEIS);
        return "pages/admin";
    }
}
