<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agendar Atendimento - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="form-container">
        <h1 style="font-size: 2em; margin-bottom: 30px;">Agendar Atendimento</h1>

        <c:if test="${not empty erro}">
            <div class="erro">
                <strong>❌ Erro:</strong> ${erro}
            </div>
        </c:if>

        <c:if test="${not empty sucesso}">
            <div class="sucesso">
                <strong>✓ Sucesso:</strong> ${sucesso}
            </div>
        </c:if>

        <c:if test="${sessionScope.usuarioCategoria != 1}">
            <div class="erro">
                <strong>❌ Acesso Restrito:</strong> Apenas pacientes podem agendar atendimentos!
            </div>
            <a href="${pageContext.request.contextPath}/dashboard-paciente.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>

        <c:if test="${sessionScope.usuarioCategoria == 1}">
            <form method="POST" action="${pageContext.request.contextPath}/AgendarAtendimentoServlet">
                <div class="form-group">
                    <label for="profissional">Profissional:</label>
                    <select id="profissional" name="profissionalId" required>
                        <option value="">-- Selecione um profissional --</option>
                        <!-- Será preenchido com profissionais disponíveis -->
                    </select>
                </div>

                <div class="form-group">
                    <label for="tipo">Tipo de Atendimento:</label>
                    <select id="tipo" name="tipo" required>
                        <option value="">-- Selecione --</option>
                        <option value="Consulta">Consulta</option>
                        <option value="Procedimento">Procedimento</option>
                        <option value="Avaliação">Avaliação</option>
                        <option value="Acompanhamento">Acompanhamento</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="dia">Dia:</label>
                    <select id="dia" name="dia" required>
                        <option value="">-- Selecione um dia --</option>
                        <option value="SEGUNDA">Segunda-feira</option>
                        <option value="TERÇA">Terça-feira</option>
                        <option value="QUARTA">Quarta-feira</option>
                        <option value="QUINTA">Quinta-feira</option>
                        <option value="SEXTA">Sexta-feira</option>
                        <option value="SABADO">Sábado</option>
                        <option value="DOMINGO">Domingo</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="horario">Horário:</label>
                    <select id="horario" name="horario" required>
                        <option value="">-- Selecione um horário --</option>
                        <option value="12:00">12:00 (Meio-dia)</option>
                        <option value="16:00">16:00 (Tarde)</option>
                    </select>
                </div>

                <button type="submit" class="btn-submit">Agendar Atendimento</button>
            </form>

            <a href="${pageContext.request.contextPath}/dashboard-paciente.jsp" class="voltar-link">Voltar ao Dashboard</a>
        </c:if>
    </div>

    <script>
        // Script para carregar profissionais via AJAX (futuro)
        document.addEventListener('DOMContentLoaded', function() {
            console.log('Página de agendamento carregada');
        });
    </script>
</body>
</html>
