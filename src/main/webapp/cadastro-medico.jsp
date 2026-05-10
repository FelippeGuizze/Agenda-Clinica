<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro Médico - Agenda Clínica</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"/>
</head>
<body>
    <div class="form-container">
        <div class="login-badge badge-medico">🩺 Cadastro de Médico</div>
        <h1 style="font-size: 2em; margin-bottom: 30px;">Criar Conta — Profissional</h1>
        
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
            <input type="hidden" name="categoria" value="2">
            <input type="hidden" name="origemCadastro" value="medico">

            <div style="margin-bottom: 20px; padding: 12px; background: rgba(0, 212, 255, 0.08); border-radius: 5px; border-left: 4px solid #00d4ff;">
                <strong>👤 Nome do Médico:</strong> Seu nome será carregado automaticamente a partir da pré-autorização do seu CRM realizada pelo administrador.
            </div>
            
            <%-- O nome será sobrescrito pelo backend usando o nome do CrmAutorizado --%>
            <input type="hidden" name="nome" value="A ser definido">

            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>
                <small id="emailStatus"></small>
            </div>

            <div class="form-group" style="position: relative;">
                <label for="senha">Senha:</label>
                <input type="password" id="senha" name="senha" required style="padding-right: 40px;">
                <button type="button" onclick="toggleSenha('senha', 'eyeIconSenha')" style="position: absolute; right: 10px; top: 38px; background: none; border: none; cursor: pointer; color: #aaa; font-size: 1.2em;">
                    <span id="eyeIconSenha">👁</span>
                </button>
            </div>

            <div class="form-group" style="position: relative;">
                <label for="confirmar_senha">Confirme a Senha:</label>
                <input type="password" id="confirmar_senha" name="confirmar_senha" required style="padding-right: 40px;">
                <button type="button" onclick="toggleSenha('confirmar_senha', 'eyeIconConfirm')" style="position: absolute; right: 10px; top: 38px; background: none; border: none; cursor: pointer; color: #aaa; font-size: 1.2em;">
                    <span id="eyeIconConfirm">👁</span>
                </button>
            </div>

            <div style="margin-bottom: 20px; padding: 12px; background: rgba(102, 126, 234, 0.1); border-radius: 5px; border-left: 4px solid #667eea;">
                <strong>ℹ️ Importante:</strong> Para se cadastrar como médico, seu CRM e email devem estar previamente autorizados pelo administrador do sistema.
            </div>

            <div style="margin-bottom: 20px; padding: 12px; background: rgba(0, 212, 255, 0.08); border-radius: 5px; border-left: 4px solid #00d4ff;">
                <strong>🏥 Tipo de Atendimento:</strong> O tipo de atendimento que você poderá oferecer (Consulta ou Exame) é definido pelo administrador no momento da autorização do seu CRM. Você não precisa escolher — será atribuído automaticamente.
            </div>

            <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 15px;">
                <div class="form-group" style="margin-bottom: 0;">
                    <label for="crm_numero">Número CRM:</label>
                    <input type="text" id="crm_numero" name="crm_numero" required
                           maxlength="6" placeholder="123456"
                           oninput="this.value = this.value.replace(/\D/g, '')"
                           style="text-align: center; font-size: 1.1em; letter-spacing: 2px;">
                </div>

                <div class="form-group" style="margin-bottom: 0;">
                    <label for="crm_uf">UF:</label>
                    <select id="crm_uf" name="crm_uf" required style="font-size: 1.1em; text-align: center;">
                        <option value="" disabled selected>UF</option>
                        <option value="AC">AC</option>
                        <option value="AL">AL</option>
                        <option value="AP">AP</option>
                        <option value="AM">AM</option>
                        <option value="BA">BA</option>
                        <option value="CE">CE</option>
                        <option value="DF">DF</option>
                        <option value="ES">ES</option>
                        <option value="GO">GO</option>
                        <option value="MA">MA</option>
                        <option value="MT">MT</option>
                        <option value="MS">MS</option>
                        <option value="MG">MG</option>
                        <option value="PA">PA</option>
                        <option value="PB">PB</option>
                        <option value="PR">PR</option>
                        <option value="PE">PE</option>
                        <option value="PI">PI</option>
                        <option value="RJ">RJ</option>
                        <option value="RN">RN</option>
                        <option value="RS">RS</option>
                        <option value="RO">RO</option>
                        <option value="RR">RR</option>
                        <option value="SC">SC</option>
                        <option value="SP">SP</option>
                        <option value="SE">SE</option>
                        <option value="TO">TO</option>
                    </select>
                </div>
            </div>

            <%-- Especialidade como campo hidden com valor padrão: o nicho real é herdado do CrmAutorizado --%>
            <input type="hidden" name="especialidade" value="A definir">

            <button type="submit" class="btn-submit">Criar Conta</button>
        </form>

        <p style="margin-top: 20px; color: #aaa;">
            Já tem conta? <a href="${pageContext.request.contextPath}/login-medico.jsp" style="color: #667eea; text-decoration: none; font-weight: 600;">Faça login aqui</a>
        </p>

        <a href="${pageContext.request.contextPath}/escolha-medico.jsp" class="voltar-link">← Voltar</a>
    </div>

    <script>
        function toggleSenha(inputId, iconId) {
            const input = document.getElementById(inputId);
            const icon = document.getElementById(iconId);
            if (input.type === "password") {
                input.type = "text";
                icon.innerText = "🙈";
            } else {
                input.type = "password";
                icon.innerText = "👁";
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
