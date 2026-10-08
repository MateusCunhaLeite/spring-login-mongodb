package com.mateusleite.loginseguro.web;

import com.mateusleite.loginseguro.auth.SenhaForte;
import com.mateusleite.loginseguro.user.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Formulário do painel admin para criar ORIENTADOR ou ADMIN.
 * O admin define uma senha inicial e a repassa à pessoa.
 */
public class NovoUsuarioForm {

    @NotBlank(message = "Informe o nome.")
    @Size(max = 100, message = "O nome pode ter no máximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "Informe o e-mail.")
    @Email(regexp = ".+@.+\\..+", message = "Informe um e-mail válido, como nome@exemplo.com.")
    @Size(max = 254, message = "O e-mail pode ter no máximo 254 caracteres.")
    private String email;

    @NotBlank(message = "Defina uma senha inicial.")
    @SenhaForte
    private String senha;

    @NotNull(message = "Escolha o perfil.")
    private Role role;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
