package br.com.agendaclinica.crud.agenda_clinica.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import br.com.agendaclinica.crud.agenda_clinica.dao.AtendimentoDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.UsuarioDAO;
import br.com.agendaclinica.crud.agenda_clinica.model.Atendimento;
import br.com.agendaclinica.crud.agenda_clinica.model.Usuario;

@WebServlet("/BackupDatabaseServlet")
public class BackupDatabaseServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(0)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso Negado");
            return;
        }

        // Força download ao invés de exibir
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"backup_clinica.json\"");

        PrintWriter out = response.getWriter();

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();

            List<Usuario> usuarios = usuarioDAO.listarTodos();
            List<Atendimento> atendimentos = atendimentoDAO.listarTodos();

            com.google.gson.JsonObject root = new com.google.gson.JsonObject();
            com.google.gson.JsonArray jsonUsuarios = new com.google.gson.JsonArray();
            
            for (Usuario u : usuarios) {
                com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
                obj.addProperty("id", u.getId());
                obj.addProperty("nome", u.getNome());
                obj.addProperty("email", u.getEmail());
                obj.addProperty("categoria", u.getCategoria());
                if (u.getPacienteId() != null) obj.addProperty("pacienteId", u.getPacienteId());
                if (u.getProfissionalId() != null) obj.addProperty("profissionalId", u.getProfissionalId());
                jsonUsuarios.add(obj);
            }
            root.add("usuarios", jsonUsuarios);

            com.google.gson.JsonArray jsonAtendimentos = new com.google.gson.JsonArray();
            for (Atendimento a : atendimentos) {
                com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
                obj.addProperty("id", a.getId());
                obj.addProperty("tipo_classe", a.getClass().getSimpleName());
                obj.addProperty("tipo", a.getTipo());
                obj.addProperty("status", a.getStatus());
                obj.addProperty("datahora", a.getDatahora().toString());
                obj.addProperty("preco", a.getPreco());
                if (a.getOrientacaoMedico() != null) obj.addProperty("orientacao_medico", a.getOrientacaoMedico());
                if (a.getPaciente() != null) obj.addProperty("paciente_id", a.getPaciente().getId());
                obj.addProperty("profissional_id", a.getProfissional().getId());
                jsonAtendimentos.add(obj);
            }
            root.add("atendimentos", jsonAtendimentos);

            com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
            out.print(gson.toJson(root));

        } catch (Exception e) {
            e.printStackTrace();
            out.println("{\"erro\": \"Falha ao gerar o Backup: " + e.getMessage() + "\"}");
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}
