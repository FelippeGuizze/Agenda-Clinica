<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gerenciar Usuários - ADMIN</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <style>
        .cards-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .card-item {
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 12px;
            padding: 20px;
            display: flex;
            flex-direction: column;
            gap: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        .card-item:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 15px rgba(0, 212, 255, 0.2);
            border-color: rgba(0, 212, 255, 0.3);
        }
        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            padding-bottom: 10px;
            margin-bottom: 5px;
        }
        .card-title {
            font-size: 1.2em;
            font-weight: bold;
            color: #00d4ff;
            margin: 0;
        }
        .card-subtitle {
            font-size: 0.9em;
            color: #aaa;
        }
        .card-info {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 0.95em;
        }
        .card-price {
            font-size: 1.3em;
            font-weight: bold;
            color: #00ff88;
            margin-top: auto;
            padding-top: 15px;
            border-top: 1px dashed rgba(255, 255, 255, 0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
    </style>
</head>
<body>
    <div class="table-container">
        <h1 style="font-size: 2em; margin-bottom: 30px;">Gerenciar Usuários da Clínica</h1>

        <c:if test="${sessionScope.usuarioCategoria != 0}">
            <div class="erro">
                <strong>❌ Acesso Restrito:</strong> Área exclusíva para Administradores.
            </div>
            <a href="${pageContext.request.contextPath}/index.jsp" class="voltar-link">Voltar ao Inicio</a>
        </c:if>

        <c:if test="${sessionScope.usuarioCategoria == 0}">
            <div class="cards-wrapper" id="pacientes-container">
                <div class="cards-grid">
                    <div class="card-item" style="justify-content: center; align-items: center; color: #aaa; min-height: 200px; grid-column: 1 / -1;">
                        Carregando pacientes...
                    </div>
                </div>
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
                    document.getElementById('pacientes-container').innerHTML = data;
                })
                .catch(error => {
                    console.error('Erro:', error);
                    document.getElementById('pacientes-container').innerHTML = '<div class="cards-grid"><div class="card-item" style="color: #ff6b6b; justify-content: center; align-items: center;">Erro de comunicação do servidor</div></div>';
                });
        }
    </script>
</body>
</html>
