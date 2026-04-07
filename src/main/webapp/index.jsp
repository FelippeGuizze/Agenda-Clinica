<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="container">
        <h1>Agenda Clínica</h1>
        <div style="display: flex; flex-direction: column; gap: 20px; align-items: center;">
            <button class="botao botao-cadastro" onclick="location.href='${pageContext.request.contextPath}/cadastro.jsp'">Cadastro</button>
            <button class="botao botao-login" onclick="location.href='${pageContext.request.contextPath}/login.jsp'">Login</button>
        </div>
    </div>
</body>
</html>
