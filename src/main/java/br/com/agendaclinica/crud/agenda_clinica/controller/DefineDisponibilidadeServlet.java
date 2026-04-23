package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.model.DisponibilidadeProfissional;
import br.com.agendaclinica.crud.agenda_clinica.dao.DisponibilidadeProfissionalDAO;
import br.com.agendaclinica.crud.agenda_clinica.util.SecurityUtil;
import java.io.IOException;

@WebServlet("/DefineDisponibilidadeServlet")
public class DefineDisponibilidadeServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();

        // Verificar se é profissional
        if (session.getAttribute("usuarioCategoria") == null || 
            !session.getAttribute("usuarioCategoria").equals(2)) {
            session.setAttribute("erro", "❌ Acesso restrito! Apenas profissionais podem definir disponibilidade.");
            response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
            return;
        }

        try {
            String dia = request.getParameter("dia");
            String horario = request.getParameter("horario");
            String ativoStr = request.getParameter("ativo");
            Long profissionalId = (Long) session.getAttribute("usuarioId");

            // Validações
            if (horario == null || horario.trim().isEmpty()) {
                session.setAttribute("erro", "O horário é obrigatório!");
                response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
                return;
            }

            // Sanitizar entrada
            horario = horario.trim();

            // Proteger contra SQL Injection e XSS
            if (!SecurityUtil.isSafeInput(horario)) {
                session.setAttribute("erro", "Entrada contém caracteres inválidos!");
                SecurityUtil.registrarAuditoria(
                    (String) session.getAttribute("usuarioEmail"), 
                    "Definir disponibilidade - Entrada suspeita", 
                    false
                );
                response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
                return;
            }

            // Validar formato do horário (HH:mm)
            if (!horario.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                session.setAttribute("erro", "Formato de horário inválido! Use HH:mm");
                response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
                return;
            }

            // Criar disponibilidade
            DisponibilidadeProfissional disponibilidade = new DisponibilidadeProfissional(
                profissionalId, 
                horario
            );
            
            if (ativoStr != null && ativoStr.equals("true")) {
                disponibilidade.setAtivo(true);
            } else {
                disponibilidade.setAtivo(false);
            }

            DisponibilidadeProfissionalDAO dao = new DisponibilidadeProfissionalDAO();
            dao.salvar(disponibilidade);

            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"), 
                "Disponibilidade adicionada: " + horario, 
                true
            );

            session.setAttribute("sucesso", "✓ Disponibilidade adicionada com sucesso!");
            response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("erro", "Erro ao definir disponibilidade!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"), 
                "Definir disponibilidade - Erro na aplicação", 
                false
            );
            response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
        }
    }
}
