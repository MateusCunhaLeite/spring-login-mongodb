package com.mateusleite.loginseguro.config;

/**
 * Tema visual. O "id" é o nome da pasta em static/themes/, de onde vem o theme.css.
 * Para criar um tema novo: adicione a pasta com o CSS e uma constante aqui.
 */
public record Tema(String id, String nome, String subtitulo) {

    public static final Tema DEFAULT = new Tema("default", "Login Seguro", "Autenticação com Spring Security");
    public static final Tema ATHENA = new Tema("athena", "Athena", "O Portal do PFC");

    /** Tema configurado em app.theme. Valor desconhecido ou vazio cai no tema padrão. */
    public static Tema of(String id) {
        return ATHENA.id().equalsIgnoreCase(id) ? ATHENA : DEFAULT;
    }
}
