<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Minhas Consultas - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="table-container">
        <h1 style="font-size: 2em; margin-bottom: 30px;">Minhas Consultas Agendadas</h1>

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
                <strong>❌ Acesso Restrito:</strong> Apenas pacientes podem visualizar suas consultas!
            </div>
            <a href="${pageContext.request.contextPath}/dashboard-paciente.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>

        <c:if test="${sessionScope.usuarioCategoria == 1}">
            <div style="margin-bottom: 30px; padding: 15px; background: rgba(0, 212, 255, 0.1); border-radius: 5px; border-left: 4px solid #00d4ff;">
                <strong>ℹ️ Informação:</strong> Aqui você pode visualizar todas as suas consultas agendadas e seus respectivos status.
            </div>

            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 2px solid #00d4ff;">
                            <th style="padding: 12px; text-align: left;">Profissional</th>
                            <th style="padding: 12px; text-align: left;">Especialidade</th>
                            <th style="padding: 12px; text-align: left; min-width: 180px;">Tipo</th>
                            <th style="padding: 12px; text-align: left;">Data/Hora</th>
                            <th style="padding: 12px; text-align: left;">Preço Calculado</th>
                            <th style="padding: 12px; text-align: left;">Status</th>
                            <th style="padding: 12px; text-align: left;">Orientações</th>
                            <th style="padding: 12px; text-align: center;">Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Será preenchida dinamicamente -->
                        <tr>
                            <td colspan="8" style="padding: 20px; text-align: center; color: #aaa;">
                                Carregando suas consultas...
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <a href="${pageContext.request.contextPath}/dashboard-paciente.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>
    </div>

    <script>
        // Carregar consultas do paciente ao carregar a página
        document.addEventListener('DOMContentLoaded', function() {
            carregarMinhasConsultas();
        });

        // Função para carregar consultas do paciente via AJAX
        function carregarMinhasConsultas() {
            fetch('${pageContext.request.contextPath}/ListarMinhasConsultasServlet')
                .then(response => response.text())
                .then(data => {
                    document.querySelector('tbody').innerHTML = data;
                })
                .catch(error => {
                    console.error('Erro ao carregar consultas:', error);
                    document.querySelector('tbody').innerHTML = '<tr><td colspan="8" style="padding: 20px; text-align: center; color: #ff6b6b;">Erro ao carregar dados</td></tr>';
                });
        }
    </script>
</body>
</html>