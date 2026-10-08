package com.mateusleite.loginseguro.auth;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Regra de senha do sistema: de 8 a 72 caracteres, com pelo menos uma letra e um número.
 * Uso: anote o campo com @SenhaForte.
 */
@Documented
@Constraint(validatedBy = SenhaForteValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SenhaForte {

    String message() default "Use de 8 a 72 caracteres, com pelo menos uma letra e um número.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
