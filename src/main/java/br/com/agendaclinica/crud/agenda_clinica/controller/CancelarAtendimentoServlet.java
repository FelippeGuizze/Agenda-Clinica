package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.AtendimentoDAO;
import br.com.agendaclinica.crud.agenda_clinica.model.Atendimento;
import java.io.IOException;

@WebServlet("/CancelarAtendimentoServlet")
public class CancelarAtendimentoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        if (session.getAttribute("usuarioCategoria") == null || 
            !session.getAttribute("usuarioCategoria").equals(1)) {
            session.setAttribute("erro", "Acesso restrito!");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            Long pacienteId = (Long) session.getAttribute("pacienteId");
            String atendimentoIdStr = request.getParameter("atendimentoId");
            
            if (atendimentoIdStr == null || atendimentoIdStr.trim().isEmpty()) {
                session.setAttribute("erro", "ID do atendimento não fornecido.");
                response.sendRedirect(request.getContextPath() + "/minhas-consultas.jsp");
                return;
            }
            
            Long atendimentoId = Long.parseLong(atendimentoIdStr);
            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
            Atendimento atendimento = atendimentoDAO.buscarPorId(atendimentoId);
            
            if (atendimento == null) {
                session.setAttribute("erro", "Atendimento não encontrado.");
                response.sendRedirect(request.getContextPath() + "/minhas-consultas.jsp");
                return;
            }
            
            // Valida se o agendamento pertence mesmo ao cara
            if (atendimento.getPaciente() == null || !atendimento.getPaciente().getId().equals(pacienteId)) {
                session.setAttribute("erro", "Você não tem permissão para cancelar este agendamento.");
                response.sendRedirect(request.getContextPath() + "/minhas-consultas.jsp");
                return;
            }
            
            // Cancela desvinculando paciten e liberando a vaga no mural
            atendimento.setPaciente(null);
            atendimento.setStatus("Disponível");
            
            atendimentoDAO.atualizar(atendimento);
            
            session.setAttribute("sucesso", "Consulta desmarcada com sucesso. O horário voltou a ficar disponível!");
            response.sendRedirect(request.getContextPath() + "/minhas-consultas.jsp");
            
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("erro", "Erro ao cancelar o agendamento.");
            response.sendRedirect(request.getContextPath() + "/minhas-consultas.jsp");
        }
    }
}
