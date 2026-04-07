package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.model.Usuario;
import br.com.agendaclinica.crud.agenda_clinica.model.Paciente;
import br.com.agendaclinica.crud.agenda_clinica.model.Profissional;
import br.com.agendaclinica.crud.agenda_clinica.dao.UsuarioDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.PacienteDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.ProfissionalDAO;
import br.com.agendaclinica.crud.agenda_clinica.util.SeedService;
import java.io.IOException;

@WebServlet("/CadastroServlet")
public class CadastroServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String categoriaStr = request.getParameter("categoria");
        String contato = request.getParameter("contato");
        String especialidade = request.getParameter("especialidade");

        try {
            Integer categoria = Integer.parseInt(categoriaStr);

            // Verificar se email já existe na tabela usuarios
            if (SeedService.emailJaExiste(email)) {
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Este email já está cadastrado no sistema!");
                session.setAttribute("tipoMensagem", "erro"); // Mensagem vermelha
                response.sendRedirect(request.getContextPath() + "/cadastro.jsp");
                return;
            }

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Long pacienteId = null;
            Long profissionalId = null;

            // Se for paciente, criar registro em pacientes e salvar o ID
            if (categoria == 1) {
                Paciente paciente = new Paciente(nome, contato);
                PacienteDAO pacienteDAO = new PacienteDAO();
                pacienteDAO.salvar(paciente);
                // Buscar o ID do paciente recém criado
                pacienteId = paciente.getId();
            }
            // Se for profissional, criar registro em profissionais e salvar o ID
            else if (categoria == 2) {
                Profissional profissional = new Profissional(nome, "Profissional", especialidade);
                ProfissionalDAO profissionalDAO = new ProfissionalDAO();
                profissionalDAO.salvar(profissional);
                // Buscar o ID do profissional recém criado
                profissionalId = profissional.getId();
            }

            // Criar usuário com links para pacientes ou profissionais
            Usuario usuario = new Usuario(nome, email, senha, categoria);
            usuario.setPacienteId(pacienteId);
            usuario.setProfissionalId(profissionalId);
            usuarioDAO.salvar(usuario);

            // Redirecionar para login com mensagem de sucesso (verde)
            HttpSession session = request.getSession();
            session.setAttribute("sucesso", "Cadastro realizado com sucesso! Faça login agora.");
            session.setAttribute("tipoMensagem", "sucesso"); // Mensagem verde
            response.sendRedirect(request.getContextPath() + "/login.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("erro", "Erro ao realizar cadastro: " + e.getMessage());
            session.setAttribute("tipoMensagem", "erro"); // Mensagem vermelha
            response.sendRedirect(request.getContextPath() + "/cadastro.jsp");
        }
    }
}
