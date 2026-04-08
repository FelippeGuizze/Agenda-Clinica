package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.AtendimentoDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.PacienteDAO;
import br.com.agendaclinica.crud.agenda_clinica.model.Atendimento;
import br.com.agendaclinica.crud.agenda_clinica.model.Paciente;
import br.com.agendaclinica.crud.agenda_clinica.util.SecurityUtil;
import java.io.IOException;

@WebServlet("/AgendarConsultaEspecificaServlet")
public class AgendarConsultaEspecificaServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Verificar se é paciente
        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(1)) {
            session.setAttribute("erro", "❌ Acesso negado! Apenas pacientes podem agendar consultas.");
            response.sendRedirect(request.getContextPath() + "/dashboard-paciente.jsp");
            return;
        }

        try {
            String atendimentoIdStr = request.getParameter("atendimentoId");
            if (atendimentoIdStr == null || atendimentoIdStr.trim().isEmpty()) {
                session.setAttribute("erro", "ID da consulta não informado!");
                response.sendRedirect(request.getContextPath() + "/dashboard-paciente.jsp");
                return;
            }

            Long atendimentoId = Long.parseLong(atendimentoIdStr);
            Long pacienteId = (Long) session.getAttribute("pacienteId");

            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
            PacienteDAO pacienteDAO = new PacienteDAO();

            // Buscar a consulta
            Atendimento consulta = atendimentoDAO.buscarPorId(atendimentoId);
            if (consulta == null) {
                session.setAttribute("erro", "Consulta não encontrada!");
                response.sendRedirect(request.getContextPath() + "/dashboard-paciente.jsp");
                return;
            }

            // Verificar se ainda está disponível
            if (!"Disponível".equals(consulta.getStatus())) {
                session.setAttribute("erro", "Esta consulta não está mais disponível!");
                response.sendRedirect(request.getContextPath() + "/dashboard-paciente.jsp");
                return;
            }

            // Buscar paciente
            Paciente paciente = pacienteDAO.buscarPorId(pacienteId);
            if (paciente == null) {
                session.setAttribute("erro", "Dados do paciente não encontrados!");
                response.sendRedirect(request.getContextPath() + "/dashboard-paciente.jsp");
                return;
            }

            // Agendar a consulta
            consulta.setPaciente(paciente);
            consulta.setStatus("Agendado");

            atendimentoDAO.atualizar(consulta);

            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Consulta agendada: " + consulta.getTipo() + " - " + consulta.getDatahora(),
                true
            );

            session.setAttribute("sucesso", "Consulta agendada com sucesso!");
            response.sendRedirect(request.getContextPath() + "/dashboard-paciente.jsp");

        } catch (NumberFormatException e) {
            session.setAttribute("erro", "ID inválido!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Tentativa de agendar consulta com ID inválido",
                false
            );
            response.sendRedirect(request.getContextPath() + "/dashboard-paciente.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("erro", "Erro ao agendar consulta!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Erro ao agendar consulta",
                false
            );
            response.sendRedirect(request.getContextPath() + "/dashboard-paciente.jsp");
        }
    }
}