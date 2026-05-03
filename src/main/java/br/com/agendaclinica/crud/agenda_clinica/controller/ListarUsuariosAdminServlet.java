package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.UsuarioDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.AtendimentoDAO;
import br.com.agendaclinica.crud.agenda_clinica.model.Usuario;
import br.com.agendaclinica.crud.agenda_clinica.model.Atendimento;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@WebServlet("/ListarUsuariosAdminServlet")
public class ListarUsuariosAdminServlet extends HttpServlet {

    private static final BigDecimal TAXA_GERAL = new BigDecimal("0.10");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (session.getAttribute("usuarioCategoria") == null || 
            !session.getAttribute("usuarioCategoria").equals(0)) {
            out.println("<tr><td colspan='5'>Acesso Negado (Não-Administrador)</td></tr>");
            return;
        }

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
            
            // Puxamos todo mundo
            List<Usuario> todosUsuarios = usuarioDAO.listarTodos();
            
            boolean temPaciente = false;
            
            StringBuilder htmlCards = new StringBuilder("<div class='cards-grid'>");

            for (Usuario u : todosUsuarios) {
                // SÓ QUEREMOS PACIENTES (categoria 1)
                if (u.getCategoria() != 1) continue;
                temPaciente = true;
                
                BigDecimal debtBruto = BigDecimal.ZERO;
                BigDecimal debtLiquido = BigDecimal.ZERO;
                
                // Buscar custos via Polimorfismo Original de cada consulta dele
                List<Atendimento> atendimentosDele = atendimentoDAO.buscarPorPaciente(u.getPacienteId());
                for (Atendimento att : atendimentosDele) {
                    if (att.calcularCusto() != null) {
                        BigDecimal base = att.calcularCusto();
                        debtBruto = debtBruto.add(base);
                        BigDecimal comTaxa = base.add(base.multiply(TAXA_GERAL).setScale(2, RoundingMode.HALF_UP));
                        debtLiquido = debtLiquido.add(comTaxa);
                    }
                }
                
                htmlCards.append("<div class='card-item'>");
                htmlCards.append("<div class='card-header'>");
                htmlCards.append("<div>");
                htmlCards.append("<h3 class='card-title'>").append(u.getNome()).append("</h3>");
                htmlCards.append("<div class='card-subtitle'>ID Paciente: #").append(u.getPacienteId()).append("</div>");
                htmlCards.append("</div>");
                htmlCards.append("</div>");
                
                htmlCards.append("<div class='card-info'><strong>📧 Email:</strong> ").append(u.getEmail()).append("</div>");
                
                htmlCards.append("<div class='card-price'>");
                htmlCards.append("<div>");
                htmlCards.append("<div style='font-size: 0.6em; color: #ffeb3b; text-transform: uppercase;'>Fatura Bruta</div>");
                htmlCards.append("<div style='font-size: 0.8em; color: #ffeb3b;'>R$ ").append(debtBruto.toString()).append("</div>");
                htmlCards.append("</div>");
                
                htmlCards.append("<div style='text-align: right;'>");
                htmlCards.append("<div style='font-size: 0.6em; color: #00ff88; text-transform: uppercase;'>Fatura Líquida (+10%)</div>");
                htmlCards.append("<div>R$ ").append(debtLiquido.toString()).append("</div>");
                htmlCards.append("</div>");
                htmlCards.append("</div>"); // card-price
                
                htmlCards.append("</div>"); // card-item
            }
            
            htmlCards.append("</div>"); // cards-grid
            
            if(!temPaciente) {
                out.println("<div class='cards-grid'><div class='card-item' style='justify-content: center; align-items: center; color: #aaa; min-height: 200px; grid-column: 1 / -1;'>Não existem pacientes cadastrados na clínica.</div></div>");
            } else {
                out.println(htmlCards.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<div class='erro' style='text-align: center; margin-top: 20px;'>❌ Erro no processamento do Relatório Financeiro</div>");
        }
    }
}
