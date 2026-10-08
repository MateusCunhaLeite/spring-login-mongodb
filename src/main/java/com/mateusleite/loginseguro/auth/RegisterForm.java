package com.mateusleite.loginseguro.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados do formulário de cadastro público.
 * Não existe campo "role": o cadastro público sempre cria ALUNO,
 * então nem adianta alguém tentar enviar uma role na requisição.
 */
public class RegisterForm {

    @NotBlank(message = "Informe seu nome.")
    @Size(max = 100, message = "O nome pode ter no máximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "Informe seu e-mail.")
    @Email(regexp = ".+@.+\\..+", message = "Informe um e-mail válido, como nome@exemplo.com.")
    @Size(max = 254, message = "O e-mail pode ter no máximo 254 caracteres.")
    private String email;

    @NotBlank(message = "Crie uma senha.")
    @SenhaForte
    private String senha;

    @NotBlank(message = "Repita a senha.")
    private String confirmacaoSenha;

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

    public String getConfirmacaoSenha() {
        return confirmacaoSenha;
    }

    public void setConfirmacaoSenha(String confirmacaoSenha) {
        this.confirmacaoSenha = confirmacaoSenha;
    }
}
