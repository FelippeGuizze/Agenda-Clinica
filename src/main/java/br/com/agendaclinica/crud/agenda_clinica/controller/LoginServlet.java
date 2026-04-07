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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        // Validar entradas
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("erro", "Email e senha são obrigatórios!");
            SecurityUtil.registrarAuditoria(email != null ? email : "UNKNOWN", "Login - Campos vazios", false);
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Sanitizar entrada (proteção contra SQL Injection - Hibernate usa Prepared Statements)
        if (!SecurityUtil.isSafeInput(email) || !SecurityUtil.isSafeInput(senha)) {
            HttpSession session = request.getSession();
            session.setAttribute("erro", "Entrada contém caracteres inválidos!");
            SecurityUtil.registrarAuditoria(email, "Login - Entrada suspeita", false);
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.buscarPorEmail(email);

            if (usuario != null && usuario.validarSenha(senha)) {
                // Login bem-sucedido
                HttpSession session = request.getSession();
                session.setAttribute("usuarioId", usuario.getId());
                session.setAttribute("usuarioNome", SecurityUtil.escaparXSS(usuario.getNome()));
                session.setAttribute("usuarioCategoria", usuario.getCategoria());
                session.setAttribute("usuarioEmail", usuario.getEmail());
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
                session.setAttribute("erro", "❌ Email não cadastrado no sistema!");
                SecurityUtil.registrarAuditoria(email, "Login - Email não encontrado", false);
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            } else {
                // Senha incorreta
                HttpSession session = request.getSession();
                session.setAttribute("erro", "❌ Senha incorreta!");
                SecurityUtil.registrarAuditoria(email, "Login - Senha incorreta", false);
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("erro", "Erro ao realizar login!");
            SecurityUtil.registrarAuditoria(email, "Login - Erro na aplicação", false);
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}
