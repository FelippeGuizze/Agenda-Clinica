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
    <div class="index-page">
        <!-- Botão Painel ADM no canto superior direito -->
        <div class="index-header">
            <a href="${pageContext.request.contextPath}/login-admin.jsp" class="btn-adm" id="btn-painel-adm">
                🔒 Painel ADM
            </a>
        </div>

        <!-- Hero -->
        <div class="index-hero">
            <h1>Agenda Clínica</h1>
            <p>Selecione seu perfil para acessar o sistema</p>
        </div>

        <!-- Cards de seleção -->
        <div class="index-cards">
            <a href="${pageContext.request.contextPath}/escolha-paciente.jsp" class="index-card card-paciente" id="card-paciente">
                <span class="card-icon">👤</span>
                <div class="card-title">Sou Paciente</div>
                <div class="card-desc">Agende consultas, acompanhe seus atendimentos e gerencie sua saúde</div>
            </a>

            <a href="${pageContext.request.contextPath}/escolha-medico.jsp" class="index-card card-medico" id="card-medico">
                <span class="card-icon">🩺</span>
                <div class="card-title">Sou Médico</div>
                <div class="card-desc">Gerencie sua agenda, disponibilidade e atendimentos profissionais</div>
            </a>
        </div>
    </div>
</body>
</html>
