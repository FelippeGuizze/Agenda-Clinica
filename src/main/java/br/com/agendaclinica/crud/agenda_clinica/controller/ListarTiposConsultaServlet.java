package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.TipoConsultaDAO;
import br.com.agendaclinica.crud.agenda_clinica.model.TipoConsulta;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/ListarTiposConsultaServlet")
public class ListarTiposConsultaServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Verificar se é profissional
        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(2)) {
            out.println("<tr><td colspan='5' style='padding: 10px; text-align: center; color: #ff6b6b;'>Acesso negado</td></tr>");
            return;
        }

        try {
            Long profissionalId = (Long) session.getAttribute("usuarioId");
            TipoConsultaDAO dao = new TipoConsultaDAO();
            List<TipoConsulta> tiposConsulta = dao.buscarPorProfissional(profissionalId);

            if (tiposConsulta.isEmpty()) {
                out.println("<tr><td colspan='5' style='padding: 10px; text-align: center; color: #aaa;'>Nenhum tipo de consulta cadastrado</td></tr>");
                return;
            }

            for (TipoConsulta tipo : tiposConsulta) {
                String status = tipo.getAtivo() ? "<span style='color: #00ff88;'>✓ Ativo</span>" : "<span style='color: #ff6b6b;'>✗ Inativo</span>";
                String precoFormatado = "R$ " + tipo.getPreco().toString();
                String descricao = tipo.getDescricao() != null && !tipo.getDescricao().isEmpty() ?
                    tipo.getDescricao() : "Sem descrição";
                String acao = "<button onclick='removerTipoConsulta(" + tipo.getId() + ")' style='background: #ff4444; color: white; border: none; padding: 5px 10px; border-radius: 3px; cursor: pointer;'>Remover</button>";

                out.println("<tr style='border-bottom: 1px solid rgba(255, 255, 255, 0.1);'>");
                out.println("<td style='padding: 10px; font-weight: 600;'>" + tipo.getNome() + "</td>");
                out.println("<td style='padding: 10px; font-weight: 600; color: #00ff88;'>" + precoFormatado + "</td>");
                out.println("<td style='padding: 10px; max-width: 300px; word-wrap: break-word; overflow-wrap: break-word; white-space: normal;'>" + descricao + "</td>");
                out.println("<td style='padding: 10px;'>" + status + "</td>");
                out.println("<td style='padding: 10px; text-align: center;'>" + acao + "</td>");
                out.println("</tr>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<tr><td colspan='5' style='padding: 10px; text-align: center; color: #ff6b6b;'>Erro ao carregar tipos de consulta</td></tr>");
        }
    }
}