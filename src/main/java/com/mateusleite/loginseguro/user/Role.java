package com.mateusleite.loginseguro.user;

/**
 * Perfis de acesso do sistema. As regras de autorização dependem só da role.
 */
public enum Role {

    /** Quem se cadastra pelo formulário público. */
    ALUNO,

    /** Criado apenas pelo ADMIN, no painel administrativo. */
    ORIENTADOR,

    /** Administrador. O primeiro é criado automaticamente na inicialização. */
    ADMIN
}
