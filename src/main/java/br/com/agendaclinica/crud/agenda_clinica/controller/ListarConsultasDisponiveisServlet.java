package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.AtendimentoDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.ProfissionalDAO;
import br.com.agendaclinica.crud.agenda_clinica.model.Atendimento;
import br.com.agendaclinica.crud.agenda_clinica.model.Profissional;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/ListarConsultasDisponiveisServlet")
public class ListarConsultasDisponiveisServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Verificar se é paciente
        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(1)) {
            out.println("<tr><td colspan='6' style='padding: 10px; text-align: center; color: #ff6b6b;'>Acesso negado</td></tr>");
            return;
        }

        try {
            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
            ProfissionalDAO profissionalDAO = new ProfissionalDAO();

            // Buscar todas as consultas disponíveis (status = "Disponível")
            List<Atendimento> consultasDisponiveis = atendimentoDAO.buscarConsultasDisponiveis();

            if (consultasDisponiveis.isEmpty()) {
                out.println("<tr><td colspan='6' style='padding: 10px; text-align: center; color: #aaa;'>Nenhuma consulta disponível no momento</td></tr>");
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Atendimento consulta : consultasDisponiveis) {
                Profissional profissional = profissionalDAO.buscarPorId(consulta.getProfissional().getId());

                String dataFormatada = consulta.getDatahora().format(formatter);
                String precoFormatado = consulta.calcularCusto() != null ?
                    "R$ " + consulta.calcularCusto().toString() : "A combinar";

                String botaoAgendar = "<button type='button' onclick='agendarConsulta(" + consulta.getId() + ")' " +
                    "style='background: linear-gradient(135deg, #00d4ff 0%, #0099ff 100%); color: white; border: none; padding: 6px 12px; border-radius: 5px; cursor: pointer; font-weight: bold; display: inline-flex; align-items: center; justify-content: center; height: 35px; white-space: nowrap;'>Agendar</button>";

                out.println("<tr style='border-bottom: 1px solid rgba(255, 255, 255, 0.1);'>");
                out.println("<td style='padding: 12px;'>" + profissional.getNome() + "</td>");
                out.println("<td style='padding: 12px;'>" + profissional.getEspecialidade() + "</td>");
                out.println("<td style='padding: 12px;'>" + consulta.getTipo() + "</td>");
                out.println("<td style='padding: 12px;'>" + dataFormatada + "</td>");
                out.println("<td style='padding: 12px; font-weight: 600; color: #00ff88;'>" + precoFormatado + "</td>");
                out.println("<td style='padding: 12px; text-align: center;'>" + botaoAgendar + "</td>");
                out.println("</tr>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<tr><td colspan='6' style='padding: 10px; text-align: center; color: #ff6b6b;'>Erro ao carregar consultas</td></tr>");
        }
    }
}