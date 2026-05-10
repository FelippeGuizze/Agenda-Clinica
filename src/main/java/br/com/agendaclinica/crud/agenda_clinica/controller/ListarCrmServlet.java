package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.CrmAutorizadoDAO;
import br.com.agendaclinica.crud.agenda_clinica.model.CrmAutorizado;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/ListarCrmServlet")
public class ListarCrmServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Verificar se é admin
        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(0)) {
            out.print("<tr><td colspan='5' style='padding: 12px; text-align: center; color: #ff6b6b;'>Acesso Negado</td></tr>");
            return;
        }

        try {
            CrmAutorizadoDAO crmDAO = new CrmAutorizadoDAO();
            List<CrmAutorizado> crms = crmDAO.listarTodos();

            if (crms.isEmpty()) {
                out.print("<tr><td colspan='7' style='padding: 20px; text-align: center; color: #aaa;'>Nenhum CRM autorizado cadastrado.</td></tr>");
                return;
            }

            for (CrmAutorizado crm : crms) {
                String statusColor = crm.getUsado() ? "#00ff88" : "#ffca28";
                String statusText = crm.getUsado() ? "✓ Utilizado" : "⏳ Disponível";

                String tipoAtend = crm.getTipoNicho() != null ? crm.getTipoNicho() : "N/D";
                String tipoIcon = "Exame".equals(tipoAtend) ? "🔬" : "🩺";
                String tipoColor = "Exame".equals(tipoAtend) ? "#a78bfa" : "#00d4ff";

                out.print("<tr style='border-bottom: 1px solid rgba(255,255,255,0.1);'>");
                out.print("<td style='padding: 12px;'><strong style='color: #00d4ff;'>" + crm.getCrmFormatado() + "</strong></td>");
                out.print("<td style='padding: 12px;'>" + (crm.getNomeAutorizado() != null ? crm.getNomeAutorizado() : "-") + "</td>");
                out.print("<td style='padding: 12px;'>" + crm.getEmailAutorizado() + "</td>");
                out.print("<td style='padding: 12px;'><span style='color: " + tipoColor + "; font-weight: 700;'>" + tipoIcon + " " + tipoAtend + "</span></td>");
                out.print("<td style='padding: 12px; color: " + statusColor + "; font-weight: 600;'>" + statusText + "</td>");
                out.print("<td style='padding: 12px; color: #aaa; font-size: 0.85em;'>" + crm.getDataCriacao().toLocalDate() + "</td>");
                out.print("<td style='padding: 12px; text-align: center;'>");

                if (!crm.getUsado()) {
                    out.print("<button onclick=\"removerCrm(" + crm.getId() + ")\" "
                        + "style='background: linear-gradient(135deg, #ff6b6b, #ee5a6f); color: white; border: none; "
                        + "padding: 6px 14px; border-radius: 5px; cursor: pointer; font-weight: 600; font-size: 0.85em;'>"
                        + "🗑️ Remover</button>");
                } else {
                    out.print("<span style='color: #666; font-size: 0.85em;'>—</span>");
                }

                out.print("</td>");
                out.print("</tr>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("<tr><td colspan='7' style='padding: 20px; text-align: center; color: #ff6b6b;'>Erro ao carregar CRMs: " + e.getMessage() + "</td></tr>");
        }
    }
}
