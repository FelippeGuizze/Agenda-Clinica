package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.CrmAutorizadoDAO;
import br.com.agendaclinica.crud.agenda_clinica.util.SecurityUtil;
import java.io.IOException;

@WebServlet("/RemoverCrmServlet")
public class RemoverCrmServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Verificar se é admin
        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(0)) {
            session.setAttribute("erro", "Acesso negado.");
            response.sendRedirect(request.getContextPath() + "/dashboard-admin.jsp");
            return;
        }

        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                session.setAttribute("erro", "ID inválido!");
                response.sendRedirect(request.getContextPath() + "/admin-crm.jsp");
                return;
            }

            Long id = Long.parseLong(idStr);
            CrmAutorizadoDAO crmDAO = new CrmAutorizadoDAO();
            crmDAO.remover(id);

            session.setAttribute("sucesso", "Autorização CRM removida com sucesso!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Remoção de autorização CRM ID: " + id,
                true
            );
            response.sendRedirect(request.getContextPath() + "/admin-crm.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("erro", "Erro ao remover CRM: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin-crm.jsp");
        }
    }
}
