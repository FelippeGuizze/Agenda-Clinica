<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Admin - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="form-container">
        <h1 style="font-size: 2.5em; margin-bottom: 10px;">Painel Administrativo</h1>
        <p style="color: #aaa; margin-bottom: 30px;">Email: ${sessionScope.usuarioEmail}</p>

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

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 30px; align-items: stretch;">
            <a href="${pageContext.request.contextPath}/admin-usuarios.jsp" 
               style="display: flex; align-items: center; justify-content: center; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 8px; text-decoration: none; text-align: center; color: white; font-weight: 600; transition: all 0.3s ease; box-sizing: border-box; min-height: 80px;">
                👥 Gerenciar Usuários
            </a>

            <a href="${pageContext.request.contextPath}/admin-relatorios.jsp" 
               style="display: flex; align-items: center; justify-content: center; padding: 20px; background: linear-gradient(135deg, #00d4ff 0%, #0099ff 100%); border-radius: 8px; text-decoration: none; text-align: center; color: white; font-weight: 600; transition: all 0.3s ease; box-sizing: border-box; min-height: 80px;">
                📊 Relatórios Financeiros
            </a>
            
            <a href="${pageContext.request.contextPath}/BackupDatabaseServlet" 
               style="display: flex; align-items: center; justify-content: center; padding: 20px; background: linear-gradient(135deg, #ffca28 0%, #ffc107 100%); border-radius: 8px; text-decoration: none; text-align: center; color: white; font-weight: 600; transition: all 0.3s ease; box-sizing: border-box; min-height: 80px;">
                💾 Backup Database (JSON)
            </a>
            
            <a href="javascript:void(0);" onclick="document.getElementById('backupFileInput').click();"
               style="display: flex; align-items: center; justify-content: center; padding: 20px; background: linear-gradient(135deg, #f6d365 0%, #fda085 100%); border-radius: 8px; text-decoration: none; text-align: center; color: white; font-weight: 600; text-shadow: 1px 1px 2px rgba(0,0,0,0.2); transition: all 0.3s ease; box-sizing: border-box; min-height: 80px;">
                📥 Inserir Backup
            </a>
        </div>
        
        <!-- Formulário Oculto para Restore -->
        <form id="restoreForm" method="POST" action="${pageContext.request.contextPath}/RestaurarDatabaseServlet" enctype="multipart/form-data" style="display:none;">
            <input type="file" id="backupFileInput" name="backupFile" accept=".json" onchange="document.getElementById('restoreForm').submit();">
        </form>

        <a href="${pageContext.request.contextPath}/LogoutServlet" 
           style="display: inline-block; padding: 12px 30px; background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%); border: none; border-radius: 8px; color: white; text-decoration: none; font-weight: 600; cursor: pointer; transition: all 0.3s ease;">
            🚪 Logout
        </a>
    </div>
</body>
</html>
