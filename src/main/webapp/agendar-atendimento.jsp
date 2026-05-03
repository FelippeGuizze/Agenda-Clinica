<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="br.com.agendaclinica.crud.agenda_clinica.dao.AtendimentoDAO" %>
<%@ page import="java.util.List" %>

<%
    AtendimentoDAO daoFiltro = new AtendimentoDAO();
    List<String> tiposDisponiveis = daoFiltro.listarTiposDisponiveis();
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agendar Consulta - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <style>
        .filter-container {
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
            display: flex;
            gap: 15px;
            align-items: flex-end;
            flex-wrap: wrap;
        }
        .filter-group {
            flex: 1;
            min-width: 200px;
        }
        .filter-group label {
            display: block;
            margin-bottom: 8px;
            color: #aaa;
            font-weight: 600;
        }
        .filter-group select {
            width: 100%;
            padding: 10px;
            border-radius: 5px;
            border: 1px solid rgba(255, 255, 255, 0.2);
            background: rgba(0, 0, 0, 0.2);
            color: white;
            font-size: 1em;
            box-sizing: border-box;
        }
        .btn-pesquisar {
            padding: 10px 25px;
            background: linear-gradient(135deg, #00d4ff 0%, #0099ff 100%);
            border: none;
            border-radius: 5px;
            color: white;
            font-weight: bold;
            cursor: pointer;
            height: 42px;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: opacity 0.3s ease;
        }
        .btn-pesquisar:hover {
            opacity: 0.9;
        }
    </style>
</head>
<body>
    <div class="table-container">
        <h1 style="font-size: 2em; margin-bottom: 30px;">Consultas Disponíveis</h1>

        <c:if test="${not empty erro}">
            <div class="erro">
                <strong>❌ Erro:</strong> ${erro}
            </div>
            <c:remove var="erro" scope="session"/>
        </c:if>

        <c:if test="${not empty sucesso}">
            <div class="sucesso">
                <strong>✓ Sucesso:</strong> ${sucesso}
            </div>
            <c:remove var="sucesso" scope="session"/>
        </c:if>

        <c:if test="${sessionScope.usuarioCategoria != 1}">
            <div class="erro">
                <strong>❌ Acesso Restrito:</strong> Apenas pacientes podem visualizar consultas disponíveis!
            </div>
            <a href="${pageContext.request.contextPath}/dashboard-paciente.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>

        <c:if test="${sessionScope.usuarioCategoria == 1}">
            <div style="margin-bottom: 20px; padding: 15px; background: rgba(0, 212, 255, 0.1); border-radius: 5px; border-left: 4px solid #00d4ff;">
                <strong>ℹ️ Informação:</strong> Selecione o tipo de atendimento e a especialidade desejada para buscar opções disponíveis.
            </div>

            <!-- Filtro de Busca -->
            <div class="filter-container">
                <div class="filter-group">
                    <label for="filtro-tipo">Tipo de Atendimento:</label>
                    <select id="filtro-tipo">
                        <option value="Todos">Todos</option>
                        <% for(String t : tiposDisponiveis) { %>
                            <option value="<%= t %>"><%= t %></option>
                        <% } %>
                    </select>
                </div>
                <div class="filter-group">
                    <label for="filtro-especialidade">Especialidade:</label>
                    <select id="filtro-especialidade">
                        <option value="Todas">Todas</option>
                        <option value="Clínico Geral">Clínico Geral</option>
                        <option value="Cardiologia">Cardiologia</option>
                        <option value="Dermatologia">Dermatologia</option>
                        <option value="Neurologia">Neurologia</option>
                        <option value="Ortopedia">Ortopedia</option>
                        <option value="Pediatria">Pediatria</option>
                        <option value="Psiquiatria">Psiquiatria</option>
                    </select>
                </div>
                <button type="button" class="btn-pesquisar" onclick="carregarConsultasDisponiveis()">
                    🔍 Pesquisar
                </button>
            </div>

            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 2px solid #00d4ff;">
                            <th style="padding: 12px; text-align: left;">Profissional</th>
                            <th style="padding: 12px; text-align: left;">Especialidade</th>
                            <th style="padding: 12px; text-align: left; min-width: 180px;">Tipo</th>
                            <th style="padding: 12px; text-align: left;">Data/Hora</th>
                            <th style="padding: 12px; text-align: left;">Preço</th>
                            <th style="padding: 12px; text-align: center;">Ação</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Será preenchida dinamicamente -->
                        <tr>
                            <td colspan="6" style="padding: 20px; text-align: center; color: #aaa;">
                                Carregando...
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <a href="${pageContext.request.contextPath}/dashboard-paciente.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>
    </div>

    <script>
        // Carregar consultas disponíveis ao carregar a página
        document.addEventListener('DOMContentLoaded', function() {
            carregarConsultasDisponiveis();
        });

        // Função para carregar consultas disponíveis via AJAX com filtros
        function carregarConsultasDisponiveis() {
            const tbody = document.querySelector('tbody');
            tbody.innerHTML = '<tr><td colspan="6" style="padding: 20px; text-align: center; color: #aaa;">⏳ Buscando...</td></tr>';

            const tipo = document.getElementById('filtro-tipo') ? document.getElementById('filtro-tipo').value : '';
            const especialidade = document.getElementById('filtro-especialidade') ? document.getElementById('filtro-especialidade').value : '';

            const params = new URLSearchParams();
            if (tipo && tipo !== 'Todos') params.append('tipo', tipo);
            if (especialidade && especialidade !== 'Todas') params.append('especialidade', especialidade);

            fetch('${pageContext.request.contextPath}/ListarConsultasDisponiveisServlet?' + params.toString())
                .then(response => response.text())
                .then(data => {
                    tbody.innerHTML = data;
                })
                .catch(error => {
                    console.error('Erro ao carregar consultas:', error);
                    tbody.innerHTML = '<tr><td colspan="6" style="padding: 20px; text-align: center; color: #ff6b6b;">Erro ao carregar dados</td></tr>';
                });
        }

        // Função para agendar consulta
        function agendarConsulta(atendimentoId, isExame) {
            if (confirm('Tem certeza que deseja agendar esta consulta?')) {
                // Criar formulário dinâmico para POST
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/AgendarConsultaEspecificaServlet';

                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'atendimentoId';
                input.value = atendimentoId;

                form.appendChild(input);

                if (isExame) {
                    const cb = document.getElementById('taxa_' + atendimentoId);
                    if (cb && cb.checked) {
                        const cbInput = document.createElement('input');
                        cbInput.type = 'hidden';
                        cbInput.name = 'incluirTaxaLaboratorial';
                        cbInput.value = 'true';
                        form.appendChild(cbInput);
                    }
                }

                document.body.appendChild(form);
                
                // Mostrar feedback
                const tbody = document.querySelector('tbody');
                tbody.innerHTML = '<tr><td colspan="6" style="padding: 20px; text-align: center; color: #00d4ff;">⏳ Agendando consulta...</td></tr>';
                
                form.submit();
            }
        }
    </script>
</body>
</html>