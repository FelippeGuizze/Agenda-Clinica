package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.TipoConsultaDAO;
import br.com.agendaclinica.crud.agenda_clinica.model.TipoConsulta;
import br.com.agendaclinica.crud.agenda_clinica.util.SecurityUtil;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/AdicionarTipoConsultaServlet")
public class AdicionarTipoConsultaServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Verificar se é profissional
        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(2)) {
            session.setAttribute("erro", "❌ Acesso negado! Apenas profissionais podem definir preços.");
            response.sendRedirect(request.getContextPath() + "/tipos-consulta.jsp");
            return;
        }

        try {
            String nome = request.getParameter("nome");
            String precoStr = request.getParameter("preco");
            String descricao = request.getParameter("descricao");
            Long profissionalId = (Long) session.getAttribute("usuarioId");

            // Validações
            if (nome == null || nome.trim().isEmpty()) {
                session.setAttribute("erro", "Nome do tipo de consulta é obrigatório!");
                response.sendRedirect(request.getContextPath() + "/tipos-consulta.jsp");
                return;
            }

            if (precoStr == null || precoStr.trim().isEmpty()) {
                session.setAttribute("erro", "Preço é obrigatório!");
                response.sendRedirect(request.getContextPath() + "/tipos-consulta.jsp");
                return;
            }

            // Sanitizar entrada
            nome = SecurityUtil.escaparXSS(nome.trim());
            descricao = descricao != null ? SecurityUtil.escaparXSS(descricao.trim()) : "";

            // Proteger contra SQL Injection
            if (!SecurityUtil.isSafeInput(nome) || !SecurityUtil.isSafeInput(descricao)) {
                session.setAttribute("erro", "Entrada contém caracteres inválidos!");
                SecurityUtil.registrarAuditoria(
                    (String) session.getAttribute("usuarioEmail"),
                    "Tentativa de adicionar tipo de consulta com entrada suspeita",
                    false
                );
                response.sendRedirect(request.getContextPath() + "/tipos-consulta.jsp");
                return;
            }

            BigDecimal preco = new BigDecimal(precoStr);
            if (preco.compareTo(BigDecimal.ZERO) <= 0) {
                session.setAttribute("erro", "Preço deve ser maior que zero!");
                response.sendRedirect(request.getContextPath() + "/tipos-consulta.jsp");
                return;
            }

            // Criar tipo de consulta
            TipoConsulta tipoConsulta = new TipoConsulta(profissionalId, nome, preco, descricao);
            TipoConsultaDAO dao = new TipoConsultaDAO();
            dao.salvar(tipoConsulta);

            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Tipo de consulta adicionado: " + nome + " - R$ " + preco,
                true
            );

            session.setAttribute("sucesso", "✓ Tipo de consulta adicionado com sucesso!");
            response.sendRedirect(request.getContextPath() + "/tipos-consulta.jsp");

        } catch (NumberFormatException e) {
            session.setAttribute("erro", "Preço inválido!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Tentativa de adicionar tipo de consulta com preço inválido",
                false
            );
            response.sendRedirect(request.getContextPath() + "/tipos-consulta.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("erro", "Erro ao adicionar tipo de consulta!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Erro ao adicionar tipo de consulta",
                false
            );
            response.sendRedirect(request.getContextPath() + "/tipos-consulta.jsp");
        }
    }
}