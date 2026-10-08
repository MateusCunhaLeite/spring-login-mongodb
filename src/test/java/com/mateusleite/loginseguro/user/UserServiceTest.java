package com.mateusleite.loginseguro.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Testes de unidade das regras do UserService. O banco é simulado (mock).
 */
class UserServiceTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UserRepository repository;
    private UserService service;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        service = new UserService(repository, passwordEncoder);
    }

    @Test
    void cadastroPublicoSempreCriaAluno() {
        User criado = service.cadastrarAluno("Ana Souza", "ana@exemplo.com", "senhaForte1");

        assertThat(criado.getRole()).isEqualTo(Role.ALUNO);
        assertThat(criado.getStatus()).isEqualTo(Status.ATIVO);
        assertThat(criado.isEmailConfirmado()).isFalse();
    }

    @Test
    void senhaEhSalvaComoHashBCrypt() {
        service.criar("Ana Souza", "ana@exemplo.com", "senhaForte1", Role.ALUNO);

        ArgumentCaptor<User> salvo = ArgumentCaptor.forClass(User.class);
        verify(repository).save(salvo.capture());
        String hash = salvo.getValue().getSenhaHash();

        assertThat(hash).isNotEqualTo("senhaForte1").startsWith("$2");
        assertThat(passwordEncoder.matches("senhaForte1", hash)).isTrue();
    }

    @Test
    void adminNaoPodeDesativarASiMesmo() {
        User admin = new User("Admin", "admin@exemplo.com", "hash", Role.ADMIN);
        when(repository.findById("id-admin")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> service.alterarStatus("id-admin", Status.INATIVO, "ADMIN@exemplo.com"))
                .isInstanceOf(IllegalStateException.class);
        verify(repository, never()).save(any());
    }
}
