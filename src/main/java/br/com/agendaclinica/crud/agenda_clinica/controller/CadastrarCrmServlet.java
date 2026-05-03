package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.CrmAutorizadoDAO;
import br.com.agendaclinica.crud.agenda_clinica.model.CrmAutorizado;
import br.com.agendaclinica.crud.agenda_clinica.util.SecurityUtil;
import java.io.IOException;
import java.util.Set;

@WebServlet("/CadastrarCrmServlet")
public class CadastrarCrmServlet extends HttpServlet {

    // UFs válidas do Brasil
    private static final Set<String> UFS_VALIDAS = Set.of(
        "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO",
        "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI",
        "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"
    );

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Verificar se é admin
        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(0)) {
            session.setAttribute("erro", "Acesso negado. Apenas administradores podem autorizar CRMs.");
            response.sendRedirect(request.getContextPath() + "/dashboard-admin.jsp");
            return;
        }

        String crmNumero = request.getParameter("crm_numero");
        String crmUf = request.getParameter("crm_uf");
        String emailAutorizado = request.getParameter("email_autorizado");

        try {
            // Validações
            if (crmNumero == null || crmNumero.trim().isEmpty() ||
                crmUf == null || crmUf.trim().isEmpty() ||
                emailAutorizado == null || emailAutorizado.trim().isEmpty()) {
                session.setAttribute("erro", "Todos os campos são obrigatórios!");
                response.sendRedirect(request.getContextPath() + "/admin-crm.jsp");
                return;
            }

            crmNumero = crmNumero.trim();
            crmUf = crmUf.trim().toUpperCase();
            emailAutorizado = emailAutorizado.trim();

            // Validar formato CRM (apenas dígitos, máx 6)
            if (!crmNumero.matches("^\\d{1,6}$")) {
                session.setAttribute("erro", "Número do CRM inválido! Use apenas dígitos (máximo 6).");
                response.sendRedirect(request.getContextPath() + "/admin-crm.jsp");
                return;
            }

            // Validar UF
            if (!UFS_VALIDAS.contains(crmUf)) {
                session.setAttribute("erro", "UF inválida! Selecione um estado válido.");
                response.sendRedirect(request.getContextPath() + "/admin-crm.jsp");
                return;
            }

            // Validar email
            if (!SecurityUtil.validarEmail(emailAutorizado)) {
                session.setAttribute("erro", "Email inválido!");
                response.sendRedirect(request.getContextPath() + "/admin-crm.jsp");
                return;
            }

            // Sanitizar
            if (!SecurityUtil.isSafeInput(crmNumero) || !SecurityUtil.isSafeInput(emailAutorizado)) {
                session.setAttribute("erro", "Entrada contém caracteres inválidos!");
                response.sendRedirect(request.getContextPath() + "/admin-crm.jsp");
                return;
            }

            CrmAutorizadoDAO crmDAO = new CrmAutorizadoDAO();

            // Verificar duplicata de CRM
            if (crmDAO.crmJaExiste(crmNumero, crmUf)) {
                session.setAttribute("erro", "Este CRM (CRM/" + crmUf + " " + crmNumero + ") já está cadastrado!");
                response.sendRedirect(request.getContextPath() + "/admin-crm.jsp");
                return;
            }

            // Verificar duplicata de email
            if (crmDAO.emailJaAutorizado(emailAutorizado)) {
                session.setAttribute("erro", "Este email já possui um CRM autorizado!");
                response.sendRedirect(request.getContextPath() + "/admin-crm.jsp");
                return;
            }

            // Salvar
            CrmAutorizado crm = new CrmAutorizado(crmNumero, crmUf, emailAutorizado);
            crmDAO.salvar(crm);

            session.setAttribute("sucesso", "CRM/" + crmUf + " " + crmNumero + " autorizado com sucesso para " + emailAutorizado + "!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Autorização CRM: CRM/" + crmUf + " " + crmNumero + " → " + emailAutorizado,
                true
            );
            response.sendRedirect(request.getContextPath() + "/admin-crm.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("erro", "Erro ao cadastrar CRM: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin-crm.jsp");
        }
    }
}
