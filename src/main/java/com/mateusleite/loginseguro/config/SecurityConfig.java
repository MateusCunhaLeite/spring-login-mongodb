package com.mateusleite.loginseguro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.mateusleite.loginseguro.auth.MongoUserDetailsService;
import com.mateusleite.loginseguro.user.UserRepository;

/**
 * Autenticação (quem é você?) e autorização (o que você pode acessar?).
 *
 * As regras de acesso dependem SÓ da role, não de COMO o usuário se autenticou.
 * Para trocar o formulário por OIDC/JWT (ex.: AWS Cognito, com a role vinda do
 * claim "custom:perfil"), basta mudar este arquivo: controllers e telas continuam iguais.
 */
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/cadastro", "/error", "/css/**", "/themes/**").permitAll()
                .requestMatchers("/aluno/**").hasRole("ALUNO")
                .requestMatchers("/orientador/**").hasRole("ORIENTADOR")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("senha")
                .defaultSuccessUrl("/dashboard", true)
                .failureHandler((request, response, exception) -> {
                    String motivo = exception instanceof DisabledException ? "inativa" : "erro";
                    response.sendRedirect(request.getContextPath() + "/login?" + motivo);
                }))
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?saiu")
                .invalidateHttpSession(true));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(UserRepository userRepository,
                                                     PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(new MongoUserDetailsService(userRepository));
        provider.setPasswordEncoder(passwordEncoder);
        // Só verifica "conta inativa" DEPOIS de conferir a senha.
        // Assim, quem não sabe a senha não descobre se a conta existe ou está inativa.
        provider.setPreAuthenticationChecks(user -> { });
        provider.setPostAuthenticationChecks(new AccountStatusUserDetailsChecker());
        return provider;
    }
}
