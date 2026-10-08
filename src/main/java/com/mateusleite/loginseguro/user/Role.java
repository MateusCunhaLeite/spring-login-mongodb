package com.mateusleite.loginseguro.user;

/**
 * Perfis de acesso do sistema. As regras de autorização dependem só da role.
 */
public enum Role {

    /** Quem se cadastra pelo formulário público. */
    ALUNO("Aluno"),

    /** Criado apenas pelo ADMIN, no painel administrativo. */
    ORIENTADOR("Orientador"),

    /** Administrador. O primeiro é criado automaticamente na inicialização. */
    ADMIN("Administrador");

    /** Nome amigável, exibido nas telas. */
    private final String rotulo;

    Role(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
