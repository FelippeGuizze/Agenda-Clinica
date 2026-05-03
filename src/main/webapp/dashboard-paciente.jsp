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
            <c:remove var="sucesso" scope="session"/>
        </c:if>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 30px; margin-bottom: 40px; align-items: stretch;">
            <a href="${pageContext.request.contextPath}/agendar-atendimento.jsp" 
               style="display: flex; flex-direction: column; align-items: center; justify-content: center; box-sizing: border-box; min-height: 120px; padding: 25px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 12px; text-decoration: none; text-align: center; color: white; font-weight: 600; font-size: 1.2em; transition: all 0.3s ease; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);"
               onmouseover="this.style.transform='translateY(-5px)'; this.style.boxShadow='0 8px 25px rgba(102, 126, 234, 0.6)';"
               onmouseout="this.style.transform='translateY(0)'; this.style.boxShadow='0 4px 15px rgba(102, 126, 234, 0.4)';">
                <span style="font-size: 2em; margin-bottom: 10px;">📅</span>
                Agendar Atendimento
            </a>

            <a href="${pageContext.request.contextPath}/minhas-consultas.jsp" 
               style="display: flex; flex-direction: column; align-items: center; justify-content: center; box-sizing: border-box; min-height: 120px; padding: 25px; background: linear-gradient(135deg, #00d4ff 0%, #0099ff 100%); border-radius: 12px; text-decoration: none; text-align: center; color: white; font-weight: 600; font-size: 1.2em; transition: all 0.3s ease; box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4);"
               onmouseover="this.style.transform='translateY(-5px)'; this.style.boxShadow='0 8px 25px rgba(0, 212, 255, 0.6)';"
               onmouseout="this.style.transform='translateY(0)'; this.style.boxShadow='0 4px 15px rgba(0, 212, 255, 0.4)';">
                <span style="font-size: 2em; margin-bottom: 10px;">📋</span>
                Minhas Consultas e Custos
            </a>
        </div>

        <a href="${pageContext.request.contextPath}/LogoutServlet" 
           style="display: inline-block; padding: 12px 30px; background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%); border: none; border-radius: 8px; color: white; text-decoration: none; font-weight: 600; cursor: pointer; transition: all 0.3s ease;">
            🚪 Logout
        </a>
    </div>
</body>
</html>