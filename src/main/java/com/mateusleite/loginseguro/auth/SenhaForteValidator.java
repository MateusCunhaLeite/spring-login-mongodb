package com.mateusleite.loginseguro.auth;

import java.nio.charset.StandardCharsets;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementa a regra da anotação @SenhaForte.
 */
public class SenhaForteValidator implements ConstraintValidator<SenhaForte, String> {

    private static final int MINIMO = 8;

    /** O BCrypt só considera os primeiros 72 bytes da senha. */
    private static final int MAXIMO_BYTES = 72;

    @Override
    public boolean isValid(String senha, ConstraintValidatorContext context) {
        if (senha == null || senha.isEmpty()) {
            return true; // campo vazio é tratado pelo @NotBlank, com mensagem própria
        }
        return senha.length() >= MINIMO
                && senha.getBytes(StandardCharsets.UTF_8).length <= MAXIMO_BYTES
                && senha.chars().anyMatch(Character::isLetter)
                && senha.chars().anyMatch(Character::isDigit);
    }
}
