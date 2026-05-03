package br.com.agendaclinica.crud.agenda_clinica.controller;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.agendaclinica.crud.agenda_clinica.dao.AtendimentoDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.PacienteDAO;
import br.com.agendaclinica.crud.agenda_clinica.dao.ProfissionalDAO;
import br.com.agendaclinica.crud.agenda_clinica.model.Atendimento;
import br.com.agendaclinica.crud.agenda_clinica.model.Paciente;
import br.com.agendaclinica.crud.agenda_clinica.model.Profissional;

@WebServlet("/RestaurarDatabaseServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class RestaurarDatabaseServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        if (session.getAttribute("usuarioCategoria") == null ||
            !session.getAttribute("usuarioCategoria").equals(0)) {
            session.setAttribute("erro", "Acesso Negado. Apenas administradores podem restaurar rotinas.");
            response.sendRedirect(request.getContextPath() + "/dashboard-admin.jsp");
            return;
        }

        try {
            Part filePart = request.getPart("backupFile");
            if (filePart == null || filePart.getSize() == 0) {
                session.setAttribute("erro", "Nenhum arquivo enviado!");
                response.sendRedirect(request.getContextPath() + "/dashboard-admin.jsp");
                return;
            }

            // Lê o JSON direto do InputStream do arquivo upado!
            Reader reader = new InputStreamReader(filePart.getInputStream(), "UTF-8");
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            AtendimentoDAO atendimentoDAO = new AtendimentoDAO();
            PacienteDAO pacienteDAO = new PacienteDAO();
            ProfissionalDAO profissionalDAO = new ProfissionalDAO();
            
            int modificados = 0;

            // === RESTAURAR PACIENTES ===
            if (jsonObject.has("pacientes")) {
                JsonArray pacientesArray = jsonObject.getAsJsonArray("pacientes");
                for (JsonElement element : pacientesArray) {
                    JsonObject pJson = element.getAsJsonObject();
                    Long id = pJson.get("id").getAsLong();
                    Paciente existente = pacienteDAO.buscarPorId(id);

                    if (existente != null) {
                        if (pJson.has("nome") && !pJson.get("nome").isJsonNull()) {
                            existente.setNome(pJson.get("nome").getAsString());
                        }
                        if (pJson.has("contato") && !pJson.get("contato").isJsonNull()) {
                            existente.setContato(pJson.get("contato").getAsString());
                        }
                        pacienteDAO.atualizar(existente);
                        modificados++;
                    }
                }
            }

            // === RESTAURAR PROFISSIONAIS ===
            if (jsonObject.has("profissionais")) {
                JsonArray profissionaisArray = jsonObject.getAsJsonArray("profissionais");
                for (JsonElement element : profissionaisArray) {
                    JsonObject profJson = element.getAsJsonObject();
                    Long id = profJson.get("id").getAsLong();
                    Profissional existente = profissionalDAO.buscarPorId(id);

                    if (existente != null) {
                        if (profJson.has("nome") && !profJson.get("nome").isJsonNull()) {
                            existente.setNome(profJson.get("nome").getAsString());
                        }
                        if (profJson.has("categoria") && !profJson.get("categoria").isJsonNull()) {
                            existente.setCategoria(profJson.get("categoria").getAsString());
                        }
                        if (profJson.has("especialidade") && !profJson.get("especialidade").isJsonNull()) {
                            existente.setEspecialidade(profJson.get("especialidade").getAsString());
                        }
                        profissionalDAO.atualizar(existente);
                        modificados++;
                    }
                }
            }

            // === RESTAURAR ATENDIMENTOS ===
            if (jsonObject.has("atendimentos")) {
                JsonArray atendimentosArray = jsonObject.getAsJsonArray("atendimentos");
                
                for (JsonElement element : atendimentosArray) {
                    JsonObject attJson = element.getAsJsonObject();
                    
                    Long id = attJson.get("id").getAsLong();
                    Atendimento attExistente = atendimentoDAO.buscarPorId(id);
                    
                    if (attExistente != null) {
                        // Atualizando campos do Backup
                        if (attJson.has("status") && !attJson.get("status").isJsonNull()) {
                            attExistente.setStatus(attJson.get("status").getAsString());
                        }
                        if (attJson.has("preco") && !attJson.get("preco").isJsonNull()) {
                            attExistente.setPreco(attJson.get("preco").getAsBigDecimal());
                        }
                        if (attJson.has("valor_taxa") && !attJson.get("valor_taxa").isJsonNull()) {
                            attExistente.setValorTaxa(attJson.get("valor_taxa").getAsBigDecimal());
                        }
                        if (attJson.has("preco_final") && !attJson.get("preco_final").isJsonNull()) {
                            attExistente.setPrecoFinal(attJson.get("preco_final").getAsBigDecimal());
                        }
                        if (attJson.has("incluir_taxa_laboratorial") && !attJson.get("incluir_taxa_laboratorial").isJsonNull()) {
                            attExistente.setIncluirTaxaLaboratorial(attJson.get("incluir_taxa_laboratorial").getAsBoolean());
                        }
                        if (attJson.has("disponibilidade_id") && !attJson.get("disponibilidade_id").isJsonNull()) {
                            attExistente.setDisponibilidadeId(attJson.get("disponibilidade_id").getAsLong());
                        }
                        if (attJson.has("orientacao_medico") && !attJson.get("orientacao_medico").isJsonNull()) {
                            attExistente.setOrientacaoMedico(attJson.get("orientacao_medico").getAsString());
                        }
                        if (attJson.has("tipo") && !attJson.get("tipo").isJsonNull()) {
                            attExistente.setTipo(attJson.get("tipo").getAsString());
                        }
                        if (attJson.has("datahora") && !attJson.get("datahora").isJsonNull()) {
                            attExistente.setDatahora(LocalDateTime.parse(attJson.get("datahora").getAsString()));
                        }
                        
                        if (attJson.has("paciente_id") && !attJson.get("paciente_id").isJsonNull()) {
                            Paciente p = pacienteDAO.buscarPorId(attJson.get("paciente_id").getAsLong());
                            attExistente.setPaciente(p);
                        } else if (attJson.has("paciente_id") && attJson.get("paciente_id").isJsonNull()) {
                             attExistente.setPaciente(null);
                        }
                        
                        atendimentoDAO.atualizar(attExistente);
                        modificados++;
                    }
                    // Ignoraremos a inserção direta de IDs conflitantes sem o mapeamento hierárquico
                }
            } else {
                session.setAttribute("erro", "Formato de Backup JSON inválido. Estrutura não reconhecida.");
                response.sendRedirect(request.getContextPath() + "/dashboard-admin.jsp");
                return;
            }

            session.setAttribute("sucesso", "Backup processado! " + modificados + " registros sincronizados/atualizados com a DB atual.");
            response.sendRedirect(request.getContextPath() + "/dashboard-admin.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("erro", "Falha crítica ao Restaurar: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/dashboard-admin.jsp");
        }
    }
}
