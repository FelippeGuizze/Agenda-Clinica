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
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/ListarConsultaLivreMedicoServlet")
public class ListarConsultaLivreMedicoServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(2)) {
            out.println("<tr><td colspan='4' style='padding: 10px; text-align: center; color: #ff6b6b;'>Acesso negado</td></tr>");
            return;
        }

        try {
            Long profissionalId = (Long) session.getAttribute("profissionalId");
            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
            
            // Re-aproveitamos do DAO de buscar por profissional ou buscar dispos
            // Vamos filtrar manualmente caso o DAO traga os não "Disponível", mas 
            // usar o buscarPorProfissional e depois fazer filter
            List<Atendimento> todas = atendimentoDAO.buscarPorProfissional(profissionalId);
            
            List<Atendimento> consultasLivres = todas.stream()
                .filter(a -> "Disponível".equals(a.getStatus()))
                .collect(Collectors.toList());

            if (consultasLivres.isEmpty()) {
                out.println("<tr><td colspan='4' style='padding: 20px; text-align: center; color: #aaa;'>Você ainda não ofereceu horários, ou todos já foram agendados!</td></tr>");
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Atendimento agenda : consultasLivres) {
                String dataFormatada = agenda.getDatahora().format(formatter);
                
                // Polymorphic Call
                String precoFormatado = agenda.calcularCusto() != null ?
                    "R$ " + agenda.calcularCusto().toString() : "A combinar";

                out.println("<tr style='border-bottom: 1px solid rgba(255, 255, 255, 0.1);'>");
                out.println("<td style='padding: 12px;'>" + agenda.getClass().getSimpleName() + "</td>");
                out.println("<td style='padding: 12px; font-weight: 600;'>" + agenda.getTipo() + "</td>");
                out.println("<td style='padding: 12px;'>" + dataFormatada + "</td>");
                out.println("<td style='padding: 12px; font-weight: 600; color: #00ff88;'>" + precoFormatado + "</td>");
                out.println("</tr>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<tr><td colspan='4' style='padding: 10px; text-align: center; color: #ff6b6b;'>Erro ao carregar sua agenda</td></tr>");
        }
    }
}
