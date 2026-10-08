package com.mateusleite.loginseguro.user;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Acesso à coleção "users". O Spring Data gera a implementação
 * a partir do nome dos métodos (ex.: findByEmail → { email: ? }).
 */
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
