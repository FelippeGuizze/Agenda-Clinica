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

            // Nicho lido da sessão (definido pelo admin no CRM autorizado) — não do POST
            String classeHeranca = (String) session.getAttribute("profissionalNicho");
            if (classeHeranca == null || classeHeranca.isEmpty()) {
                classeHeranca = "Consulta"; // fallback seguro
            }

            // Múltiplas datas/horários (array enviado pelo formulário dinâmico)
            String[] datahoraValues = request.getParameterValues("datahora");
            String tipo = request.getParameter("tipo");
            String precoStr = request.getParameter("preco");
            String orientacaoExtra = request.getParameter("orientacao");

            if (datahoraValues == null || datahoraValues.length == 0 || tipo == null || precoStr == null) {
                session.setAttribute("erro", "Preencha todos os campos obrigatórios!");
                response.sendRedirect(request.getContextPath() + "/minha-agenda-medico.jsp");
                return;
            }

            BigDecimal preco = new BigDecimal(precoStr);

            ProfissionalDAO profissionalDAO = new ProfissionalDAO();
            Profissional profissional = profissionalDAO.buscarPorId(profissionalId);

            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();

            int criados = 0;
            int ignorados = 0;

            for (String datahoraStr : datahoraValues) {
                if (datahoraStr == null || datahoraStr.trim().isEmpty()) {
                    ignorados++;
                    continue;
                }

                try {
                    LocalDateTime dataHora = LocalDateTime.parse(datahoraStr.trim());

                    // Factory baseada no nicho da sessão (seguro contra manipulação de POST)
                    Atendimento novoAtendimento;
                    if ("Exame".equalsIgnoreCase(classeHeranca)) {
                        novoAtendimento = new Exame(tipo, null, profissional, dataHora, "Disponível", preco, null);
                    } else {
                        novoAtendimento = new Consulta(tipo, null, profissional, dataHora, "Disponível", preco, null);
                    }

                    if (orientacaoExtra != null && !orientacaoExtra.trim().isEmpty()) {
                        novoAtendimento.setOrientacaoMedico(orientacaoExtra.trim());
                    }

                    atendimentoDAO.salvar(novoAtendimento);
                    criados++;

                } catch (Exception ex) {
                    ignorados++;
                    System.err.println("Horário inválido ignorado: " + datahoraStr + " — " + ex.getMessage());
                }
            }

            if (criados > 0) {
                String msg = criados == 1
                    ? "Horário de [" + tipo + "] criado com sucesso!"
                    : criados + " horários de [" + tipo + "] criados com sucesso!";
                if (ignorados > 0) msg += " (" + ignorados + " ignorado(s) por data inválida)";
                session.setAttribute("sucesso", msg);
            } else {
                session.setAttribute("erro", "Nenhum horário válido foi criado. Verifique as datas informadas.");
            }

            response.sendRedirect(request.getContextPath() + "/minha-agenda-medico.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("erro", "Erro ao processar as datas do calendário ou o salvamento!");
            response.sendRedirect(request.getContextPath() + "/minha-agenda-medico.jsp");
        }
    }
}
