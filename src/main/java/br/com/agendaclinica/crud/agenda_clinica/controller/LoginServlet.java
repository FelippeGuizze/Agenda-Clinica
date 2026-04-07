package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.model.Usuario;
import br.com.agendaclinica.crud.agenda_clinica.dao.UsuarioDAO;
import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.buscarPorEmailESenha(email, senha);

            if (usuario != null) {
                // Login bem-sucedido
                HttpSession session = request.getSession();
                session.setAttribute("usuarioId", usuario.getId());
                session.setAttribute("usuarioNome", usuario.getNome());
                session.setAttribute("usuarioCategoria", usuario.getCategoria());
                session.setAttribute("usuarioEmail", usuario.getEmail());
                session.setAttribute("logado", true);

                // Redirecionar de acordo com a categoria
                if (usuario.getCategoria() == 1) {
                    // Paciente
                    response.sendRedirect(request.getContextPath() + "/dashboard-paciente.jsp");
                } else if (usuario.getCategoria() == 2) {
                    // Médico/Profissional
                    response.sendRedirect(request.getContextPath() + "/dashboard-medico.jsp");
                }
            } else {
                // Login inválido
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Email ou senha inválidos!");
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("erro", "Erro ao realizar login: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}
