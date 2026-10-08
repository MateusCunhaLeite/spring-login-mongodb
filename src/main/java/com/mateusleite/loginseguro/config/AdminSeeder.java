package com.mateusleite.loginseguro.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.mateusleite.loginseguro.user.Role;
import com.mateusleite.loginseguro.user.UserService;

/**
 * Cria o primeiro ADMIN na inicialização, com e-mail e senha vindos do .env
 * (ADMIN_EMAIL e ADMIN_PASSWORD). Se o admin já existir, não faz nada.
 */
@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final AppProperties properties;
    private final UserService userService;

    public AdminSeeder(AppProperties properties, UserService userService) {
        this.properties = properties;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        String email = properties.admin().email();
        String senha = properties.admin().password();

        if (!StringUtils.hasText(email) || !StringUtils.hasText(senha)) {
            log.warn("ADMIN_EMAIL/ADMIN_PASSWORD não configurados: nenhum administrador inicial foi criado.");
            return;
        }
        if (userService.emailJaCadastrado(email)) {
            return;
        }
        userService.criar("Administrador", email, senha, Role.ADMIN);
        log.info("Administrador inicial criado: {}", email);
    }
}
