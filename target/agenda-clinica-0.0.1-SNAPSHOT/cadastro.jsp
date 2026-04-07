<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="form-container">
        <h1 style="font-size: 2em; margin-bottom: 30px;">Criar Conta</h1>
        
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

        <form method="POST" action="${pageContext.request.contextPath}/CadastroServlet">
            <div class="form-group">
                <label for="nome">Nome Completo:</label>
                <input type="text" id="nome" name="nome" required>
            </div>

            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
                <small id="emailStatus"></small>
            </div>

            <div class="form-group">
                <label for="senha">Senha:</label>
                <input type="password" id="senha" name="senha" required>
            </div>

            <div class="form-group">
                <label for="categoria">Tipo de Usuário:</label>
                <select id="categoria" name="categoria" required>
                    <option value="">-- Selecione --</option>
                    <option value="1">Paciente</option>
                    <option value="2">Profissional/Médico</option>
                </select>
            </div>

            <div class="form-group" id="contato-group" style="display: none;">
                <label for="contato">Contato (Telefone/WhatsApp):</label>
                <input type="text" id="contato" name="contato" placeholder="(11) 98765-4321">
            </div>

            <div class="form-group" id="especialidade-group" style="display: none;">
                <label for="especialidade">Especialidade:</label>
                <input type="text" id="especialidade" name="especialidade" placeholder="Ex: Cardiologia, Dermatologia">
            </div>

            <button type="submit" class="btn-submit">Criar Conta</button>
        </form>

        <p style="margin-top: 20px; color: #aaa;">
            Já tem conta? <a href="${pageContext.request.contextPath}/login.jsp" style="color: #00d4ff; text-decoration: none; font-weight: 600;">Faça login aqui</a>
        </p>

        <a href="${pageContext.request.contextPath}/index.jsp" class="voltar-link">Voltar para início</a>
    </div>

    <script>
        const categoriaSelect = document.getElementById('categoria');
        const contatoGroup = document.getElementById('contato-group');
        const especialidadeGroup = document.getElementById('especialidade-group');
        const contatoInput = document.getElementById('contato');
        const especialidadeInput = document.getElementById('especialidade');

        categoriaSelect.addEventListener('change', function() {
            if (this.value === '1') {
                // Paciente
                contatoGroup.style.display = 'block';
                especialidadeGroup.style.display = 'none';
                contatoInput.required = true;
                especialidadeInput.required = false;
                especialidadeInput.value = '';
            } else if (this.value === '2') {
                // Médico/Profissional
                contatoGroup.style.display = 'none';
                especialidadeGroup.style.display = 'block';
                contatoInput.required = false;
                especialidadeInput.required = true;
                contatoInput.value = '';
            } else {
                contatoGroup.style.display = 'none';
                especialidadeGroup.style.display = 'none';
                contatoInput.required = false;
                especialidadeInput.required = false;
            }
        });
    </script>
</body>
</html>
