package br.com.agendaclinica.crud.agenda_clinica.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.com.agendaclinica.crud.agenda_clinica.model.Usuario;
import br.com.agendaclinica.crud.agenda_clinica.dao.UsuarioDAO;

@WebServlet("/cadastrar")
public class UsuarioServlet extends HttpServlet {
    private UsuarioDAO dao = new UsuarioDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String nome = request.getParameter("nomeUsuario");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        Integer categoria = Integer.parseInt(request.getParameter("categoria"));
        Usuario user = new Usuario(nome, email, senha, categoria);
        
        dao.salvar(user); // Função pra colocar no banco
        
        response.getWriter().println("<h1>Usuario " + nome + " salvo com sucesso!</h1>");
    }
}