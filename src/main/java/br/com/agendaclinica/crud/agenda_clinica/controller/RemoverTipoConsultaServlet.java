package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.TipoConsultaDAO;
import br.com.agendaclinica.crud.agenda_clinica.util.SecurityUtil;
import java.io.IOException;

@WebServlet("/RemoverTipoConsultaServlet")
public class RemoverTipoConsultaServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Verificar se é profissional
        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(2)) {
            session.setAttribute("erro", "❌ Acesso negado!");
            response.sendRedirect(request.getContextPath() + "/tipos-consulta.jsp");
            return;
        }

        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                session.setAttribute("erro", "ID do tipo de consulta não informado!");
                response.sendRedirect(request.getContextPath() + "/tipos-consulta.jsp");
                return;
            }

            Long id = Long.parseLong(idStr);
            TipoConsultaDAO dao = new TipoConsultaDAO();
            dao.deletar(id);

            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Tipo de consulta removido: ID " + id,
                true
            );

            session.setAttribute("sucesso", "✓ Tipo de consulta removido com sucesso!");
            response.sendRedirect(request.getContextPath() + "/tipos-consulta.jsp");

        } catch (NumberFormatException e) {
            session.setAttribute("erro", "ID inválido!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Tentativa de remover tipo de consulta com ID inválido",
                false
            );
            response.sendRedirect(request.getContextPath() + "/tipos-consulta.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("erro", "Erro ao remover tipo de consulta!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Erro ao remover tipo de consulta",
                false
            );
            response.sendRedirect(request.getContextPath() + "/tipos-consulta.jsp");
        }
    }
}