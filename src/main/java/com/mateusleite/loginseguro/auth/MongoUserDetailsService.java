package com.mateusleite.loginseguro.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mateusleite.loginseguro.user.User;
import com.mateusleite.loginseguro.user.UserRepository;

/**
 * Ensina o Spring Security a encontrar usuários na coleção "users" do MongoDB.
 * O e-mail digitado no login é o "username".
 * É criado no SecurityConfig, dentro do provedor de autenticação.
 */
public class MongoUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public MongoUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(User.normalizarEmail(email))
                .map(UsuarioLogado::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}
