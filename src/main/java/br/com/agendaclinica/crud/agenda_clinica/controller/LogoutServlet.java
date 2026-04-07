package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.util.SecurityUtil;
import java.io.IOException;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            String email = (String) session.getAttribute("usuarioEmail");
            SecurityUtil.registrarAuditoria(email != null ? email : "UNKNOWN", "Logout", true);
            session.invalidate();
        }
        
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}
