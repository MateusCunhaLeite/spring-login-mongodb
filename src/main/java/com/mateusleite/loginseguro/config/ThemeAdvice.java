package com.mateusleite.loginseguro.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Disponibiliza o tema ativo (variável "tema") para todos os templates.
 * O layout base usa ${tema.id} para carregar /themes/{id}/theme.css.
 */
@ControllerAdvice
public class ThemeAdvice {

    private final Tema tema;

    public ThemeAdvice(AppProperties properties) {
        this.tema = Tema.of(properties.theme());
    }

    @ModelAttribute("tema")
    public Tema tema() {
        return tema;
    }
}
