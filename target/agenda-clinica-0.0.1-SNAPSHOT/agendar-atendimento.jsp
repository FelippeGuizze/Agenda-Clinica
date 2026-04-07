<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agendar Consulta - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="form-container">
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
            <div style="margin-bottom: 30px; padding: 15px; background: rgba(0, 212, 255, 0.1); border-radius: 5px; border-left: 4px solid #00d4ff;">
                <strong>ℹ️ Informação:</strong> Selecione uma consulta disponível para agendar. Os preços são definidos pelos profissionais.
            </div>

            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 2px solid #00d4ff;">
                            <th style="padding: 12px; text-align: left;">Profissional</th>
                            <th style="padding: 12px; text-align: left;">Especialidade</th>
                            <th style="padding: 12px; text-align: left;">Tipo</th>
                            <th style="padding: 12px; text-align: left;">Data/Hora</th>
                            <th style="padding: 12px; text-align: left;">Preço</th>
                            <th style="padding: 12px; text-align: center;">Ação</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Será preenchida dinamicamente -->
                        <tr>
                            <td colspan="6" style="padding: 20px; text-align: center; color: #aaa;">
                                Carregando consultas disponíveis...
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

        // Função para carregar consultas disponíveis via AJAX
        function carregarConsultasDisponiveis() {
            fetch('${pageContext.request.contextPath}/ListarConsultasDisponiveisServlet')
                .then(response => response.text())
                .then(data => {
                    document.querySelector('tbody').innerHTML = data;
                })
                .catch(error => {
                    console.error('Erro ao carregar consultas:', error);
                    document.querySelector('tbody').innerHTML = '<tr><td colspan="6" style="padding: 20px; text-align: center; color: #ff6b6b;">Erro ao carregar dados</td></tr>';
                });
        }

        // Função para agendar consulta
        function agendarConsulta(atendimentoId) {
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
                document.body.appendChild(form);
                
                // Mostrar feedback
                const tbody = document.querySelector('tbody');
                const originalContent = tbody.innerHTML;
                tbody.innerHTML = '<tr><td colspan="6" style="padding: 20px; text-align: center; color: #00d4ff;">⏳ Agendando consulta...</td></tr>';
                
                form.submit();
            }
        }
    </script>
</body>
</html>