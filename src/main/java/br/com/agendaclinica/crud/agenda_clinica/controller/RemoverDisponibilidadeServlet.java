package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.DisponibilidadeProfissionalDAO;
import br.com.agendaclinica.crud.agenda_clinica.util.SecurityUtil;
import java.io.IOException;

@WebServlet("/RemoverDisponibilidadeServlet")
public class RemoverDisponibilidadeServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Verificar se é profissional
        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(2)) {
            session.setAttribute("erro", "❌ Acesso negado!");
            response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
            return;
        }

        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                session.setAttribute("erro", "ID da disponibilidade não informado!");
                response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
                return;
            }

            Long id = Long.parseLong(idStr);
            DisponibilidadeProfissionalDAO dao = new DisponibilidadeProfissionalDAO();
            dao.deletar(id);

            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Disponibilidade removida: ID " + id,
                true
            );

            session.setAttribute("sucesso", "✓ Disponibilidade removida com sucesso!");
            response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");

        } catch (NumberFormatException e) {
            session.setAttribute("erro", "ID inválido!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Tentativa de remover disponibilidade com ID inválido",
                false
            );
            response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("erro", "Erro ao remover disponibilidade!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Erro ao remover disponibilidade",
                false
            );
            response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
        }
    }
}