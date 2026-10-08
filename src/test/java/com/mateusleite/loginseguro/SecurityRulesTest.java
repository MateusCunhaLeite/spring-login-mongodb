package com.mateusleite.loginseguro;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mateusleite.loginseguro.auth.RegisterValidator;
import com.mateusleite.loginseguro.auth.UsuarioLogado;
import com.mateusleite.loginseguro.config.AppProperties;
import com.mateusleite.loginseguro.config.SecurityConfig;
import com.mateusleite.loginseguro.user.Role;
import com.mateusleite.loginseguro.user.User;
import com.mateusleite.loginseguro.user.UserRepository;
import com.mateusleite.loginseguro.user.UserService;

/**
 * Testes da camada web com as regras reais do SecurityConfig.
 * O MongoDB não é usado: UserService e UserRepository são simulados (mocks).
 */
@WebMvcTest
@Import({ SecurityConfig.class, RegisterValidator.class })
@EnableConfigurationProperties(AppProperties.class)
class SecurityRulesTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void anonimoEhRedirecionadoParaOLogin() throws Exception {
        mvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void alunoNaoAcessaAreaAdmin() throws Exception {
        mvc.perform(get("/admin").with(user(logado(Role.ALUNO))))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminAcessaAreaAdmin() throws Exception {
        when(userService.listarTodos()).thenReturn(List.of());

        mvc.perform(get("/admin").with(user(logado(Role.ADMIN))))
                .andExpect(status().isOk());
    }

    @Test
    void cadastroIgnoraRoleEnviadaNoFormularioECriaAluno() throws Exception {
        when(userService.emailJaCadastrado(anyString())).thenReturn(false);

        mvc.perform(post("/cadastro").with(csrf())
                        .param("nome", "Ana Souza")
                        .param("email", "ana@exemplo.com")
                        .param("senha", "senhaForte1")
                        .param("confirmacaoSenha", "senhaForte1")
                        .param("role", "ADMIN")) // tentativa de "promover" a conta pelo formulário
                .andExpect(redirectedUrl("/login?cadastrado"));

        verify(userService).cadastrarAluno("Ana Souza", "ana@exemplo.com", "senhaForte1");
        verify(userService, never()).criar(anyString(), anyString(), anyString(), any(Role.class));
    }

    @Test
    void formularioSemTokenCsrfEhRecusado() throws Exception {
        mvc.perform(post("/cadastro").param("nome", "Ana"))
                .andExpect(status().isForbidden());
    }

    private static UsuarioLogado logado(Role role) {
        return new UsuarioLogado(new User("Usuário Teste", "teste@exemplo.com", "hash", role));
    }
}
