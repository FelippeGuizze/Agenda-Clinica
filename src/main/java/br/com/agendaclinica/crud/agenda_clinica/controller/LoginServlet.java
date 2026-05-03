package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.model.Usuario;
import br.com.agendaclinica.crud.agenda_clinica.dao.UsuarioDAO;
import br.com.agendaclinica.crud.agenda_clinica.util.SecurityUtil;
import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    /**
     * Determina a página de login correta para redirecionar em caso de erro,
     * baseado no parâmetro tipoLogin.
     */
    private String getPaginaLogin(String tipoLogin, String contextPath) {
        if (tipoLogin == null) return contextPath + "/login.jsp";
        switch (tipoLogin) {
            case "paciente": return contextPath + "/login-paciente.jsp";
            case "medico":   return contextPath + "/login-medico.jsp";
            case "admin":    return contextPath + "/login-admin.jsp";
            default:         return contextPath + "/login.jsp";
        }
    }

    /**
     * Valida se a categoria do usuário corresponde ao tipo de login tentado.
     * Retorna mensagem de erro ou null se OK.
     */
    private String validarCategoriaLogin(String tipoLogin, Integer categoriaUsuario) {
        if (tipoLogin == null) return null; // Login genérico, sem validação de tipo

        switch (tipoLogin) {
            case "paciente":
                if (categoriaUsuario != 1) {
                    if (categoriaUsuario == 2) {
                        return "Esta conta é de um profissional/médico. Faça login na área de médicos.";
                    } else if (categoriaUsuario == 0) {
                        return "Esta conta é administrativa. Faça login no painel ADM.";
                    }
                }
                break;
            case "medico":
                if (categoriaUsuario != 2) {
                    if (categoriaUsuario == 1) {
                        return "Esta conta é de um paciente. Faça login na área de pacientes.";
                    } else if (categoriaUsuario == 0) {
                        return "Esta conta é administrativa. Faça login no painel ADM.";
                    }
                }
                break;
            case "admin":
                if (categoriaUsuario != 0) {
                    return "Acesso negado. Esta conta não possui permissões administrativas.";
                }
                break;
        }
        return null; // OK
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String tipoLogin = request.getParameter("tipoLogin");
        String paginaLogin = getPaginaLogin(tipoLogin, request.getContextPath());

        // Validar entradas
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("erro", "Email e senha são obrigatórios!");
            SecurityUtil.registrarAuditoria(email != null ? email : "UNKNOWN", "Login - Campos vazios", false);
            response.sendRedirect(paginaLogin);
            return;
        }

        // Sanitizar entrada (proteção contra SQL Injection - Hibernate usa Prepared Statements)
        if (!SecurityUtil.isSafeInput(email) || !SecurityUtil.isSafeInput(senha)) {
            HttpSession session = request.getSession();
            session.setAttribute("erro", "Entrada contém caracteres inválidos!");
            SecurityUtil.registrarAuditoria(email, "Login - Entrada suspeita", false);
            response.sendRedirect(paginaLogin);
            return;
        }

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.buscarPorEmail(email);

            if (usuario != null && usuario.validarSenha(senha)) {
                // Validar se a categoria do usuário corresponde ao tipo de login
                String erroCategoria = validarCategoriaLogin(tipoLogin, usuario.getCategoria());
                if (erroCategoria != null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("erro", erroCategoria);
                    SecurityUtil.registrarAuditoria(email, "Login - Categoria incorreta (tentou: " + tipoLogin + ", real: " + usuario.getCategoria() + ")", false);
                    response.sendRedirect(paginaLogin);
                    return;
                }

                // Login bem-sucedido
                // Proteção contra Sequestro de Sessão (Session Fixation)
                request.changeSessionId(); 
                
                HttpSession session = request.getSession();
                session.setAttribute("usuarioId", usuario.getId());
                session.setAttribute("usuarioNome", SecurityUtil.escaparXSS(usuario.getNome()));
                session.setAttribute("usuarioCategoria", usuario.getCategoria());
                session.setAttribute("usuarioEmail", usuario.getEmail());
                session.setAttribute("profissionalId", usuario.getProfissionalId());
                session.setAttribute("pacienteId", usuario.getPacienteId());
                session.setAttribute("logado", true);

                SecurityUtil.registrarAuditoria(usuario.getEmail(), "Login bem-sucedido", true);

                // Redirecionar de acordo com a categoria
                if (usuario.getCategoria() == 1) {
                    // Paciente
                    response.sendRedirect(request.getContextPath() + "/dashboard-paciente.jsp");
                } else if (usuario.getCategoria() == 2) {
                    // Médico/Profissional
                    response.sendRedirect(request.getContextPath() + "/dashboard-medico.jsp");
                } else if (usuario.getCategoria() == 0) {
                    // Admin
                    response.sendRedirect(request.getContextPath() + "/dashboard-admin.jsp");
                }
            } else if (usuario == null) {
                // Email não existe
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Email não cadastrado no sistema!");
                SecurityUtil.registrarAuditoria(email, "Login - Email não encontrado", false);
                response.sendRedirect(paginaLogin);
            } else {
                // Senha incorreta
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Senha incorreta!");
                SecurityUtil.registrarAuditoria(email, "Login - Senha incorreta", false);
                response.sendRedirect(paginaLogin);
            }

        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("erro", "Erro ao realizar login!");
            SecurityUtil.registrarAuditoria(email, "Login - Erro na aplicação", false);
            response.sendRedirect(paginaLogin);
        }
    }
}
