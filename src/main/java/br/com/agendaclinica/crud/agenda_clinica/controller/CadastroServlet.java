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
import br.com.agendaclinica.crud.agenda_clinica.util.SecurityUtil;
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
            // Validações de entrada
            if (nome == null || email == null || senha == null || categoriaStr == null) {
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Todos os campos são obrigatórios!");
                response.sendRedirect(request.getContextPath() + "/cadastro.jsp");
                return;
            }

            // Sanitizar entradas contra XSS
            nome = SecurityUtil.escaparXSS(nome.trim());
            email = email.trim();
            contato = contato != null ? contato.trim() : "";
            especialidade = especialidade != null ? especialidade.trim() : "";

            // Validar formato de entrada
            if (!SecurityUtil.validarNome(nome)) {
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Nome inválido! Use apenas letras e espaços.");
                SecurityUtil.registrarAuditoria(email, "Cadastro - Nome inválido", false);
                response.sendRedirect(request.getContextPath() + "/cadastro.jsp");
                return;
            }

            if (!SecurityUtil.validarEmail(email)) {
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Email inválido!");
                SecurityUtil.registrarAuditoria(email, "Cadastro - Email inválido", false);
                response.sendRedirect(request.getContextPath() + "/cadastro.jsp");
                return;
            }

            if (!SecurityUtil.validarSenhaForte(senha)) {
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Senha deve ter no mínimo 6 caracteres!");
                SecurityUtil.registrarAuditoria(email, "Cadastro - Senha fraca", false);
                response.sendRedirect(request.getContextPath() + "/cadastro.jsp");
                return;
            }

            // Proteção contra SQL Injection (validar entrada)
            if (!SecurityUtil.isSafeInput(nome) || !SecurityUtil.isSafeInput(email) || 
                !SecurityUtil.isSafeInput(contato) || !SecurityUtil.isSafeInput(especialidade)) {
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Entrada contém caracteres inválidos!");
                SecurityUtil.registrarAuditoria(email, "Cadastro - Entrada suspeita", false);
                response.sendRedirect(request.getContextPath() + "/cadastro.jsp");
                return;
            }

            Integer categoria = Integer.parseInt(categoriaStr);

            // Verificar se email já existe
            if (SeedService.emailJaExiste(email)) {
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Este email já está cadastrado no sistema!");
                SecurityUtil.registrarAuditoria(email, "Cadastro - Email duplicado", false);
                response.sendRedirect(request.getContextPath() + "/cadastro.jsp");
                return;
            }

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Long pacienteId = null;
            Long profissionalId = null;

            // Se for paciente, criar registro em pacientes
            if (categoria == 1) {
                if (!SecurityUtil.validarContato(contato)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("erro", "Contato inválido! Use formato: (11) 98765-4321");
                    response.sendRedirect(request.getContextPath() + "/cadastro.jsp");
                    return;
                }

                Paciente paciente = new Paciente(nome, contato);
                PacienteDAO pacienteDAO = new PacienteDAO();
                pacienteDAO.salvar(paciente);
                pacienteId = paciente.getId();
            }
            // Se for profissional, criar registro em profissionais
            else if (categoria == 2) {
                if (especialidade.isEmpty()) {
                    HttpSession session = request.getSession();
                    session.setAttribute("erro", "Especialidade é obrigatória!");
                    response.sendRedirect(request.getContextPath() + "/cadastro.jsp");
                    return;
                }

                Profissional profissional = new Profissional(nome, "Profissional", especialidade);
                ProfissionalDAO profissionalDAO = new ProfissionalDAO();
                profissionalDAO.salvar(profissional);
                profissionalId = profissional.getId();
            }

            // Criar usuário com links para pacientes ou profissionais
            // A senha será criptografada automaticamente no construtor
            Usuario usuario = new Usuario(nome, email, senha, categoria);
            usuario.setPacienteId(pacienteId);
            usuario.setProfissionalId(profissionalId);
            usuarioDAO.salvar(usuario);

            // Redirecionar para login com mensagem de sucesso
            HttpSession session = request.getSession();
            session.setAttribute("sucesso", "Cadastro realizado com sucesso! Faça login agora.");
            SecurityUtil.registrarAuditoria(email, "Cadastro bem-sucedido", true);
            response.sendRedirect(request.getContextPath() + "/login.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("erro", "Erro ao realizar cadastro!");
            String emailFail = request.getParameter("email");
            SecurityUtil.registrarAuditoria(emailFail != null ? emailFail : "UNKNOWN", "Cadastro - Erro na aplicação", false);
            response.sendRedirect(request.getContextPath() + "/cadastro.jsp");
        }
    }
}
