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
import br.com.agendaclinica.crud.agenda_clinica.model.CrmAutorizado;
import br.com.agendaclinica.crud.agenda_clinica.dao.UsuarioDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.PacienteDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.ProfissionalDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.CrmAutorizadoDAO;
import br.com.agendaclinica.crud.agenda_clinica.util.SeedService;
import br.com.agendaclinica.crud.agenda_clinica.util.SecurityUtil;
import java.io.IOException;

@WebServlet("/CadastroServlet")
public class CadastroServlet extends HttpServlet {

    /**
     * Determina as páginas de redirecionamento baseado no origemCadastro.
     */
    private String getPaginaCadastro(String origem, String contextPath) {
        if (origem == null) return contextPath + "/cadastro.jsp";
        switch (origem) {
            case "paciente": return contextPath + "/cadastro-paciente.jsp";
            case "medico":   return contextPath + "/cadastro-medico.jsp";
            default:         return contextPath + "/cadastro.jsp";
        }
    }

    private String getPaginaLoginSucesso(String origem, String contextPath) {
        if (origem == null) return contextPath + "/login.jsp";
        switch (origem) {
            case "paciente": return contextPath + "/login-paciente.jsp";
            case "medico":   return contextPath + "/login-medico.jsp";
            default:         return contextPath + "/login.jsp";
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String categoriaStr = request.getParameter("categoria");
        String contato = request.getParameter("contato");
        String especialidade = request.getParameter("especialidade");
        String origemCadastro = request.getParameter("origemCadastro");
        String crmNumero = request.getParameter("crm_numero");
        String crmUf = request.getParameter("crm_uf");

        String paginaCadastro = getPaginaCadastro(origemCadastro, request.getContextPath());
        String paginaLogin = getPaginaLoginSucesso(origemCadastro, request.getContextPath());

        try {
            // Validações de entrada
            if (nome == null || email == null || senha == null || categoriaStr == null) {
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Todos os campos são obrigatórios!");
                response.sendRedirect(paginaCadastro);
                return;
            }

            // Sanitizar entradas contra XSS
            nome = SecurityUtil.escaparXSS(nome.trim());
            email = email.trim();
            contato = contato != null ? contato.trim() : "";
            especialidade = especialidade != null ? especialidade.trim() : "";
            crmNumero = crmNumero != null ? crmNumero.trim() : "";
            crmUf = crmUf != null ? crmUf.trim().toUpperCase() : "";

            // Validar formato de entrada
            if (!SecurityUtil.validarNome(nome)) {
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Nome inválido! Use apenas letras e espaços.");
                SecurityUtil.registrarAuditoria(email, "Cadastro - Nome inválido", false);
                response.sendRedirect(paginaCadastro);
                return;
            }

            if (!SecurityUtil.validarEmail(email)) {
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Email inválido!");
                SecurityUtil.registrarAuditoria(email, "Cadastro - Email inválido", false);
                response.sendRedirect(paginaCadastro);
                return;
            }

            if (!SecurityUtil.validarSenhaForte(senha)) {
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Senha deve ter no mínimo 6 caracteres!");
                SecurityUtil.registrarAuditoria(email, "Cadastro - Senha fraca", false);
                response.sendRedirect(paginaCadastro);
                return;
            }

            // Proteção contra SQL Injection (validar entrada)
            if (!SecurityUtil.isSafeInput(nome) || !SecurityUtil.isSafeInput(email) || 
                !SecurityUtil.isSafeInput(contato) || !SecurityUtil.isSafeInput(especialidade)) {
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Entrada contém caracteres inválidos!");
                SecurityUtil.registrarAuditoria(email, "Cadastro - Entrada suspeita", false);
                response.sendRedirect(paginaCadastro);
                return;
            }

            Integer categoria = Integer.parseInt(categoriaStr);

            // Verificar se email já existe
            if (SeedService.emailJaExiste(email)) {
                HttpSession session = request.getSession();
                session.setAttribute("erro", "Este email já está cadastrado no sistema!");
                SecurityUtil.registrarAuditoria(email, "Cadastro - Email duplicado", false);
                response.sendRedirect(paginaCadastro);
                return;
            }

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Long pacienteId = null;
            Long profissionalId = null;

            // Se for paciente, criar registro em pacientes
            if (categoria == 1) {
                // A validação de formato e comprimento do contato (máscara)
                // agora é garantida no front-end (HTML5) e no BD (VARCHAR 15)

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
                    response.sendRedirect(paginaCadastro);
                    return;
                }

                // === VALIDAÇÃO CRM ===
                // Médicos precisam de um CRM autorizado pelo administrador
                if (crmNumero.isEmpty() || crmUf.isEmpty()) {
                    HttpSession session = request.getSession();
                    session.setAttribute("erro", "CRM é obrigatório para cadastro de médico! Informe o número CRM e UF.");
                    SecurityUtil.registrarAuditoria(email, "Cadastro médico - CRM não informado", false);
                    response.sendRedirect(paginaCadastro);
                    return;
                }

                // Validar formato CRM (apenas dígitos, máx 6)
                if (!crmNumero.matches("^\\d{1,6}$")) {
                    HttpSession session = request.getSession();
                    session.setAttribute("erro", "Número do CRM inválido! Use apenas dígitos (máximo 6).");
                    response.sendRedirect(paginaCadastro);
                    return;
                }

                // Verificar se o CRM + email está autorizado pelo admin
                CrmAutorizadoDAO crmDAO = new CrmAutorizadoDAO();
                CrmAutorizado autorizacao = crmDAO.buscarPorEmailECrm(email, crmNumero, crmUf);

                if (autorizacao == null) {
                    HttpSession session = request.getSession();
                    session.setAttribute("erro", "CRM não autorizado! Solicite autorização ao administrador do sistema. "
                        + "O CRM e email informados devem corresponder à autorização previamente cadastrada.");
                    SecurityUtil.registrarAuditoria(email, "Cadastro médico - CRM não autorizado (CRM/" + crmUf + " " + crmNumero + ")", false);
                    response.sendRedirect(paginaCadastro);
                    return;
                }

                if (autorizacao.getUsado()) {
                    HttpSession session = request.getSession();
                    session.setAttribute("erro", "Este CRM já foi utilizado para cadastro! Cada CRM só pode ser usado uma vez.");
                    SecurityUtil.registrarAuditoria(email, "Cadastro médico - CRM já usado (CRM/" + crmUf + " " + crmNumero + ")", false);
                    response.sendRedirect(paginaCadastro);
                    return;
                }

                // CRM autorizado — criar profissional com CRM, nome e nicho herdados do admin
                String nomeDefinitivo = (autorizacao.getNomeAutorizado() != null && !autorizacao.getNomeAutorizado().isEmpty()) 
                                        ? autorizacao.getNomeAutorizado() : nome;
                
                Profissional profissional = new Profissional(nomeDefinitivo, "Profissional", especialidade);
                profissional.setCrmNumero(crmNumero);
                profissional.setCrmUf(crmUf);
                // Herdar tipoNicho do CRM autorizado (definido pelo admin)
                if (autorizacao.getTipoNicho() != null && !autorizacao.getTipoNicho().isEmpty()) {
                    profissional.setTipoNicho(autorizacao.getTipoNicho());
                } else {
                    profissional.setTipoNicho("Consulta"); // padrão se admin não definiu
                }
                ProfissionalDAO profissionalDAO = new ProfissionalDAO();
                profissionalDAO.salvar(profissional);
                profissionalId = profissional.getId();
                
                // Sobrescrever a variável nome local para que o Usuário também receba o nome correto
                nome = nomeDefinitivo;

                // Marcar CRM como usado
                crmDAO.marcarComoUsado(autorizacao.getId());
            }

            // Criar usuário com links para pacientes ou profissionais
            // A senha será criptografada automaticamente no construtor
            Usuario usuario = new Usuario(nome, email, senha, categoria);
            usuario.setPacienteId(pacienteId);
            usuario.setProfissionalId(profissionalId);
            usuarioDAO.salvar(usuario);

            // Redirecionar para login CORRETO com mensagem de sucesso
            HttpSession session = request.getSession();
            session.setAttribute("sucesso", "Cadastro realizado com sucesso! Faça login agora.");
            SecurityUtil.registrarAuditoria(email, "Cadastro bem-sucedido", true);
            response.sendRedirect(paginaLogin);

        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("erro", "Erro ao realizar cadastro!");
            String emailFail = request.getParameter("email");
            SecurityUtil.registrarAuditoria(emailFail != null ? emailFail : "UNKNOWN", "Cadastro - Erro na aplicação", false);
            response.sendRedirect(paginaCadastro);
        }
    }
}
