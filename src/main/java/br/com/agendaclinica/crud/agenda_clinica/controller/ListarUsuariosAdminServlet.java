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
            out.println("<p style='color:#ff6b6b;text-align:center;'>Acesso Negado</p>");
            return;
        }

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
            List<Usuario> todosUsuarios = usuarioDAO.listarTodos();

            // ── SEÇÃO PACIENTES ──────────────────────────────────────────────────
            out.println("<h2 style='color:#00d4ff;margin-bottom:16px;font-size:1.3em;'>👤 Pacientes</h2>");
            out.println("<div class='cards-grid'>");

            boolean temPaciente = false;
            for (Usuario u : todosUsuarios) {
                if (u.getCategoria() != 1) continue;
                temPaciente = true;

                BigDecimal debtBruto = BigDecimal.ZERO;
                BigDecimal debtLiquido = BigDecimal.ZERO;

                List<Atendimento> atendimentosDele = atendimentoDAO.buscarPorPaciente(u.getPacienteId());
                for (Atendimento att : atendimentosDele) {
                    if (att.calcularCusto() != null) {
                        BigDecimal base = att.calcularCusto();
                        debtBruto = debtBruto.add(base);
                        debtLiquido = debtLiquido.add(base.add(base.multiply(TAXA_GERAL).setScale(2, RoundingMode.HALF_UP)));
                    }
                }

                out.println("<div class='card-item'>");
                out.println("  <div class='card-header'>");
                out.println("    <div>");
                out.println("      <h3 class='card-title'>" + u.getNome() + "</h3>");
                out.println("      <div class='card-subtitle'>ID Paciente: #" + u.getPacienteId() + "</div>");
                out.println("    </div>");
                out.println("    <span style='background:rgba(0,212,255,0.15);color:#00d4ff;font-size:0.75em;font-weight:700;padding:3px 10px;border-radius:20px;'>Paciente</span>");
                out.println("  </div>");
                out.println("  <div class='card-info'><strong>📧 Email:</strong> " + u.getEmail() + "</div>");
                out.println("  <div class='card-price'>");
                out.println("    <div><div style='font-size:0.6em;color:#ffeb3b;text-transform:uppercase;'>Fatura Bruta</div><div style='font-size:0.8em;color:#ffeb3b;'>R$ " + debtBruto + "</div></div>");
                out.println("    <div style='text-align:right;'><div style='font-size:0.6em;color:#00ff88;text-transform:uppercase;'>Fatura Líquida (+10%)</div><div>R$ " + debtLiquido + "</div></div>");
                out.println("  </div>");
                out.println("</div>");
            }
            if (!temPaciente) {
                out.println("<div class='card-item' style='justify-content:center;align-items:center;color:#aaa;min-height:120px;grid-column:1/-1;'>Nenhum paciente cadastrado.</div>");
            }
            out.println("</div>"); // fim cards-grid pacientes

            // ── SEPARADOR ────────────────────────────────────────────────────────
            out.println("<hr style='border:1px solid rgba(255,255,255,0.1);margin:30px 0;'>");

            // ── SEÇÃO MÉDICOS ─────────────────────────────────────────────────────
            out.println("<h2 style='color:#a78bfa;margin-bottom:16px;font-size:1.3em;'>🩺 Médicos / Profissionais</h2>");
            out.println("<div class='cards-grid'>");

            boolean temMedico = false;
            for (Usuario u : todosUsuarios) {
                if (u.getCategoria() != 2) continue;
                temMedico = true;

                // Atendimentos totais do médico (para info)
                List<Atendimento> atendMedico = atendimentoDAO.buscarPorProfissional(u.getProfissionalId());
                long qtdDisponiveis = atendMedico.stream().filter(a -> "Disponível".equals(a.getStatus())).count();
                long qtdAgendados   = atendMedico.stream().filter(a -> "Agendado".equals(a.getStatus())).count();
                long qtdConcluidos  = atendMedico.stream().filter(a -> "Concluído".equals(a.getStatus())).count();

                out.println("<div class='card-item' style='border-color:rgba(167,139,250,0.25);'>");
                out.println("  <div class='card-header'>");
                out.println("    <div>");
                out.println("      <h3 class='card-title' style='color:#a78bfa;'>" + u.getNome() + "</h3>");
                out.println("      <div class='card-subtitle'>ID Profissional: #" + u.getProfissionalId() + "</div>");
                out.println("    </div>");
                out.println("    <span style='background:rgba(167,139,250,0.15);color:#a78bfa;font-size:0.75em;font-weight:700;padding:3px 10px;border-radius:20px;'>Médico</span>");
                out.println("  </div>");
                out.println("  <div class='card-info'><strong>📧 Email:</strong> " + u.getEmail() + "</div>");
                out.println("  <div style='display:flex;gap:12px;margin-top:8px;'>");
                out.println("    <div style='flex:1;text-align:center;padding:8px;background:rgba(0,212,255,0.1);border-radius:8px;'>");
                out.println("      <div style='font-size:1.4em;font-weight:bold;color:#00d4ff;'>" + qtdDisponiveis + "</div>");
                out.println("      <div style='font-size:0.7em;color:#aaa;'>Disponíveis</div>");
                out.println("    </div>");
                out.println("    <div style='flex:1;text-align:center;padding:8px;background:rgba(255,202,40,0.1);border-radius:8px;'>");
                out.println("      <div style='font-size:1.4em;font-weight:bold;color:#ffca28;'>" + qtdAgendados + "</div>");
                out.println("      <div style='font-size:0.7em;color:#aaa;'>Agendados</div>");
                out.println("    </div>");
                out.println("    <div style='flex:1;text-align:center;padding:8px;background:rgba(0,255,136,0.1);border-radius:8px;'>");
                out.println("      <div style='font-size:1.4em;font-weight:bold;color:#00ff88;'>" + qtdConcluidos + "</div>");
                out.println("      <div style='font-size:0.7em;color:#aaa;'>Concluídos</div>");
                out.println("    </div>");
                out.println("  </div>");
                out.println("</div>");
            }
            if (!temMedico) {
                out.println("<div class='card-item' style='justify-content:center;align-items:center;color:#aaa;min-height:120px;grid-column:1/-1;'>Nenhum médico cadastrado.</div>");
            }
            out.println("</div>"); // fim cards-grid médicos

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<div class='erro' style='text-align:center;margin-top:20px;'>❌ Erro ao carregar usuários</div>");
        }
    }
}
