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
                
                out.println("<tr style='border-bottom: 1px solid rgba(255, 255, 255, 0.1);'>");
                out.println("<td style='padding: 12px; color:#aaa'>" + u.getPacienteId() + "</td>");
                out.println("<td style='padding: 12px; font-weight: bold;'>" + u.getNome() + "</td>");
                out.println("<td style='padding: 12px;'>" + u.getEmail() + "</td>");
                out.println("<td style='padding: 12px; color:#ffeb3b;'>R$ " + debtBruto.toString() + "</td>");
                out.println("<td style='padding: 12px; font-weight: bold; color:#00ff88;'>R$ " + debtLiquido.toString() + "</td>");
                out.println("</tr>");
            }
            
            if(!temPaciente) {
                out.println("<tr><td colspan='5' style='padding: 20px; text-align: center; color: #aaa;'>Não existem pacientes cadastrados na clínica.</td></tr>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<tr><td colspan='5' style='padding: 20px; text-align: center; color: #ff6b6b;'>Erro no processamento do Relatório Financeiro</td></tr>");
        }
    }
}
