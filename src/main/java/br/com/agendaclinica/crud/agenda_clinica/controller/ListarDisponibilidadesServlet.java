package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.DisponibilidadeProfissionalDAO;
import br.com.agendaclinica.crud.agenda_clinica.model.DisponibilidadeProfissional;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/ListarDisponibilidadesServlet")
public class ListarDisponibilidadesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Verificar se é profissional
        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(2)) {
            out.println("<tr><td colspan='4' style='padding: 10px; text-align: center; color: #ff6b6b;'>Acesso negado</td></tr>");
            return;
        }

        try {
            Long profissionalId = (Long) session.getAttribute("usuarioId");
            DisponibilidadeProfissionalDAO dao = new DisponibilidadeProfissionalDAO();
            List<DisponibilidadeProfissional> disponibilidades = dao.buscarPorProfissional(profissionalId);

            if (disponibilidades.isEmpty()) {
                out.println("<tr><td colspan='4' style='padding: 10px; text-align: center; color: #aaa;'>Nenhuma disponibilidade cadastrada</td></tr>");
                return;
            }

            for (DisponibilidadeProfissional disp : disponibilidades) {
                String diaFormatado = formatarDia(disp.getDiaSemana());
                String status = disp.getAtivo() ? "<span style='color: #00ff88;'>✓ Disponível</span>" : "<span style='color: #ff6b6b;'>✗ Indisponível</span>";
                String acao = "<button onclick='removerDisponibilidade(" + disp.getId() + ")' style='background: #ff4444; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;'>Remover</button>";

                out.println("<tr style='border-bottom: 1px solid rgba(255, 255, 255, 0.1);'>");
                out.println("<td style='padding: 10px;'>" + diaFormatado + "</td>");
                out.println("<td style='padding: 10px;'>" + disp.getHorario() + "</td>");
                out.println("<td style='padding: 10px;'>" + status + "</td>");
                out.println("<td style='padding: 10px; text-align: center;'>" + acao + "</td>");
                out.println("</tr>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<tr><td colspan='4' style='padding: 10px; text-align: center; color: #ff6b6b;'>Erro ao carregar disponibilidades</td></tr>");
        }
    }

    private String formatarDia(String dia) {
        return switch (dia) {
            case "SEGUNDA" -> "Segunda-feira";
            case "TERÇA" -> "Terça-feira";
            case "QUARTA" -> "Quarta-feira";
            case "QUINTA" -> "Quinta-feira";
            case "SEXTA" -> "Sexta-feira";
            case "SABADO" -> "Sábado";
            case "DOMINGO" -> "Domingo";
            default -> dia;
        };
    }
}