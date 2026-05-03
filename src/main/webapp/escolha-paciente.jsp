<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Área do Paciente - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="escolha-container">
        <div class="login-badge badge-paciente">👤 Área do Paciente</div>
        <h1>Bem-vindo, Paciente!</h1>
        <p class="escolha-subtitle">O que deseja fazer?</p>

        <div class="escolha-buttons">
            <a href="${pageContext.request.contextPath}/login-paciente.jsp" class="btn-escolha btn-escolha-login" id="btn-login-paciente">
                🔑 Já tenho conta — Entrar
            </a>
            <a href="${pageContext.request.contextPath}/cadastro-paciente.jsp" class="btn-escolha btn-escolha-cadastro" id="btn-cadastro-paciente">
                ✏️ Criar nova conta
            </a>
        </div>

        <a href="${pageContext.request.contextPath}/index.jsp" class="voltar-link">← Voltar para início</a>
    </div>
</body>
</html>
