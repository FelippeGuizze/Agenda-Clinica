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

            java.math.BigDecimal totalAcumulado = java.math.BigDecimal.ZERO;

            StringBuilder htmlCards = new StringBuilder("<div class='cards-grid'>");

            for (Atendimento consulta : minhasConsultas) {
                String dataFormatada = consulta.getDatahora().format(formatter);
                
                String precoFormatado;
                java.math.BigDecimal valorFinalDaConsulta = java.math.BigDecimal.ZERO;

                if (consulta.getPrecoFinal() != null) {
                    valorFinalDaConsulta = consulta.getPrecoFinal();
                    precoFormatado = "R$ " + valorFinalDaConsulta.toString() + 
                                     " <br><span style='font-size: 0.7em; color: #aaa;'>(Base: " + consulta.calcularCusto() + " + Taxa: " + consulta.getValorTaxa() + ")</span>";
                } else {
                    if (consulta.calcularCusto() != null) {
                        valorFinalDaConsulta = consulta.calcularCusto();
                    }
                    precoFormatado = "R$ " + valorFinalDaConsulta.toString();
                }

                if (!"Cancelado".equals(consulta.getStatus())) {
                    totalAcumulado = totalAcumulado.add(valorFinalDaConsulta);
                }

                String orientacoes = consulta.gerarOrientacoes();
                if (consulta.getOrientacaoMedico() != null && !consulta.getOrientacaoMedico().isEmpty()) {
                    orientacoes += " <br><span style='color:#ffffff; font-weight:bold;'>Dr.: </span><span style='color:#ffffff;'>" + consulta.getOrientacaoMedico() + "</span>";
                }

                String statusColor;
                switch (consulta.getStatus()) {
                    case "Agendado" -> statusColor = "#00ff88";
                    case "Em progresso" -> statusColor = "#ffa500";
                    case "Concluído" -> statusColor = "#00d4ff";
                    case "Cancelado" -> statusColor = "#ff6b6b";
                    default -> statusColor = "#aaa";
                }

                htmlCards.append("<div class='card-item'>");
                htmlCards.append("<div class='card-header'>");
                htmlCards.append("<div>");
                htmlCards.append("<h3 class='card-title'>").append(consulta.getTipo()).append("</h3>");
                htmlCards.append("<div class='card-subtitle'>").append(consulta.getProfissional().getNome()).append(" (").append(consulta.getProfissional().getEspecialidade()).append(")</div>");
                htmlCards.append("</div>");
                htmlCards.append("<span style='color: ").append(statusColor).append("; font-weight: bold; border: 1px solid ").append(statusColor).append("; padding: 4px 8px; border-radius: 20px; font-size: 0.8em;'>").append(consulta.getStatus()).append("</span>");
                htmlCards.append("</div>");
                
                htmlCards.append("<div class='card-info'><strong>📅 Data:</strong> ").append(dataFormatada).append("</div>");
                htmlCards.append("<div class='card-info' style='color: #ffeb3b; font-size: 0.85em;'><strong>⚠️ Orientações:</strong> ").append(orientacoes).append("</div>");

                htmlCards.append("<div class='card-price'>");
                htmlCards.append("<div>").append(precoFormatado).append("</div>");
                
                if (consulta.getStatus().equals("Agendado")) {
                    htmlCards.append("<form method='POST' action='").append(request.getContextPath()).append("/CancelarAtendimentoServlet' style='margin:0;'>");
                    htmlCards.append("<input type='hidden' name='atendimentoId' value='").append(consulta.getId()).append("'/>");
                    htmlCards.append("<button type='submit' style='background: #ff4444; color: white; border: none; padding: 6px 12px; border-radius: 4px; cursor: pointer; font-weight: bold; font-size: 0.8em;'>Desmarcar</button>");
                    htmlCards.append("</form>");
                }
                
                htmlCards.append("</div>"); // card-price
                htmlCards.append("</div>"); // card-item
            }
            htmlCards.append("</div>"); // end cards-grid

            // Div do Totalizador
            htmlCards.append("<div class='card-totalizador'>");
            htmlCards.append("<h2 style='margin: 0; font-size: 1.8em; color: #00ff88;'>Custo Total Acumulado: R$ ").append(totalAcumulado.toString()).append("</h2>");
            htmlCards.append("</div>");

            out.println(htmlCards.toString());

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<div class='erro' style='text-align: center; margin-top: 20px;'>❌ Erro ao carregar suas consultas</div>");
        }
    }
}