package com.mateusleite.loginseguro.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configurações próprias da aplicação (prefixo "app" no application.yml).
 * Centraliza tudo num só lugar, em vez de espalhar @Value pelo código.
 */
@ConfigurationProperties("app")
public record AppProperties(String theme, Registration registration, Admin admin) {

    /** Regras do cadastro público. */
    public record Registration(String allowedDomain) {
    }

    /** Dados do primeiro administrador, criado na inicialização. */
    public record Admin(String email, String password) {
    }
}
