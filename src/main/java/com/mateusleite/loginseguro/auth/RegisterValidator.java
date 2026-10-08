package com.mateusleite.loginseguro.auth;

import java.util.Locale;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.mateusleite.loginseguro.config.AppProperties;
import com.mateusleite.loginseguro.user.UserService;

/**
 * Regras do cadastro que dependem de mais de um campo, da configuração ou do banco:
 * confirmação de senha, domínio permitido e e-mail duplicado.
 * (As regras de formato ficam nas anotações do RegisterForm.)
 */
@Component
public class RegisterValidator {

    private final AppProperties properties;
    private final UserService userService;

    public RegisterValidator(AppProperties properties, UserService userService) {
        this.properties = properties;
        this.userService = userService;
    }

    public void validar(RegisterForm form, Errors errors) {
        if (StringUtils.hasText(form.getConfirmacaoSenha())
                && !form.getConfirmacaoSenha().equals(form.getSenha())) {
            errors.rejectValue("confirmacaoSenha", "senha.diferente", "As senhas não conferem.");
        }

        if (errors.hasFieldErrors("email")) {
            return; // formato inválido: não adianta checar domínio nem duplicidade
        }
        String dominio = dominioPermitido();
        if (dominio != null && !pertenceAoDominio(form.getEmail(), dominio)) {
            errors.rejectValue("email", "email.dominio", "Use um e-mail do domínio " + dominio + ".");
        } else if (userService.emailJaCadastrado(form.getEmail())) {
            errors.rejectValue("email", "email.duplicado", "Este e-mail já está cadastrado. Que tal fazer login?");
        }
    }

    /** Domínio configurado em app.registration.allowed-domain, ou null se qualquer e-mail for aceito. */
    public String dominioPermitido() {
        String dominio = properties.registration().allowedDomain();
        return StringUtils.hasText(dominio) ? dominio.trim().toLowerCase(Locale.ROOT) : null;
    }

    /** Aceita o domínio exato e seus subdomínios: com "umc.br", vale "umc.br" e "alunos.umc.br", mas não "xumc.br". */
    static boolean pertenceAoDominio(String email, String dominio) {
        String dominioDoEmail = email.substring(email.lastIndexOf('@') + 1).trim().toLowerCase(Locale.ROOT);
        return dominioDoEmail.equals(dominio) || dominioDoEmail.endsWith("." + dominio);
    }
}
