package com.mateusleite.loginseguro.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mateusleite.loginseguro.user.Role;
import com.mateusleite.loginseguro.user.User;

/**
 * Usuário autenticado, guardado na sessão pelo Spring Security.
 * Leva só o necessário para as telas (nome, e-mail e role).
 */
public class UsuarioLogado implements UserDetails, CredentialsContainer {

    private final String id;
    private final String nome;
    private final String email;
    private final Role role;
    private final boolean ativo;
    private String senhaHash;

    public UsuarioLogado(User user) {
        this.id = user.getId();
        this.nome = user.getNome();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.ativo = user.isAtivo();
        this.senhaHash = user.getSenhaHash();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Role getRole() {
        return role;
    }

    /** O Spring Security espera o prefixo "ROLE_" (ex.: ROLE_ADMIN) para usar hasRole("ADMIN"). */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return senhaHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    /** Conta INATIVA = desabilitada: o Spring Security bloqueia o login. */
    @Override
    public boolean isEnabled() {
        return ativo;
    }

    /** Chamado após o login: apaga o hash para ele não ficar guardado na sessão. */
    @Override
    public void eraseCredentials() {
        this.senhaHash = null;
    }
}
