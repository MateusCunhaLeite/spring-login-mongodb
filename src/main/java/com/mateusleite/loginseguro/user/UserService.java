package com.mateusleite.loginseguro.user;

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

    public User criar(String nome, String email, String senha, Role role) {
        String senhaHash = passwordEncoder.encode(senha);
        return repository.save(new User(nome, email, senhaHash, role));
    }
}
