package com.mateusleite.loginseguro.user;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Regras de negócio do usuário. É o único lugar que transforma
 * senha em hash, então nenhuma senha em texto puro chega ao banco.
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailJaCadastrado(String email) {
        return repository.existsByEmail(User.normalizarEmail(email));
    }

    /** Cadastro público: a role é sempre ALUNO, nunca vem do formulário. */
    public User cadastrarAluno(String nome, String email, String senha) {
        return criar(nome, email, senha, Role.ALUNO);
    }

    public User criar(String nome, String email, String senha, Role role) {
        String senhaHash = passwordEncoder.encode(senha);
        return repository.save(new User(nome, email, senhaHash, role));
    }

    public User buscarPorEmail(String email) {
        return repository.findByEmail(User.normalizarEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public List<User> listarTodos() {
        return repository.findAllByOrderByNomeAsc();
    }

    public List<User> listarAlunos() {
        return repository.findByRoleOrderByNomeAsc(Role.ALUNO);
    }

    /**
     * Ativa ou desativa uma conta. Um admin não pode desativar a si mesmo,
     * para o sistema nunca ficar sem ninguém capaz de administrá-lo.
     */
    public User alterarStatus(String id, Status novoStatus, String emailDeQuemAltera) {
        User user = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        if (novoStatus == Status.INATIVO && user.getEmail().equals(User.normalizarEmail(emailDeQuemAltera))) {
            throw new IllegalStateException("Você não pode desativar a sua própria conta.");
        }
        user.setStatus(novoStatus);
        return repository.save(user);
    }
}
