<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gerenciamento de Pacientes - ADMIN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="table-container">
        <h1 style="font-size: 2em; margin-bottom: 30px;">Gerenciar Dívidas/Faturas de Pacientes</h1>

        <c:if test="${sessionScope.usuarioCategoria != 0}">
            <div class="erro">
                <strong>❌ Acesso Restrito:</strong> Área exclusíva para Administradores.
            </div>
            <a href="${pageContext.request.contextPath}/index.jsp" class="voltar-link">Voltar ao Inicio</a>
        </c:if>

        <c:if test="${sessionScope.usuarioCategoria == 0}">
            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 2px solid #00d4ff;">
                            <th style="padding: 12px; text-align: left;">ID Cliente</th>
                            <th style="padding: 12px; text-align: left;">Nome (Paciente)</th>
                            <th style="padding: 12px; text-align: left;">Email</th>
                            <th style="padding: 12px; text-align: left;">Fatura Bruta (Sem Taxa)</th>
                            <th style="padding: 12px; text-align: left; color:#00ff88;">Fatura Líquida (+10% Taxa)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td colspan="5" style="padding: 20px; text-align: center; color: #aaa;">
                                Carregando pacientes...
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <a href="${pageContext.request.contextPath}/dashboard-admin.jsp" class="voltar-link">Voltar ao Dashboard Administrativo</a>
        </c:if>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            carregarUsuarios();
        });

        function carregarUsuarios() {
            fetch('${pageContext.request.contextPath}/ListarUsuariosAdminServlet')
                .then(response => response.text())
                .then(data => {
                    document.querySelector('tbody').innerHTML = data;
                })
                .catch(error => {
                    console.error('Erro:', error);
                    document.querySelector('tbody').innerHTML = '<tr><td colspan="5" style="padding: 20px; text-align: center; color: #ff6b6b;">Erro de comunicação do servidor</td></tr>';
                });
        }
    </script>
</body>
</html>
