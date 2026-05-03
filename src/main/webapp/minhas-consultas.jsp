<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Minhas Consultas - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <style>
        @media print {
            @page { margin: 0; }
            body { background: white; color: black; margin: 2cm; }
            .voltar-link, .btn-print, .erro, .sucesso, div[style*="rgba(0, 212, 255, 0.1)"] { display: none !important; }
            .cards-grid { display: block !important; }
            .card-item { border: 1px solid #ccc !important; color: black !important; padding: 15px; margin-bottom: 15px; break-inside: avoid; }
            h1, h3, span, strong, div { color: black !important; }
        }
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
        .card-totalizador {
            background: linear-gradient(135deg, rgba(0, 255, 136, 0.1) 0%, rgba(0, 212, 255, 0.1) 100%);
            border: 2px solid #00ff88;
            border-radius: 12px;
            padding: 25px;
            text-align: center;
            margin-top: 20px;
            box-shadow: 0 4px 15px rgba(0, 255, 136, 0.2);
        }
    </style>
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

            <button class="btn-print" onclick="window.print()" style="margin-bottom: 20px; padding: 10px 20px; background: #ffca28; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; color: #333;">🖨️ Exportar Relatório PDF</button>

            <div class="cards-wrapper" id="consultas-container">
                <div class="cards-grid">
                    <div class="card-item" style="justify-content: center; align-items: center; color: #aaa; min-height: 200px; grid-column: 1 / -1;">
                        Carregando suas consultas...
                    </div>
                </div>
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
                    document.getElementById('consultas-container').innerHTML = data;
                })
                .catch(error => {
                    console.error('Erro ao carregar consultas:', error);
                    document.getElementById('consultas-container').innerHTML = '<div class="cards-grid"><div class="card-item" style="color: #ff6b6b; justify-content: center; align-items: center;">Erro ao carregar dados</div></div>';
                });
        }
    </script>
</body>
</html>