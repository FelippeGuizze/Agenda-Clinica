<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Paciente - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="form-container">
        <h1 style="font-size: 2.5em; margin-bottom: 10px;">Bem-vindo, ${sessionScope.usuarioNome}!</h1>
        <p style="color: #aaa; margin-bottom: 30px;">Email: ${sessionScope.usuarioEmail}</p>

        <c:if test="${not empty sucesso}">
            <div class="sucesso">
                <strong>✓</strong> ${sucesso}
            </div>
        </c:if>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 30px;">
            <a href="${pageContext.request.contextPath}/agendar-atendimento.jsp" 
               style="display: block; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 8px; text-decoration: none; text-align: center; color: white; font-weight: 600; transition: all 0.3s ease;">
                📅 Agendar Atendimento
            </a>

            <a href="#" 
               style="display: block; padding: 20px; background: linear-gradient(135deg, #00d4ff 0%, #0099ff 100%); border-radius: 8px; text-decoration: none; text-align: center; color: white; font-weight: 600; transition: all 0.3s ease;">
                📋 Meus Agendamentos
            </a>
        </div>

        <a href="${pageContext.request.contextPath}/LogoutServlet" 
           style="display: inline-block; padding: 12px 30px; background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%); border: none; border-radius: 8px; color: white; text-decoration: none; font-weight: 600; cursor: pointer; transition: all 0.3s ease;">
            🚪 Logout
        </a>
    </div>
</body>
</html>
