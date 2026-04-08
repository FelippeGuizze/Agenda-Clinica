package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.dao.DisponibilidadeProfissionalDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.TipoConsultaDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.AtendimentoDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.ProfissionalDAO;
import br.com.agendaclinica.crud.agenda_clinica.model.DisponibilidadeProfissional;
import br.com.agendaclinica.crud.agenda_clinica.model.TipoConsulta;
import br.com.agendaclinica.crud.agenda_clinica.model.Atendimento;
import br.com.agendaclinica.crud.agenda_clinica.model.Consulta;
import br.com.agendaclinica.crud.agenda_clinica.model.Exame;
import br.com.agendaclinica.crud.agenda_clinica.model.Profissional;
import br.com.agendaclinica.crud.agenda_clinica.util.SecurityUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/GerarConsultasDisponiveisServlet")
public class GerarConsultasDisponiveisServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Verificar se é profissional
        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(2)) {
            session.setAttribute("erro", "❌ Acesso negado! Apenas profissionais podem gerar consultas.");
            response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
            return;
        }

        try {
            Long profissionalId = (Long) session.getAttribute("usuarioId");

            DisponibilidadeProfissionalDAO dispDAO = new DisponibilidadeProfissionalDAO();
            TipoConsultaDAO tipoDAO = new TipoConsultaDAO();
            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
            ProfissionalDAO profissionalDAO = new ProfissionalDAO();

            // Buscar profissional
            Profissional profissional = profissionalDAO.buscarPorId(profissionalId);
            if (profissional == null) {
                session.setAttribute("erro", "Profissional não encontrado!");
                response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
                return;
            }

            // Buscar disponibilidades do profissional
            List<DisponibilidadeProfissional> disponibilidades = dispDAO.buscarPorProfissional(profissionalId);
            if (disponibilidades.isEmpty()) {
                session.setAttribute("erro", "Você não tem disponibilidades configuradas!");
                response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
                return;
            }

            // Buscar tipos de consulta do profissional
            List<TipoConsulta> tiposConsulta = tipoDAO.buscarPorProfissional(profissionalId);
            if (tiposConsulta.isEmpty()) {
                session.setAttribute("erro", "Você não tem tipos de consulta configurados!");
                response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
                return;
            }

            // Gerar consultas para as próximas 4 semanas
            LocalDate hoje = LocalDate.now();
            int consultasGeradas = 0;

            for (int semana = 0; semana < 4; semana++) {
                for (DisponibilidadeProfissional disp : disponibilidades) {
                    if (!disp.getAtivo()) continue;

                    // Calcular a data para este dia da semana nesta semana
                    LocalDate dataConsulta = calcularProximaData(hoje.plusWeeks(semana), disp.getDiaSemana());
                    LocalDateTime dataHora = LocalDateTime.of(dataConsulta.getYear(), dataConsulta.getMonth(),
                                                             dataConsulta.getDayOfMonth(),
                                                             Integer.parseInt(disp.getHorario().split(":")[0]),
                                                             Integer.parseInt(disp.getHorario().split(":")[1]));

                    // Verificar se já existe uma consulta nesta data/hora
                    if (consultaJaExiste(atendimentoDAO, profissionalId, dataHora)) {
                        continue;
                    }

                    // Criar consultas para cada tipo disponível (Polimorfismo aplicado na factory manual)
                    for (TipoConsulta tipo : tiposConsulta) {
                        if (!tipo.getAtivo()) continue;

                        Atendimento atendimentoAgendado;
                        if (tipo.getNome().toLowerCase().contains("exame")) {
                            atendimentoAgendado = new Exame(
                                tipo.getNome(),
                                null, // paciente = null (disponível)
                                profissional,
                                dataHora,
                                "Disponível",
                                tipo.getPreco(),
                                disp.getId()
                            );
                        } else {
                            atendimentoAgendado = new Consulta(
                                tipo.getNome(),
                                null,
                                profissional,
                                dataHora,
                                "Disponível",
                                tipo.getPreco(),
                                disp.getId()
                            );
                        }

                        atendimentoDAO.salvar(atendimentoAgendado);
                        consultasGeradas++;
                    }
                }
            }

            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Geradas " + consultasGeradas + " consultas disponíveis",
                true
            );

            session.setAttribute("sucesso", "✓ " + consultasGeradas + " consultas disponíveis geradas com sucesso!");
            response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("erro", "Erro ao gerar consultas!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"),
                "Erro ao gerar consultas disponíveis",
                false
            );
            response.sendRedirect(request.getContextPath() + "/disponibilidade-profissional.jsp");
        }
    }

    private LocalDate calcularProximaData(LocalDate dataBase, String diaSemana) {
        DayOfWeek targetDay = switch (diaSemana) {
            case "SEGUNDA" -> DayOfWeek.MONDAY;
            case "TERÇA" -> DayOfWeek.TUESDAY;
            case "QUARTA" -> DayOfWeek.WEDNESDAY;
            case "QUINTA" -> DayOfWeek.THURSDAY;
            case "SEXTA" -> DayOfWeek.FRIDAY;
            case "SABADO" -> DayOfWeek.SATURDAY;
            case "DOMINGO" -> DayOfWeek.SUNDAY;
            default -> DayOfWeek.MONDAY;
        };

        LocalDate data = dataBase;
        while (data.getDayOfWeek() != targetDay) {
            data = data.plusDays(1);
        }
        return data;
    }

    private boolean consultaJaExiste(AtendimentoDAO dao, Long profissionalId, LocalDateTime dataHora) {
        // Esta é uma verificação simplificada - em produção seria melhor ter uma query específica
        List<Atendimento> existentes = dao.buscarPorProfissional(profissionalId);
        return existentes.stream().anyMatch(a ->
            a.getDatahora().equals(dataHora) && !"Cancelado".equals(a.getStatus())
        );
    }
}