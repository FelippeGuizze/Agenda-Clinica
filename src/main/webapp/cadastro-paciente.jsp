<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro Paciente - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="form-container">
        <div class="login-badge badge-paciente">👤 Cadastro de Paciente</div>
        <h1 style="font-size: 2em; margin-bottom: 30px;">Criar Conta — Paciente</h1>
        
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

        <form method="POST" action="${pageContext.request.contextPath}/CadastroServlet" onsubmit="return validarFormulario(event)">
            <input type="hidden" name="categoria" value="1">
            <input type="hidden" name="origemCadastro" value="paciente">

            <div class="form-group">
                <label for="nome">Nome Completo:</label>
                <input type="text" id="nome" name="nome" required>
            </div>

            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
                <small id="emailStatus"></small>
            </div>

            <div class="form-group" style="position: relative;">
                <label for="senha">Senha:</label>
                <input type="password" id="senha" name="senha" required style="padding-right: 40px;">
                <button type="button" onclick="toggleSenha('senha', 'eyeIconSenha')" style="position: absolute; right: 10px; top: 38px; background: none; border: none; cursor: pointer; color: #aaa; font-size: 1.2em;">
                    <span id="eyeIconSenha">👁️</span>
                </button>
            </div>

            <div class="form-group" style="position: relative;">
                <label for="confirmar_senha">Confirme a Senha:</label>
                <input type="password" id="confirmar_senha" name="confirmar_senha" required style="padding-right: 40px;">
                <button type="button" onclick="toggleSenha('confirmar_senha', 'eyeIconConfirm')" style="position: absolute; right: 10px; top: 38px; background: none; border: none; cursor: pointer; color: #aaa; font-size: 1.2em;">
                    <span id="eyeIconConfirm">👁️</span>
                </button>
            </div>

            <div class="form-group">
                <label for="contato">Contato (Telefone/WhatsApp):</label>
                <input type="text" id="contato" name="contato" placeholder="(11) 98765-4321" oninput="mascaraTelefone(this)" maxlength="15" minlength="15" pattern="^\(\d{2}\) \d{5}-\d{4}$" required>
            </div>

            <button type="submit" class="btn-submit">Criar Conta</button>
        </form>

        <p style="margin-top: 20px; color: #aaa;">
            Já tem conta? <a href="${pageContext.request.contextPath}/login-paciente.jsp" style="color: #00d4ff; text-decoration: none; font-weight: 600;">Faça login aqui</a>
        </p>

        <a href="${pageContext.request.contextPath}/escolha-paciente.jsp" class="voltar-link">← Voltar</a>
    </div>

    <script>
        function mascaraTelefone(input) {
            let v = input.value.replace(/\D/g, '');
            if (v.length > 11) v = v.slice(0, 11);
            if (v.length > 2) {
                v = '(' + v.substring(0, 2) + ') ' + v.substring(2);
            }
            if (v.length > 10) {
                v = v.substring(0, 10) + '-' + v.substring(10);
            }
            input.value = v;
        }

        function toggleSenha(inputId, iconId) {
            const input = document.getElementById(inputId);
            const icon = document.getElementById(iconId);
            if (input.type === "password") {
                input.type = "text";
                icon.innerText = "🙈";
            } else {
                input.type = "password";
                icon.innerText = "👁️";
            }
        }

        function validarFormulario(event) {
            const senha = document.getElementById('senha').value;
            const confirmar_senha = document.getElementById('confirmar_senha').value;
            if (senha !== confirmar_senha) {
                alert('As senhas não coincidem!');
                event.preventDefault();
                return false;
            }
            return true;
        }
    </script>
</body>
</html>
