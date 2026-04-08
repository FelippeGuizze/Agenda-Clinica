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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@WebServlet("/ListarCustosTotaisServlet")
public class ListarCustosTotaisServlet extends HttpServlet {

    private static final BigDecimal TAXA_GERAL = new BigDecimal("0.10");

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        response.setContentType("application/json;charset=UTF-8"); // Vamos devolver JSON desta vez
        PrintWriter out = response.getWriter();

        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(1)) {
            out.print("{\"erro\": \"Acesso negado. Apenas pacientes.\"}");
            return;
        }

        try {
            Long pacienteId = (Long) session.getAttribute("pacienteId");
            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
            List<Atendimento> minhasConsultas = atendimentoDAO.buscarPorPaciente(pacienteId);

            if (minhasConsultas.isEmpty()) {
                out.print("{\"itens\": [], \"totalStr\": \"0,00\"}");
                return;
            }

            BigDecimal totalAcumulado = BigDecimal.ZERO;
            StringBuilder json = new StringBuilder();
            json.append("{\"itens\": [");

            for (int i = 0; i < minhasConsultas.size(); i++) {
                Atendimento att = minhasConsultas.get(i);

                // ================= POLIMORFISMO ORIGINAL =================
                BigDecimal custoBase = att.calcularCusto();
                String orientacoes = att.gerarOrientacoes();
                
                if (att.getOrientacaoMedico() != null && !att.getOrientacaoMedico().isEmpty()) {
                    orientacoes += " <br><span style='color:#ffffff; font-weight:bold;'>Observações do Dr.: </span><span style='color:#ffffff;'>" + att.getOrientacaoMedico() + "</span>";
                }
                
                // Tratar escapes para JSOn string
                orientacoes = orientacoes.replace("\"", "\\\"");

                // ================= REGRA DE NEGÓCIOS NOVA (TAXA 10%) =================
                // Taxa aplicada sempre em cima do valor base (se não for nulo/0)
                if (custoBase == null) custoBase = BigDecimal.ZERO;
                
                BigDecimal valorDaTaxa = custoBase.multiply(TAXA_GERAL).setScale(2, RoundingMode.HALF_UP);
                BigDecimal valorFinal = custoBase.add(valorDaTaxa).setScale(2, RoundingMode.HALF_UP);

                totalAcumulado = totalAcumulado.add(valorFinal);

                String extrato = String.format("Custo OOP: R$ %s <br>+ Taxa Geral (10%%): R$ %s <br><b>= Valor Final: R$ %s</b>", 
                                        custoBase.toString(), valorDaTaxa.toString(), valorFinal.toString());

                String profissionalNome = att.getProfissional().getNome() + " (" + att.getProfissional().getEspecialidade() + ")";
                String classeInstanciada = att.getClass().getSimpleName();

                json.append("{");
                json.append("\"profissional\": \"").append(profissionalNome).append("\", ");
                json.append("\"classe\": \"").append(classeInstanciada).append("\", ");
                json.append("\"extrato\": \"").append(extrato).append("\", ");
                json.append("\"orientacoes\": \"").append(orientacoes).append("\"");
                json.append("}");

                if (i < minhasConsultas.size() - 1) {
                    json.append(",");
                }
            }

            json.append("], \"totalStr\": \"");
            json.append(totalAcumulado.toString());
            json.append("\"}");

            out.print(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"erro\": \"Erro ao processar a matemática.\"}");
        }
    }
}
