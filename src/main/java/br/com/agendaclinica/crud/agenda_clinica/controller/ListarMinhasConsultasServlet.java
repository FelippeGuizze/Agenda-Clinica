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

@WebServlet("/ListarMinhasConsultasServlet")
public class ListarMinhasConsultasServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Verificar se é paciente
        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(1)) {
            out.println("<tr><td colspan='7' style='padding: 10px; text-align: center; color: #ff6b6b;'>Acesso negado</td></tr>");
            return;
        }

        try {
            Long pacienteId = (Long) session.getAttribute("pacienteId");
            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
            List<Atendimento> minhasConsultas = atendimentoDAO.buscarPorPaciente(pacienteId);

            if (minhasConsultas.isEmpty()) {
                out.println("<tr><td colspan='7' style='padding: 20px; text-align: center; color: #aaa;'>Você ainda não tem consultas agendadas</td></tr>");
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // DEMONSTRAÇÃO EXIGIDA PELO PROFESSOR (Polimorfismo numa coleção usando métodos overridden)
            System.out.println("====== DEMONSTRAÇÃO DE POLIMORFISMO ======");
            for (Atendimento att : minhasConsultas) {
                System.out.println("Atendimento ID: " + att.getId() + " | Tipo Obj: " + att.getClass().getSimpleName());
                System.out.println("- Custo Calculado: R$ " + att.calcularCusto());
                System.out.println("- Orientações: " + att.gerarOrientacoes());
            }
            System.out.println("==========================================");

            for (Atendimento consulta : minhasConsultas) {
                String dataFormatada = consulta.getDatahora().format(formatter);
                
                // Agora pegamos o valor da Regra de Negócio Polimórfica:
                String precoFormatado = "R$ " + consulta.calcularCusto().toString();
                String orientacoes = consulta.gerarOrientacoes();
                if (consulta.getOrientacaoMedico() != null && !consulta.getOrientacaoMedico().isEmpty()) {
                    orientacoes += " <br><span style='color:#ffffff; font-weight:bold;'>Observações do Dr.: </span><span style='color:#ffffff;'>" + consulta.getOrientacaoMedico() + "</span>";
                }

                String statusColor;
                switch (consulta.getStatus()) {
                    case "Agendado" -> statusColor = "#00ff88"; // Verde
                    case "Em progresso" -> statusColor = "#ffa500"; // Laranja
                    case "Concluído" -> statusColor = "#00d4ff"; // Azul
                    case "Cancelado" -> statusColor = "#ff6b6b"; // Vermelho
                    default -> statusColor = "#aaa"; // Cinza
                }

                String statusHtml = "<span style='color: " + statusColor + "; font-weight: 600;'>" + consulta.getStatus() + "</span>";

                out.println("<tr style='border-bottom: 1px solid rgba(255, 255, 255, 0.1);'>");
                out.println("<td style='padding: 12px;'>" + consulta.getProfissional().getNome() + "</td>");
                out.println("<td style='padding: 12px;'>" + consulta.getProfissional().getEspecialidade() + "</td>");
                out.println("<td style='padding: 12px;'>" + consulta.getTipo() + "</td>");
                out.println("<td style='padding: 12px;'>" + dataFormatada + "</td>");
                out.println("<td style='padding: 12px; font-weight: 600; color: #00ff88;'>" + precoFormatado + "</td>");
                out.println("<td style='padding: 12px;'>" + statusHtml + "</td>");
                out.println("<td style='padding: 12px; font-size: 0.9em; color: #ffeb3b;'>" + orientacoes + "</td>");
                
                // COLUNA AÇÕES E CANCELAR
                out.println("<td style='padding: 12px; text-align: center;'>");
                if (consulta.getStatus().equals("Agendado")) {
                    out.println("<form method='POST' action='" + request.getContextPath() + "/CancelarAtendimentoServlet' style='margin:0;'>");
                    out.println("<input type='hidden' name='atendimentoId' value='" + consulta.getId() + "'/>");
                    out.println("<button type='submit' style='background: #ff4444; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-weight: bold;'>Desmarcar</button>");
                    out.println("</form>");
                }
                out.println("</td>");
                
                out.println("</tr>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<tr><td colspan='7' style='padding: 10px; text-align: center; color: #ff6b6b;'>Erro ao carregar suas consultas</td></tr>");
        }
    }
}