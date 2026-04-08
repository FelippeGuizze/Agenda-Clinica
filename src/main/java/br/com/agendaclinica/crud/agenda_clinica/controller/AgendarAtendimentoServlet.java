package br.com.agendaclinica.crud.agenda_clinica.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import br.com.agendaclinica.crud.agenda_clinica.model.Atendimento;
import br.com.agendaclinica.crud.agenda_clinica.model.Consulta;
import br.com.agendaclinica.crud.agenda_clinica.model.Exame;
import br.com.agendaclinica.crud.agenda_clinica.model.Paciente;
import br.com.agendaclinica.crud.agenda_clinica.model.Profissional;
import br.com.agendaclinica.crud.agenda_clinica.dao.AtendimentoDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.PacienteDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.ProfissionalDAO;
import br.com.agendaclinica.crud.agenda_clinica.util.SecurityUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.time.LocalDate;

@WebServlet("/AgendarAtendimentoServlet")
public class AgendarAtendimentoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Verificar se é paciente
        if (session.getAttribute("usuarioCategoria") == null || 
            !session.getAttribute("usuarioCategoria").equals(1)) {
            session.setAttribute("erro", "❌ Acesso restrito! Apenas pacientes podem agendar.");
            response.sendRedirect(request.getContextPath() + "/agendar-atendimento.jsp");
            return;
        }

        try {
            Long profissionalId = Long.parseLong(request.getParameter("profissionalId"));
            String tipo = request.getParameter("tipo");
            String dia = request.getParameter("dia");
            String horario = request.getParameter("horario");
            Long pacienteId = (Long) session.getAttribute("usuarioId");

            // Validações
            if (profissionalId == null || tipo == null || dia == null || horario == null) {
                session.setAttribute("erro", "Todos os campos são obrigatórios!");
                response.sendRedirect(request.getContextPath() + "/agendar-atendimento.jsp");
                return;
            }

            // Sanitizar entrada
            tipo = SecurityUtil.escaparXSS(tipo.trim());
            dia = dia.trim();
            horario = horario.trim();

            // Proteger contra SQL Injection e XSS
            if (!SecurityUtil.isSafeInput(tipo) || !SecurityUtil.isSafeInput(dia) || 
                !SecurityUtil.isSafeInput(horario)) {
                session.setAttribute("erro", "Entrada contém caracteres inválidos!");
                SecurityUtil.registrarAuditoria(
                    (String) session.getAttribute("usuarioEmail"), 
                    "Agendar atendimento - Entrada suspeita", 
                    false
                );
                response.sendRedirect(request.getContextPath() + "/agendar-atendimento.jsp");
                return;
            }

            // Construir data/hora do agendamento
            // Para simplificar, usamos a próxima ocorrência do dia e horário
            LocalDateTime dataHora = construirDataHora(dia, horario);

            PacienteDAO pacienteDAO = new PacienteDAO();
            ProfissionalDAO profissionalDAO = new ProfissionalDAO();
            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();

            Paciente paciente = pacienteDAO.buscarPorId(pacienteId);
            Profissional profissional = profissionalDAO.buscarPorId(profissionalId);

            if (paciente == null || profissional == null) {
                session.setAttribute("erro", "Paciente ou profissional não encontrado!");
                response.sendRedirect(request.getContextPath() + "/agendar-atendimento.jsp");
                return;
            }

            // Criar atendimento (Fábrica via Polimorfismo)
            Atendimento atendimento;
            if (tipo.toLowerCase().contains("exame")) {
                atendimento = new Exame(tipo, paciente, profissional, dataHora, "Agendado");
            } else {
                atendimento = new Consulta(tipo, paciente, profissional, dataHora, "Agendado");
            }
            
            atendimentoDAO.salvar(atendimento);

            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"), 
                "Agendamento realizado com sucesso", 
                true
            );

            session.setAttribute("sucesso", "✓ Atendimento agendado com sucesso!");
            response.sendRedirect(request.getContextPath() + "/dashboard-paciente.jsp");

        } catch (NumberFormatException e) {
            session.setAttribute("erro", "Erro ao processar o agendamento!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"), 
                "Agendar atendimento - Erro de formato", 
                false
            );
            response.sendRedirect(request.getContextPath() + "/agendar-atendimento.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("erro", "Erro ao agendar atendimento!");
            SecurityUtil.registrarAuditoria(
                (String) session.getAttribute("usuarioEmail"), 
                "Agendar atendimento - Erro na aplicação", 
                false
            );
            response.sendRedirect(request.getContextPath() + "/agendar-atendimento.jsp");
        }
    }

    /**
     * Constrói uma LocalDateTime baseada no dia da semana e horário
     */
    private LocalDateTime construirDataHora(String dia, String horario) {
        LocalDate hoje = LocalDate.now();
        DayOfWeek targetDay = converterDiaParaDayOfWeek(dia);
        
        // Encontra a próxima ocorrência do dia
        LocalDate data = hoje;
        while (data.getDayOfWeek() != targetDay) {
            data = data.plusDays(1);
        }

        // Parse do horário (formato HH:mm)
        String[] partes = horario.split(":");
        int hora = Integer.parseInt(partes[0]);
        int minuto = partes.length > 1 ? Integer.parseInt(partes[1]) : 0;

        return LocalDateTime.of(data.getYear(), data.getMonth(), data.getDayOfMonth(), hora, minuto);
    }

    /**
     * Converte string do dia para DayOfWeek
     */
    private DayOfWeek converterDiaParaDayOfWeek(String dia) {
        return switch (dia) {
            case "SEGUNDA" -> DayOfWeek.MONDAY;
            case "TERÇA" -> DayOfWeek.TUESDAY;
            case "QUARTA" -> DayOfWeek.WEDNESDAY;
            case "QUINTA" -> DayOfWeek.THURSDAY;
            case "SEXTA" -> DayOfWeek.FRIDAY;
            case "SABADO" -> DayOfWeek.SATURDAY;
            case "DOMINGO" -> DayOfWeek.SUNDAY;
            default -> DayOfWeek.MONDAY;
        };
    }
}
