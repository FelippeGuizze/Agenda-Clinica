<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login Paciente - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="form-container">
        <div class="login-badge badge-paciente">👤 Área do Paciente</div>
        <h1 style="font-size: 2em; margin-bottom: 30px;">Login Paciente</h1>

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
            <input type="hidden" name="tipoLogin" value="paciente">

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
            Não tem conta? <a href="${pageContext.request.contextPath}/cadastro-paciente.jsp" style="color: #00d4ff; text-decoration: none; font-weight: 600;">Cadastre-se aqui</a>
        </p>

        <a href="${pageContext.request.contextPath}/escolha-paciente.jsp" class="voltar-link">← Voltar</a>
    </div>
</body>
</html>
