<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Definir Preços - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="form-container">
        <h1 style="font-size: 2em; margin-bottom: 30px;">Definir Tipos de Consulta e Preços</h1>

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
                <strong>❌ Acesso Restrito:</strong> Apenas profissionais podem definir preços!
            </div>
            <a href="${pageContext.request.contextPath}/dashboard-medico.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>

        <c:if test="${sessionScope.usuarioCategoria == 2}">
            <div style="margin-bottom: 30px; padding: 15px; background: rgba(0, 212, 255, 0.1); border-radius: 5px; border-left: 4px solid #00d4ff;">
                <strong>ℹ️ Informação:</strong> Defina os tipos de consulta que você oferece e seus respectivos preços. Os pacientes verão estes preços ao agendar.
            </div>

            <form method="POST" action="${pageContext.request.contextPath}/AdicionarTipoConsultaServlet">
                <div class="form-group">
                    <label for="nome">Nome do Tipo de Consulta:</label>
                    <input type="text" id="nome" name="nome" required placeholder="Ex: Consulta Geral, Exame de Sangue">
                </div>

                <div class="form-group">
                    <label for="preco">Preço (R$):</label>
                    <input type="number" id="preco" name="preco" step="0.01" min="0" required placeholder="150.00">
                </div>

                <div class="form-group">
                    <label for="descricao">Descrição:</label>
                    <textarea id="descricao" name="descricao" rows="3" placeholder="Descreva o que inclui esta consulta..." style="resize: vertical; max-width: 100%; overflow-wrap: break-word;"></textarea>
                </div>

                <button type="submit" class="btn-submit">Adicionar Tipo de Consulta</button>
            </form>

            <hr style="border: 1px solid rgba(255, 255, 255, 0.2); margin: 30px 0;">

            <h2 style="color: #00d4ff; margin-bottom: 20px;">Seus Tipos de Consulta</h2>

            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 2px solid #00d4ff;">
                            <th style="padding: 10px; text-align: left;">Tipo</th>
                            <th style="padding: 10px; text-align: left;">Preço</th>
                            <th style="padding: 10px; text-align: left;">Descrição</th>
                            <th style="padding: 10px; text-align: left;">Status</th>
                            <th style="padding: 10px; text-align: center;">Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Será preenchida dinamicamente -->
                        <tr>
                            <td colspan="5" style="padding: 10px; text-align: center; color: #aaa;">
                                Carregando tipos de consulta...
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <hr style="border: 1px solid rgba(255, 255, 255, 0.2); margin: 30px 0;">

            <div style="text-align: center; margin-top: 30px;">
                <form method="POST" action="${pageContext.request.contextPath}/GerarConsultasDisponiveisServlet" style="display: inline;">
                    <button type="submit" style="background: linear-gradient(135deg, #00ff88 0%, #00cc66 100%); color: white; border: none; padding: 15px 30px; border-radius: 8px; font-weight: 600; cursor: pointer; font-size: 1.1em;">
                        🚀 Gerar Consultas Disponíveis
                    </button>
                </form>
                <p style="margin-top: 10px; color: #aaa; font-size: 0.9em;">
                    Clique para gerar automaticamente consultas disponíveis baseadas nas suas configurações
                </p>
            </div>

            <a href="${pageContext.request.contextPath}/dashboard-medico.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>
    </div>

    <script>
        // Carregar tipos de consulta ao carregar a página
        document.addEventListener('DOMContentLoaded', function() {
            carregarTiposConsulta();
        });

        // Função para carregar tipos de consulta via AJAX
        function carregarTiposConsulta() {
            fetch('${pageContext.request.contextPath}/ListarTiposConsultaServlet')
                .then(response => response.text())
                .then(data => {
                    document.querySelector('tbody').innerHTML = data;
                })
                .catch(error => {
                    console.error('Erro ao carregar tipos de consulta:', error);
                    document.querySelector('tbody').innerHTML = '<tr><td colspan="5" style="padding: 10px; text-align: center; color: #ff6b6b;">Erro ao carregar dados</td></tr>';
                });
        }

        // Função para remover tipo de consulta
        function removerTipoConsulta(id) {
            if (confirm('Tem certeza que deseja remover este tipo de consulta? Isso pode afetar consultas já agendadas.')) {
                // Criar formulário dinâmico para POST
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/RemoverTipoConsultaServlet';

                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'id';
                input.value = id;

                form.appendChild(input);
                document.body.appendChild(form);
                form.submit();
            }
        }

        // Recarregar após adicionar novo tipo
        <c:if test="${not empty sucesso}">
            setTimeout(function() {
                carregarTiposConsulta();
            }, 1000);
        </c:if>
    </script>
</body>
</html>