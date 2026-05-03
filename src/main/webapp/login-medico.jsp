<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Médico - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="form-container">
        <div class="login-badge badge-medico">🩺 Área do Médico</div>
        <h1 style="font-size: 2em; margin-bottom: 30px;">Login Profissional</h1>

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

        <form method="POST" action="${pageContext.request.contextPath}/LoginServlet">
            <input type="hidden" name="tipoLogin" value="medico">

            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
            </div>

            <div class="form-group">
                <label for="senha">Senha:</label>
                <input type="password" id="senha" name="senha" required>
            </div>

            <button type="submit" class="btn-submit">Entrar</button>
        </form>

        <p style="margin-top: 20px; color: #aaa;">
            Não tem conta? <a href="${pageContext.request.contextPath}/cadastro-medico.jsp" style="color: #667eea; text-decoration: none; font-weight: 600;">Cadastre-se aqui</a>
        </p>

        <a href="${pageContext.request.contextPath}/escolha-medico.jsp" class="voltar-link">← Voltar</a>
    </div>
</body>
</html>
