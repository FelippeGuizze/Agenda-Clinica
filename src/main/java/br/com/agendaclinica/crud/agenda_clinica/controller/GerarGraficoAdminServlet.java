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

@WebServlet("/GerarGraficoAdminServlet")
public class GerarGraficoAdminServlet extends HttpServlet {

    private static final BigDecimal TAXA_GERAL = new BigDecimal("0.10");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (session.getAttribute("usuarioCategoria") == null || 
            !session.getAttribute("usuarioCategoria").equals(0)) {
            out.print("{\"erro\": \"Acesso Negado\"}");
            return;
        }

        try {
            int anoAlvo = java.time.LocalDate.now().getYear(); // Default
            String anoParam = request.getParameter("ano");
            if (anoParam != null && !anoParam.isEmpty()) {
                anoAlvo = Integer.parseInt(anoParam);
            }

            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
            List<Atendimento> todosAtendimentos = atendimentoDAO.listarTodos(); 
            
            // Descobrir quais anos possuem faturamento para popular o Select no front
            java.util.Set<Integer> anosDisponiveisSet = new java.util.TreeSet<>(java.util.Collections.reverseOrder());

            // Inicializar array de 12 meses
            BigDecimal[] faturamentoLiquido = new BigDecimal[12];
            BigDecimal[] faturamentoBruto = new BigDecimal[12];
            for(int i=0; i<12; i++) {
                faturamentoLiquido[i] = BigDecimal.ZERO;
                faturamentoBruto[i] = BigDecimal.ZERO;
            }

            BigDecimal totalAnoLiquido = BigDecimal.ZERO;
            BigDecimal totalAnoBruto = BigDecimal.ZERO;

            for (Atendimento att : todosAtendimentos) {
                // Salvar o ano pro dropdown
                if (att.getPaciente() != null && !att.getStatus().equalsIgnoreCase("Cancelado")) {
                    anosDisponiveisSet.add(att.getDatahora().getYear());
                }

                // Filtra pelo ano
                if (att.getDatahora().getYear() == anoAlvo) {
                    if (att.getPaciente() != null && !att.getStatus().equalsIgnoreCase("Cancelado")) {
                        
                        BigDecimal base = att.calcularCusto();
                        if (base != null) {
                            BigDecimal valorBruto = base;
                            BigDecimal valorLiquido = valorBruto.add(valorBruto.multiply(TAXA_GERAL).setScale(2, RoundingMode.HALF_UP));
                            
                            int mesZeroIndexed = att.getDatahora().getMonthValue() - 1; // 0 = Jan
                            faturamentoLiquido[mesZeroIndexed] = faturamentoLiquido[mesZeroIndexed].add(valorLiquido);
                            faturamentoBruto[mesZeroIndexed] = faturamentoBruto[mesZeroIndexed].add(valorBruto);
                            
                            totalAnoLiquido = totalAnoLiquido.add(valorLiquido);
                            totalAnoBruto = totalAnoBruto.add(valorBruto);
                        }
                    }
                }
            }
            
            if (anosDisponiveisSet.isEmpty()) {
                anosDisponiveisSet.add(java.time.LocalDate.now().getYear());
            }

            com.google.gson.JsonObject root = new com.google.gson.JsonObject();
            
            com.google.gson.JsonArray anos = new com.google.gson.JsonArray();
            for(Integer ano : anosDisponiveisSet) anos.add(ano);
            root.add("anosDisponiveis", anos);
            
            com.google.gson.JsonArray meses = new com.google.gson.JsonArray();
            String[] nomesMeses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
            for(String m : nomesMeses) meses.add(m);
            root.add("meses", meses);
            
            com.google.gson.JsonArray liquidos = new com.google.gson.JsonArray();
            for(int i=0; i<12; i++) liquidos.add(faturamentoLiquido[i]);
            root.add("valoresLiquidos", liquidos);

            com.google.gson.JsonArray brutos = new com.google.gson.JsonArray();
            for(int i=0; i<12; i++) brutos.add(faturamentoBruto[i]);
            root.add("valoresBrutos", brutos);
            
            root.addProperty("totalLiquido", totalAnoLiquido);
            root.addProperty("totalBruto", totalAnoBruto);

            out.print(new com.google.gson.Gson().toJson(root));

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"erro\": \"Falha ao gerar matematicas.\"}");
        }
    }
}
