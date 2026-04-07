package br.com.agendaclinica.crud.agenda_clinica.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.owasp.encoder.Encode;
import java.security.SecureRandom;

/**
 * Utilitário para segurança da aplicação
 * - Criptografia BCrypt de senhas
 * - Proteção contra XSS
 * - Validação de entrada
 * - Proteção contra SQL Injection (usando Prepared Statements do Hibernate)
 */
public class SecurityUtil {

    /**
     * Criptografa uma senha usando BCrypt
     * @param senha Senha em texto plano
     * @return Senha criptografada com BCrypt
     */
    public static String criptografarSenha(String senha) {
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
        return BCrypt.withDefaults().hashToString(12, senha.toCharArray());
    }

    /**
     * Valida uma senha contra seu hash BCrypt
     * @param senha Senha em texto plano a verificar
     * @param hash Hash da senha armazenada
     * @return true se a senha corresponde, false caso contrário
     */
    public static boolean validarSenha(String senha, String hash) {
        if (senha == null || hash == null) {
            return false;
        }
        try {
            return BCrypt.verifyer().verify(senha.toCharArray(), hash.toCharArray()).verified;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Escapa strings para prevenir XSS (Cross-Site Scripting)
     * @param input String a escapar
     * @return String escapada
     */
    public static String escaparXSS(String input) {
        if (input == null) {
            return "";
        }
        return Encode.forHtml(input);
    }

    /**
     * Valida entrada de email
     * @param email Email a validar
     * @return true se email é válido
     */
    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Valida senha (mínimo 6 caracteres, sem espaços)
     * @param senha Senha a validar
     * @return true se senha é válida
     */
    public static boolean validarSenhaForte(String senha) {
        if (senha == null || senha.isEmpty()) {
            return false;
        }
        // Mínimo 6 caracteres, sem espaços em branco
        return senha.length() >= 6 && !senha.contains(" ");
    }

    /**
     * Valida nome (sem caracteres perigosos)
     * @param nome Nome a validar
     * @return true se nome é válido
     */
    public static boolean validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        // Apenas letras, espaços e alguns caracteres especiais comuns
        String nomeRegex = "^[a-zA-ZÀ-ÿ\\s'-]{2,255}$";
        return nome.matches(nomeRegex);
    }

    /**
     * Sanitiza entrada para prevenir SQL Injection (com Prepared Statements no Hibernate)
     * Esta função valida caracteres perigosos
     * @param input String a sanitizar
     * @return true se string é segura
     */
    public static boolean isSafeInput(String input) {
        if (input == null) {
            return true; // null é seguro, será tratado pelo Hibernate
        }
        // Bloqueia padrões SQL perigosos comuns
        String dangerousPatterns = "(?i)(--|;|\\*|'|\"|(union).*(select)|(drop).*(table)|(insert).*(into)|(update).*(set)|(delete).*(from))";
        return !input.matches(".*" + dangerousPatterns + ".*");
    }

    /**
     * Gera um token CSRF aleatório para proteger formulários
     * @return Token aleatório de 32 caracteres
     */
    public static String gerarTokenCSRF() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Valida telefone/contato
     * @param contato Contato a validar
     * @return true se contato é válido
     */
    public static boolean validarContato(String contato) {
        if (contato == null || contato.isEmpty()) {
            return false;
        }
        // Aceita números, parênteses, hífens, espaços
        String contatoRegex = "^[0-9()\\s\\-+]{8,20}$";
        return contato.matches(contatoRegex);
    }

    /**
     * Registra tentativas de acesso para auditoria
     * @param usuario Email do usuário
     * @param acao Ação realizada
     * @param sucesso Se a ação foi bem-sucedida
     */
    public static void registrarAuditoria(String usuario, String acao, boolean sucesso) {
        String status = sucesso ? "✓ SUCESSO" : "✗ FALHA";
        System.out.println("[AUDITORIA] " + status + " | Usuário: " + usuario + " | Ação: " + acao + " | Timestamp: " + System.currentTimeMillis());
    }
}
