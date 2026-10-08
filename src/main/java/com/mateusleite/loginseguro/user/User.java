package com.mateusleite.loginseguro.user;

import java.time.Instant;
import java.util.Locale;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Usuário do sistema, salvo na coleção "users" do MongoDB.
 * Guarda apenas os dados necessários (minimização de dados, LGPD).
 */
@Document("users")
public class User {

    @Id
    private String id;

    private String nome;

    /** Usado como login. Sempre salvo em minúsculas para evitar duplicatas como "Ana@x.com" e "ana@x.com". */
    @Indexed(unique = true)
    private String email;

    /** Hash BCrypt da senha. A senha em texto puro nunca é salva. */
    private String senhaHash;

    private Role role;

    private Status status;

    /** Sempre false por enquanto: confirmação de e-mail é trabalho futuro. */
    private boolean emailConfirmado;

    private Instant criadoEm;

    /** Usado pelo Spring Data ao ler o documento do banco. */
    protected User() {
    }

    /** Cria um usuário novo, ativo e com e-mail ainda não confirmado. */
    public User(String nome, String email, String senhaHash, Role role) {
        this.nome = nome.trim();
        this.email = normalizarEmail(email);
        this.senhaHash = senhaHash;
        this.role = role;
        this.status = Status.ATIVO;
        this.emailConfirmado = false;
        this.criadoEm = Instant.now();
    }

    public static String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public boolean isAtivo() {
        return status == Status.ATIVO;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Role getRole() {
        return role;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isEmailConfirmado() {
        return emailConfirmado;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
