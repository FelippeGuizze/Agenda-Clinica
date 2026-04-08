package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.AtendimentoDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.ProfissionalDAO;
import br.com.agendaclinica.crud.agenda_clinica.model.Atendimento;
import br.com.agendaclinica.crud.agenda_clinica.model.Profissional;
import br.com.agendaclinica.crud.agenda_clinica.model.Consulta;
import br.com.agendaclinica.crud.agenda_clinica.model.Exame;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@WebServlet("/GerarConsultaEspecificaServlet")
public class GerarConsultaEspecificaServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        if (session.getAttribute("usuarioCategoria") == null || 
            !session.getAttribute("usuarioCategoria").equals(2)) {
            session.setAttribute("erro", "Acesso restrito!");
            response.sendRedirect(request.getContextPath() + "/minha-agenda-medico.jsp");
            return;
        }

        try {
            Long profissionalId = (Long) session.getAttribute("profissionalId");
            String datahoraStr = request.getParameter("datahora");
            String tipo = request.getParameter("tipo");
            String precoStr = request.getParameter("preco");
            String orientacaoExtra = request.getParameter("orientacao");
            String classeHeranca = request.getParameter("classe_heranca"); // "Consulta" ou "Exame"

            if (datahoraStr == null || tipo == null || precoStr == null || classeHeranca == null) {
                session.setAttribute("erro", "Preencha todos os campos, incluindo a Classe do Atendimento!");
                response.sendRedirect(request.getContextPath() + "/minha-agenda-medico.jsp");
                return;
            }

            LocalDateTime dataHora = LocalDateTime.parse(datahoraStr);
            BigDecimal preco = new BigDecimal(precoStr);

            ProfissionalDAO profissionalDAO = new ProfissionalDAO();
            Profissional profissional = profissionalDAO.buscarPorId(profissionalId);

            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
            
            // Factory explícita baseada na classe de herança selecionada pelo médico
            Atendimento novoAtendimento;
            if ("Exame".equalsIgnoreCase(classeHeranca)) {
                novoAtendimento = new Exame(
                    tipo,
                    null,
                    profissional,
                    dataHora,
                    "Disponível",
                    preco,
                    null
                );
            } else {
                // Padrão: Consulta
                novoAtendimento = new Consulta(
                    tipo,
                    null,
                    profissional,
                    dataHora,
                    "Disponível",
                    preco,
                    null
                );
            }

            if (orientacaoExtra != null && !orientacaoExtra.trim().isEmpty()) {
                novoAtendimento.setOrientacaoMedico(orientacaoExtra.trim());
            }

            atendimentoDAO.salvar(novoAtendimento);

            session.setAttribute("sucesso", "Horário de [" + tipo + "] oferecido com sucesso!");
            response.sendRedirect(request.getContextPath() + "/minha-agenda-medico.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("erro", "Erro ao processar as datas do calendário ou o salvamento!");
            response.sendRedirect(request.getContextPath() + "/minha-agenda-medico.jsp");
        }
    }
}
