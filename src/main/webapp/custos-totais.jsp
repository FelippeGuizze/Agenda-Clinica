<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Custos Totais - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="table-container">
        <h1 style="font-size: 2em; margin-bottom: 30px;">Histórico e Faturamento de Extrato OOP</h1>

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
                <strong>❌ Acesso Restrito:</strong> Apenas pacientes podem acessar esta área.
            </div>
            <a href="${pageContext.request.contextPath}/dashboard-paciente.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>

        <c:if test="${sessionScope.usuarioCategoria == 1}">
            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 2px solid #00d4ff;">
                            <th style="padding: 12px; text-align: left;">Profissional</th>
                            <th style="padding: 12px; text-align: left;">Tipo Obj</th>
                            <th style="padding: 12px; text-align: left;">Cálculo e Detalhamento da Taxa</th>
                            <th style="padding: 12px; text-align: left;">Orientações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td colspan="4" style="padding: 20px; text-align: center; color: #aaa;">
                                Carregando o extrato financeiro...
                            </td>
                        </tr>
                    </tbody>
                    <tfoot style="border-top: 3px solid #00ff88;">
                        <tr>
                            <td colspan="4" style="padding: 15px; text-align: right; font-size: 1.5em; font-weight: 700; color: #00ff88;" id="totalizador">
                                Total Acumulado: R$ 0,00
                            </td>
                        </tr>
                    </tfoot>
                </table>
            </div>

            <a href="${pageContext.request.contextPath}/dashboard-paciente.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            carregarCustosTotais();
        });

        function carregarCustosTotais() {
            fetch('${pageContext.request.contextPath}/ListarCustosTotaisServlet')
                .then(response => response.json())
                .then(data => {
                    const tbody = document.querySelector('tbody');
                    const tfoot = document.getElementById('totalizador');
                    
                    if(data.erro) {
                        tbody.innerHTML = '<tr><td colspan="4" style="padding: 20px; text-align: center; color: #ff6b6b;">' + data.erro + '</td></tr>';
                        return;
                    }

                    if(!data.itens || data.itens.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="4" style="padding: 20px; text-align: center; color: #aaa;">Nenhum custo gerado.</td></tr>';
                        return;
                    }

                    let html = '';
                    data.itens.forEach(item => {
                        html += '<tr style="border-bottom: 1px solid rgba(255, 255, 255, 0.1);">';
                        html += '<td style="padding: 12px;">' + item.profissional + '</td>';
                        html += '<td style="padding: 12px; font-weight: bold; color: #00d4ff;">' + item.classe + '</td>';
                        html += '<td style="padding: 12px; font-family: monospace;">' + item.extrato + '</td>';
                        html += '<td style="padding: 12px; color: #ffeb3b; font-size: 0.9em;">' + item.orientacoes + '</td>';
                        html += '</tr>';
                    });
                    
                    tbody.innerHTML = html;
                    tfoot.innerHTML = "Total a Pagar Acumulado: R$ " + data.totalStr;
                })
                .catch(error => {
                    console.error('Erro ao carregar os custos:', error);
                    document.querySelector('tbody').innerHTML = '<tr><td colspan="4" style="padding: 20px; text-align: center; color: #ff6b6b;">Erro de servidor</td></tr>';
                });
        }
    </script>
</body>
</html>
