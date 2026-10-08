package com.mateusleite.loginseguro.config;

import org.mongodb.spring.session.config.annotation.web.http.EnableMongoHttpSession;
import org.springframework.context.annotation.Configuration;

/**
 * Guarda as sessões HTTP na coleção "sessions" do MongoDB Atlas, em vez da memória do servidor.
 * Assim o usuário continua logado mesmo se a aplicação reiniciar, e várias instâncias
 * da aplicação podem compartilhar as mesmas sessões.
 *
 * Desde o Spring Boot 4 não há configuração automática para isso, por isso a anotação explícita.
 * Sessões sem uso por 30 minutos expiram (o MongoDB remove o documento sozinho, via índice TTL).
 */
@Configuration
@EnableMongoHttpSession(collectionName = "sessions", maxInactiveIntervalInSeconds = 30 * 60)
public class SessionConfig {
}
