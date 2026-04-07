<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="form-container">
        <h1 style="font-size: 2em; margin-bottom: 30px;">Login</h1>

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

        <form method="POST" action="${pageContext.request.contextPath}/LoginServlet">
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
            Não tem conta? <a href="${pageContext.request.contextPath}/cadastro.jsp" style="color: #00d4ff; text-decoration: none; font-weight: 600;">Faça cadastro aqui</a>
        </p>

        <a href="${pageContext.request.contextPath}/index.jsp" class="voltar-link">Voltar para início</a>
    </div>
</body>
</html>
