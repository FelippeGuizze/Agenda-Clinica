package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.com.agendaclinica.crud.agenda_clinica.util.SeedService;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/VerificaEmailServlet")
public class VerificaEmailServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String email = request.getParameter("email");
        
        if (email == null || email.trim().isEmpty()) {
            out.write("{\"existe\": false, \"mensagem\": \"\"}");
            return;
        }
        
        boolean existe = SeedService.emailJaExiste(email);
        
        if (existe) {
            out.write("{\"existe\": true, \"mensagem\": \"❌ Email já cadastrado\", \"cor\": \"vermelha\"}");
        } else {
            out.write("{\"existe\": false, \"mensagem\": \"✓ Email disponível\", \"cor\": \"verde\"}");
        }
    }
}
