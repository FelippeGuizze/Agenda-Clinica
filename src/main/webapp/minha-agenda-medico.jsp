<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Oferecer Horários - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
    <style>
        @media print {
            @page { margin: 0; }
            body { background: white; color: black; margin: 2cm; }
            .voltar-link, .btn-print, .erro, .sucesso, form, hr, h1, .form-group, .btn-submit, small { display: none !important; }
            table { width: 100%; border-collapse: collapse; color: black !important; }
            th, td { border: 1px solid #ccc !important; color: black !important; padding: 8px; text-align: left; }
            h2 { color: black !important; margin-top: 0; }
        }

        /* Estilos para o container de múltiplos horários */
        .datahora-list {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }
        .datahora-item {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .datahora-item input[type="datetime-local"] {
            flex: 1;
        }
        .btn-add-slot {
            background: linear-gradient(135deg, #00d4ff, #0099ff);
            color: white;
            border: none;
            border-radius: 50%;
            width: 36px !important;
            height: 36px !important;
            flex: 0 0 36px !important;
            font-size: 1.4em;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: transform 0.15s, box-shadow 0.15s;
            flex-shrink: 0;
            line-height: 1;
        }
        .btn-add-slot:hover {
            transform: scale(1.15);
            box-shadow: 0 4px 12px rgba(0, 212, 255, 0.5);
        }
        .btn-remove-slot {
            background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
            color: white;
            border: none;
            border-radius: 50%;
            width: 36px !important;
            height: 36px !important;
            flex: 0 0 36px !important;
            font-size: 1.4em;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: transform 0.15s, box-shadow 0.15s;
            flex-shrink: 0;
            line-height: 1;
        }
        .btn-remove-slot:hover {
            transform: scale(1.15);
            box-shadow: 0 4px 12px rgba(255, 107, 107, 0.5);
        }
        .nicho-badge {
            display: inline-block;
            padding: 4px 14px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: 700;
            letter-spacing: 0.5px;
            margin-left: 10px;
            vertical-align: middle;
        }
        .nicho-consulta { background: rgba(0, 212, 255, 0.2); color: #00d4ff; border: 1px solid #00d4ff; }
        .nicho-exame    { background: rgba(102, 126, 234, 0.2); color: #a78bfa; border: 1px solid #a78bfa; }
    </style>
</head>
<body>
    <div class="table-container">
        <%-- Título dinâmico conforme o nicho do médico --%>
        <c:choose>
            <c:when test="${sessionScope.profissionalNicho == 'Exame'}">
                <h1 style="font-size: 2em; margin-bottom: 30px;">
                    🔬 Oferecer Horários de Exame
                    <span class="nicho-badge nicho-exame">Exame</span>
                </h1>
            </c:when>
            <c:otherwise>
                <h1 style="font-size: 2em; margin-bottom: 30px;">
                    🩺 Oferecer Horários de Consulta
                    <span class="nicho-badge nicho-consulta">Consulta</span>
                </h1>
            </c:otherwise>
        </c:choose>

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

        <c:if test="${sessionScope.usuarioCategoria != 2}">
            <div class="erro">
                <strong>❌ Acesso Restrito:</strong> Apenas profissionais podem acessar.
            </div>
            <a href="${pageContext.request.contextPath}/dashboard-medico.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>

        <c:if test="${sessionScope.usuarioCategoria == 2}">
            <form method="POST" action="${pageContext.request.contextPath}/GerarConsultaEspecificaServlet">

                <%-- Data e Horário — múltiplos slots com botão + --%>
                <div class="form-group">
                    <label>Data e Horário Exatos:</label>
                    <small style="color: #aaa; display: block; margin-bottom: 8px;">
                        Use o botão <strong style="color:#00d4ff;">+</strong> para adicionar vários horários de uma vez para o mesmo tipo de atendimento.
                    </small>
                    <div class="datahora-list" id="datahoraList">
                        <div class="datahora-item">
                            <input type="datetime-local" name="datahora" required>
                            <button type="button" class="btn-add-slot" onclick="adicionarSlot()" title="Adicionar mais um horário">+</button>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label for="tipo">Descrição do Atendimento (ex:
                        <c:choose>
                            <c:when test="${sessionScope.profissionalNicho == 'Exame'}">Exame de Sangue, Raio-X</c:when>
                            <c:otherwise>Consulta Geral, Retorno</c:otherwise>
                        </c:choose>
                    ):</label>
                    <input type="text" id="tipo" name="tipo" placeholder="Nome que o paciente verá" required>
                </div>

                <div class="form-group">
                    <label for="preco">Preço Base (R$):</label>
                    <input type="number" id="preco" name="preco" step="0.01" min="0" required placeholder="150.00">
                </div>

                <div class="form-group">
                    <label for="orientacao">Orientações Extras (Opcional):</label>
                    <input type="text" id="orientacao" name="orientacao" placeholder="Ex: chegar 10 min antes, limite de idade...">
                </div>

                <button type="submit" class="btn-submit" style="width: fit-content; padding: 14px 40px;">
                    <c:choose>
                        <c:when test="${sessionScope.profissionalNicho == 'Exame'}">🔬 Criar Horário(s) de Exame</c:when>
                        <c:otherwise>🩺 Criar Horário(s) de Consulta</c:otherwise>
                    </c:choose>
                </button>
            </form>

            <hr style="border: 1px solid rgba(255, 255, 255, 0.2); margin: 30px 0;">

            <h2 style="color: #00d4ff; margin-bottom: 20px;">Seus Horários Oferecidos (Livres)</h2>
            <button class="btn-print" onclick="window.print()" style="margin-bottom: 20px; padding: 10px 20px; background: #ffca28; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; color: #333;">🖨️ Exportar Relatório PDF</button>
            
            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 2px solid #00d4ff;">
                            <th style="padding: 10px; text-align: left;">Tipo</th>
                            <th style="padding: 10px; text-align: left;">Descrição</th>
                            <th style="padding: 10px; text-align: left;">Data/Hora</th>
                            <th style="padding: 10px; text-align: left;">Preço Calculado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td colspan="4" style="padding: 10px; text-align: center; color: #aaa;">Carregando horários livres...</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <a href="${pageContext.request.contextPath}/dashboard-medico.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            carregarMeusHorariosLivres();
        });

        function carregarMeusHorariosLivres() {
            fetch('${pageContext.request.contextPath}/ListarConsultaLivreMedicoServlet')
                .then(response => response.text())
                .then(data => {
                    document.querySelector('tbody').innerHTML = data;
                })
                .catch(error => {
                    console.error('Erro:', error);
                    document.querySelector('tbody').innerHTML = '<tr><td colspan="4" style="padding: 10px; text-align: center; color: #ff6b6b;">Erro ao carregar dados</td></tr>';
                });
        }

        // Adiciona novo campo de data/hora com botão + e −
        function adicionarSlot() {
            const list = document.getElementById('datahoraList');
            const item = document.createElement('div');
            item.className = 'datahora-item';
            item.innerHTML = `
                <input type="datetime-local" name="datahora" required>
                <button type="button" class="btn-add-slot" onclick="adicionarSlot()" title="Adicionar mais um horário">+</button>
                <button type="button" class="btn-remove-slot" onclick="removerSlot(this)" title="Remover este horário">−</button>
            `;
            list.appendChild(item);
            // Focar no novo campo
            item.querySelector('input').focus();
        }

        function removerSlot(btn) {
            const item = btn.closest('.datahora-item');
            // Nunca remove o último campo
            const list = document.getElementById('datahoraList');
            if (list.querySelectorAll('.datahora-item').length > 1) {
                item.remove();
            }
        }
    </script>
</body>
</html>
