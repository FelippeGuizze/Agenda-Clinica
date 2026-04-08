<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Minha Agenda - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="table-container">
        <h1 style="font-size: 2em; margin-bottom: 30px;">Oferecer Novo Horário</h1>

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
            <!-- Importante: O front-end de Datetime-Local envia dados no formato ISO YYYY-MM-DDTHH:mm -->
            <form method="POST" action="${pageContext.request.contextPath}/GerarConsultaEspecificaServlet">
                <div class="form-group">
                    <label for="datahora">Data e Horário Exatos:</label>
                    <input type="datetime-local" id="datahora" name="datahora" required>
                </div>

                <div class="form-group">
                    <label for="classe_heranca">Classe do Atendimento (Herança):</label>
                    <select id="classe_heranca" name="classe_heranca" required>
                        <option value="" disabled selected>-- Selecione a Classe --</option>
                        <option value="Consulta">Consulta (Atendimento clínico)</option>
                        <option value="Exame">Exame (Procedimento diagnóstico)</option>
                    </select>
                    <small style="color: #aaa;">Define o comportamento polimórfico e o cálculo de custo do atendimento.</small>
                </div>

                <div class="form-group">
                    <label for="tipo">Descrição do Atendimento (ex: Consulta Geral, Exame de Sangue):</label>
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

                <button type="submit" class="btn-submit">Criar Horário Disponível</button>
            </form>

            <hr style="border: 1px solid rgba(255, 255, 255, 0.2); margin: 30px 0;">

            <h2 style="color: #00d4ff; margin-bottom: 20px;">Seus Horários Oferecidos (Livres)</h2>
            
            <div style="overflow-x: auto;">
                <table style="width: 100%; border-collapse: collapse;">
                    <thead>
                        <tr style="border-bottom: 2px solid #00d4ff;">
                            <th style="padding: 10px; text-align: left;">Classe de Herança</th>
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
    </script>
</body>
</html>
